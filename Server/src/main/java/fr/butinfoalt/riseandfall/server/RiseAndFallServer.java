package fr.butinfoalt.riseandfall.server;

import com.mysql.cj.jdbc.Driver;
import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.server.BaseSocketServer;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static fr.butinfoalt.riseandfall.server.Environment.*;

/**
 * Classe principale du serveur de jeu Rise and Fall.
 * Elle étend la classe BaseSocketServer pour gérer les connexions des clients.
 * Elle initialise également la base de données et charge les données du serveur.
 */
public class RiseAndFallServer extends BaseSocketServer {
    /**
     * La connexion à la base de données.
     */
    private final Connection db;
    /**
     * Le gestionnaire d'authentification pour gérer les connexions des clients.
     */
    private final AuthenticationManager authManager;

    /**
     * Le gestionnaire de jeu pour gérer les parties en attente, en cours ou terminées.
     */
    private GameManager gameManager;

    /**
     * Le gestionnaire de jeu pour gérer les utilisateurs et les joueurs.
     */
    private UserManager userManager;

    /**
     * Constructeur de la classe BaseSocketServer.
     * Initialise le serveur socket sur le port spécifié.
     *
     * @param port Le port sur lequel le serveur écoute les connexions des clients.
     * @param db   La connexion à la base de données.
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
        this.registerReceivePacket((byte) 3, PacketCreateOrJoinGame.class, this.gameManager::onCreateOrJoinGame, PacketCreateOrJoinGame::new);
        this.registerSendPacket((byte) 4, PacketInitialGameData.class);
        this.registerReceivePacket((byte) 5, PacketUpdateOrders.class, this.gameManager::onUpdateOrders, PacketUpdateOrders::new);
        this.registerSendPacket((byte) 6, PacketUpdateGameData.class);
        this.registerReceivePacket((byte) 7, PacketGameAction.class, this::onGameAction, PacketGameAction::new);
        this.registerSendPacket((byte) 8, PacketError.class);
        this.registerReceivePacket((byte) 9, PacketRegister.class, this.authManager::onRegister, PacketRegister::new);
    }

    /**
     * Méthode pour charger les données du serveur depuis la base de données.
     * Elle récupère les races, les types de bâtiments et les types d'unités.
     */
    private void loadServerData() {
        try {
            Race[] races;
            User[] users;
            ServerPlayer[] players;
            ServerGame[] games;

            List<BuildingType> buildingTypes = new ArrayList<>();
            List<UnitType> unitTypes = new ArrayList<>();

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description, gold_multiplier, intelligence_multiplier, damage_multiplier, health_multiplier FROM race ORDER BY id")) {
                List<Race> racesList = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    float goldMultiplier = set.getFloat("gold_multiplier");
                    float intelligenceMultiplier = set.getFloat("intelligence_multiplier");
                    float damageMultiplier = set.getFloat("damage_multiplier");
                    float healthMultiplier = set.getFloat("health_multiplier");
                    racesList.add(new Race(id, name, description, goldMultiplier, intelligenceMultiplier, damageMultiplier, healthMultiplier));
                }
                races = racesList.toArray(new Race[0]);
            }

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description, price_gold, price_intelligence, gold_production, intelligence_production, max_units, initial_amount, accessible_race_id FROM building_type ORDER BY id")) {
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    int priceGold = set.getInt("price_gold");
                    int priceIntelligence = set.getInt("price_intelligence");
                    int goldProduction = set.getInt("gold_production");
                    int intelligenceProduction = set.getInt("intelligence_production");
                    int maxUnits = set.getInt("max_units");
                    int initialAmount = set.getInt("initial_amount");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : Identifiable.getById(races, accessibleRaceId);
                    buildingTypes.add(new BuildingType(id, name, description, priceGold, priceIntelligence, goldProduction, intelligenceProduction, maxUnits, initialAmount, accessibleRace));
                }
            }

            try (PreparedStatement statement = this.db.prepareStatement("SELECT id, name, description, price_gold, price_intelligence, health, damage, accessible_race_id FROM unit_type ORDER BY id")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    int priceGold = set.getInt("price_gold");
                    int priceIntelligence = set.getInt("price_intelligence");
                    int health = set.getInt("health");
                    int damage = set.getInt("damage");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : Identifiable.getById(races, accessibleRaceId);
                    unitTypes.add(new UnitType(id, name, description, priceGold, priceIntelligence, health, damage, accessibleRace));
                }
            }

            try (PreparedStatement statement = this.db.prepareStatement("SELECT * FROM game")) {
                List<ServerGame> gameList = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    int turnInterval = set.getInt("turn_interval");
                    int minPlayers = set.getInt("min_players");
                    int maxPlayers = set.getInt("max_players");
                    boolean isPrivate = false;
                    GameState state = GameState.valueOf(set.getString("state"));
                    gameList.add(new ServerGame(id, name, turnInterval, minPlayers, maxPlayers, isPrivate, state, null, 0, new HashMap<>()));
                }
                games = gameList.toArray(new ServerGame[0]);
                this.gameManager = new GameManager(this, new HashSet<>(gameList));
            }
            try (PreparedStatement statement = this.db.prepareStatement("SELECT * FROM `user`")) {
                List<User> userList = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String username = set.getString("username");
                    userList.add(new User(id, username));
                }
                users = userList.toArray(new User[0]);
            }
            ServerData.init(List.of(races), buildingTypes, unitTypes, List.of(games));

            try (PreparedStatement statement = this.db.prepareStatement("SELECT * FROM `player`")) {
                List<ServerPlayer> playerList = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    User user = Identifiable.getById(users, set.getInt("user_id"));
                    ServerGame game = Identifiable.getById(games, set.getInt("game_id"));
                    Race race = Identifiable.getById(races, set.getInt("race_id"));
                    int gold = set.getInt("gold");
                    int intelligence = set.getInt("intelligence");
                    ServerPlayer player = new ServerPlayer(id, user, game, race);
                    player.setGoldAmount(gold);
                    player.setIntelligence(intelligence);
                    playerList.add(player);
                    game.addPlayer(player);
                }
                players = playerList.toArray(new ServerPlayer[0]);
            }
            this.userManager = new UserManager(this, new HashSet<>(Arrays.asList(users)), new HashSet<>(Arrays.asList(players)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode appelée lorsqu'un client se connecte au serveur.
     * Elle envoie les données statiques du serveur au client via le PacketServerData.
     *
     * @param client Le wrapper de socket du client connecté.
     */
    @Override
    public void onClientConnected(SocketWrapper client) {
        super.onClientConnected(client);
        System.out.println("Client connecté : " + client.getName());
        try {
            client.sendPacket(new PacketServerData(
                    ServerData.getRaces().toArray(new Race[0]),
                    ServerData.getUnitTypes().toArray(new UnitType[0]),
                    ServerData.getBuildingTypes().toArray(new BuildingType[0]),
                    ServerData.getGames().toArray(new Game[0])
            ));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi des données du serveur au client : " + e.getMessage());
            try {
                client.close();
            } catch (IOException ignored) {
                System.err.println("Erreur lors de la fermeture de la connexion du client : " + client.getName());
            }
        }
    }

    /**
     * Méthode appelée lorsqu'un client se déconnecte du serveur.
     *
     * @param client Le wrapper de socket du client déconnecté.
     */
    @Override
    protected void onClientDisconnected(SocketWrapper client) {
        super.onClientDisconnected(client);
        this.gameManager.onClientDisconnected(client);
        this.authManager.onClientDisconnected(client);
        System.out.println("Client déconnecté : " + client.getName());
    }

    /**
     * Méthode appelée lors de la réception d'un paquet de type PacketGameAction.
     * Elle gère les actions du jeu telles que la déconnexion du client ou le passage au tour suivant.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de jeu reçu.
     */
    private void onGameAction(SocketWrapper sender, PacketGameAction packet) {
        switch (packet.getAction()) {
            case QUIT_GAME -> this.gameManager.onClientQuitGame(sender);
            case LOG_OUT -> {
                this.gameManager.onClientDisconnected(sender);
                this.authManager.onClientDisconnected(sender);
            }
            case NEXT_TURN -> this.gameManager.onNextTurn(sender);
        }
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

    /**
     * Méthode pour obtenir le gestionnaire de jeu.
     *
     * @return Le gestionnaire de jeu.
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * Méthode pour obtenir le gestionnaire d'utilisateurs.
     *
     * @return Le gestionnaire d'utilisateurs.
     */
    public UserManager getUserManager() {
        return this.userManager;
    }


    /**
     * Méthode pour charger le driver MySQL.
     * Elle lève une exception si le driver n'est pas trouvé.
     */
    private static void loadMysqlDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable.", e);
        }
    }

    /**
     * Méthode pour établir une connexion à la base de données.
     *
     * @return La connexion à la base de données.
     */
    private static Connection connectToDatabase() {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";

        try {
            Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            if (conn.isValid(2)) {
                System.out.println("Connexion à la base de données réussie !");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base.", e);
        }
    }

    /**
     * Méthode principale pour démarrer le serveur.
     * Elle charge le driver MySQL, établit la connexion à la base de données,
     * démarre le serveur et gère les tâches de fermeture.
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        ArrayList<Runnable> shutdownTasks = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Runnable task : shutdownTasks) {
                task.run();
            }
        }));

        loadMysqlDriver();
        Connection db = connectToDatabase();
        shutdownTasks.add(() -> {
            System.out.println("Fermeture de la connexion à la base de données...");
            try {
                db.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        try (RiseAndFallServer server = new RiseAndFallServer(SERVER_PORT, db)) {
            server.start();
            shutdownTasks.addFirst(() -> {
                System.out.println("Arrêt du serveur...");
                try {
                    server.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Serveur démarré sur le port " + SERVER_PORT);
            try {
                server.join();
            } catch (InterruptedException ignored) {
            }
            System.out.println("Serveur arrêté.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
