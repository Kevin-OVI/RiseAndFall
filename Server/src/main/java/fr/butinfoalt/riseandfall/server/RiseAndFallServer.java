package fr.butinfoalt.riseandfall.server;

import com.mysql.cj.jdbc.Driver;
import fr.butinfoalt.riseandfall.gamelogic.Race;
import fr.butinfoalt.riseandfall.gamelogic.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.network.packets.PacketServerData;
import fr.butinfoalt.riseandfall.network.packets.PacketToken;
import fr.butinfoalt.riseandfall.network.server.BaseSocketServer;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fr.butinfoalt.riseandfall.server.Environment.*;

public class RiseAndFallServer extends BaseSocketServer {
    private final Connection db;
    private final AuthenticationManager authManager;

    /**
     * Constructeur de la classe BaseSocketServer.
     * Initialise le serveur socket sur le port spécifié.
     *
     * @param port Le port sur lequel le serveur écoute les connexions des clients.
     * @param db
     * @throws IOException Si une erreur se produit lors de la création du serveur socket.
     */
    public RiseAndFallServer(int port, Connection db) throws IOException {
        super(port);
        this.db = db;
        this.authManager = new AuthenticationManager(this);
        this.loadServerData();

        this.registerReceivePacket((byte) 0, PacketAuthentification.class, this.authManager::onAuthentification, PacketAuthentification::new);
        this.registerSendAndReceivePacket((byte) 1, PacketToken.class, this.authManager::onTokenAuthentification, PacketToken::new);
        this.registerSendPacket((byte) 2, PacketServerData.class);
    }

    private void loadServerData() {
        try {
            Race[] races;
            List<BuildingType> buildingTypes = new ArrayList<>();
            List<UnitType> unitTypes = new ArrayList<>();

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description FROM race")) {
                List<Race> racesList = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    racesList.add(new Race(id, name, description));
                }
                races = racesList.toArray(new Race[0]);
            }

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description, price, gold_production, intelligence_production, max_units, initial_amount, accessible_race_id FROM building_type")) {
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    int price = set.getInt("price");
                    int goldProduction = set.getInt("gold_production");
                    int intelligenceProduction = set.getInt("intelligence_production");
                    int maxUnits = set.getInt("max_units");
                    int initialAmount = set.getInt("initial_amount");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : ServerData.getRaceByDbId(races, accessibleRaceId);
                    buildingTypes.add(new BuildingType(id, name, description, price, goldProduction, intelligenceProduction, maxUnits, initialAmount, accessibleRace));
                }
            }

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description, price, health, damage, accessible_race_id FROM unit_type")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    int price = set.getInt("price");
                    int health = set.getInt("health");
                    int damage = set.getInt("damage");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : ServerData.getRaceByDbId(races, accessibleRaceId);
                    unitTypes.add(new UnitType(id, name, description, price, health, damage, accessibleRace));
                }
            }

            ServerData.init(races, buildingTypes.toArray(new BuildingType[0]), unitTypes.toArray(new UnitType[0]));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClientConnected(SocketWrapper client) {
        super.onClientConnected(client);
        System.out.println("Client connecté : " + client.getName());
        try {
            client.sendPacket(new PacketServerData(ServerData.getRaces(), ServerData.getUnitTypes(), ServerData.getBuildingTypes()));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi des données du serveur au client : " + e.getMessage());
            try {
                client.close();
            } catch (IOException ignored) {
                System.err.println("Erreur lors de la fermeture de la connexion du client : " + client.getName());
            }
        }
        System.out.println("Données du serveur envoyées au client : " + client.getName());
    }

    @Override
    protected void onClientDisconnected(SocketWrapper client) {
        super.onClientDisconnected(client);
        System.out.println("Client déconnecté : " + client.getName());
    }

    /**
     * Méthode pour obtenir la connexion à la base de données.
     *
     * @return La connexion à la base de données.
     */
    public Connection getDb() {
        return this.db;
    }

    /**
     * Méthode pour obtenir le gestionnaire d'authentification.
     *
     * @return Le gestionnaire d'authentification.
     */
    public AuthenticationManager getAuthManager() {
        return this.authManager;
    }

    private static void loadMysqlDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable.", e);
        }
    }

    private static Connection connectToDatabase() {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";

        try {
            Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            if (conn.isValid(2)) {
                System.out.println("Connexion à la base réussie !");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base.", e);
        }
    }

    public static void main(String[] args) {
        loadMysqlDriver();

        Connection db = connectToDatabase();

        try (RiseAndFallServer server = new RiseAndFallServer(SERVER_PORT, db)) {
            server.start();
            System.out.println("Server started");
            try {
                server.join();
            } catch (InterruptedException e) {
                System.out.println("Server interrupted");
                server.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (!db.isClosed()) {
                    db.close();
                    System.out.println("Connexion fermée proprement.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
