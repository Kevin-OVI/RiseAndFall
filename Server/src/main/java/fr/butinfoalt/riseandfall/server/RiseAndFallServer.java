package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.server.BaseSocketServer;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static fr.butinfoalt.riseandfall.server.Environment.SERVER_PORT;

/**
 * Classe principale du serveur de jeu Rise and Fall.
 * Elle étend la classe BaseSocketServer pour gérer les connexions des clients.
 * Elle initialise également la base de données et charge les données du serveur.
 */
public class RiseAndFallServer extends BaseSocketServer {
    /**
     * Le gestionnaire de base de données pour interagir avec la base de données du serveur.
     */
    private final DatabaseManager databaseManager;

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
     * Timer pour gérer les tâches périodiques du serveur.
     * Il peut être utilisé pour gérer les tours de jeu, le démarrage de parties, etc.
     */
    private final Timer timer = new Timer();

    /**
     * Désérialiseur de données spécifique au serveur.
     * Il est utilisé pour désérialiser les données côté serveur.
     */
    private final ServerDataDeserializer dataDeserializer = new ServerDataDeserializer(this);

    /**
     * Constructeur de la classe BaseSocketServer.
     * Initialise le serveur socket sur le port spécifié.
     *
     * @param port            Le port sur lequel le serveur écoute les connexions des clients.
     * @param databaseManager Le gestionnaire de base de données pour interagir avec la base de données du serveur.
     * @throws IOException Si une erreur se produit lors de la création du serveur socket.
     */
    public RiseAndFallServer(int port, DatabaseManager databaseManager) throws IOException {
        super(port);
        this.databaseManager = databaseManager;
        this.authManager = new AuthenticationManager(this);
        this.loadServerData();

        this.registerReceivePacket((byte) 0, PacketAuthentification.class, this.authManager::onAuthentification, PacketAuthentification::new);
        this.registerSendAndReceivePacket((byte) 1, PacketToken.class, this.authManager::onTokenAuthentification, PacketToken::new);
        this.registerSendPacket((byte) 2, PacketServerData.class);
        this.registerReceivePacket((byte) 3, PacketCreateOrJoinGame.class, this.gameManager::onCreateOrJoinGame, PacketCreateOrJoinGame::new);
        this.registerSendPacket((byte) 4, PacketJoinedGame.class);
        this.registerReceivePacket((byte) 5, PacketUpdateOrders.class, this.gameManager::onUpdateOrders);
        this.registerSendPacket((byte) 6, PacketUpdateGameData.class);
        this.registerSendAndReceivePacket((byte) 7, PacketGameAction.class, this::onGameAction, PacketGameAction::new);
        this.registerSendPacket((byte) 8, PacketError.class);
        this.registerReceivePacket((byte) 9, PacketRegister.class, this.authManager::onRegister, PacketRegister::new);
        this.registerSendPacket((byte) 10, PacketWaitingGames.class);
        this.registerSendPacket((byte) 11, PacketDiscoverPlayer.class);
        this.registerSendPacket((byte) 15, PacketTurnResults.class);
    }

    /**
     * Méthode pour charger les données du serveur depuis la base de données.
     * Elle récupère les races, les types de bâtiments et les types d'unités.
     */
    private void loadServerData() {
        try {
            List<Race> races = new ArrayList<>();
            List<User> users = new ArrayList<>();
            List<ServerPlayer> players = new ArrayList<>();
            List<ServerGame> games = new ArrayList<>();

            List<BuildingType> buildingTypes = new ArrayList<>();
            List<UnitType> unitTypes = new ArrayList<>();

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM race ORDER BY id")) {

                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    float goldMultiplier = set.getFloat("gold_multiplier");
                    float intelligenceMultiplier = set.getFloat("intelligence_multiplier");
                    float damageMultiplier = set.getFloat("damage_multiplier");
                    float healthMultiplier = set.getFloat("health_multiplier");
                    races.add(new Race(id, name, description, goldMultiplier, intelligenceMultiplier, damageMultiplier, healthMultiplier));
                }
            }

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM building_type ORDER BY id")) {
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    float price = set.getFloat("price");
                    float requiredIntelligence = set.getFloat("required_intelligence");
                    float goldProduction = set.getFloat("gold_production");
                    float intelligenceProduction = set.getFloat("intelligence_production");
                    float resistance = set.getFloat("resistance");
                    int maxUnits = set.getInt("max_units");
                    int initialAmount = set.getInt("initial_amount");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : Identifiable.getById(races, accessibleRaceId);
                    boolean defensive = set.getBoolean("defensive");
                    buildingTypes.add(new BuildingType(id, name, description, price, requiredIntelligence, goldProduction, intelligenceProduction, resistance, maxUnits, initialAmount, accessibleRace, defensive));
                }
            }

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM unit_type ORDER BY id")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    float price = set.getFloat("price");
                    float requiredIntelligence = set.getFloat("required_intelligence");
                    float health = set.getFloat("health");
                    float damage = set.getFloat("damage");
                    int accessibleRaceId = set.getInt("accessible_race_id");
                    Race accessibleRace = set.wasNull() ? null : Identifiable.getById(races, accessibleRaceId);
                    unitTypes.add(new UnitType(id, name, description, price, requiredIntelligence, health, damage, accessibleRace));
                }
            }
            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM game")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    int turnInterval = set.getInt("turn_interval");
                    int currentTurn = set.getInt("current_turn");
                    int minPlayers = set.getInt("min_players");
                    int maxPlayers = set.getInt("max_players");
                    boolean isPrivate = set.getString("password_hash") != null;
                    GameState state = GameState.valueOf(set.getString("state"));
                    Timestamp nextActionAt = set.getTimestamp("next_action_at");
                    games.add(new ServerGame(this, id, name, turnInterval, minPlayers, maxPlayers, isPrivate, state, nextActionAt, currentTurn));
                }
            }
            // Nécessaire pour charger les joueurs juste après
            ServerData.init(races, buildingTypes, unitTypes);
            this.gameManager = new GameManager(this, games);

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM `user`")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    String username = set.getString("username");
                    users.add(new User(id, username));
                }
            }
            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM `player`")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = set.getInt("id");
                    User user = Identifiable.getById(users, set.getInt("user_id"));
                    ServerGame game = Identifiable.getById(games, set.getInt("game_id"));
                    Race race = Identifiable.getById(races, set.getInt("race_id"));
                    float gold = set.getFloat("gold");
                    float intelligence = set.getFloat("intelligence");
                    int eliminationTurn = set.getInt("elimination_turn");
                    if (set.wasNull()) {
                        eliminationTurn = -1; // -1 signifie que le joueur n'est pas éliminé
                    }
                    ServerPlayer player = new ServerPlayer(id, user, game, race, gold, intelligence, eliminationTurn);
                    players.add(player);
                    // Ajout forcé car la partie peut avoir déjà démarré, mais on est dans un cas particulier car les données ne sont pas encore chargées
                    game.forceAddPlayer(player);
                }
            }
            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM player_building")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int playerId = set.getInt("player_id");
                    BuildingType buildingType = Identifiable.getById(buildingTypes, set.getInt("building_id"));
                    int amount = set.getInt("quantity");
                    ServerPlayer player = Identifiable.getById(players, playerId);
                    player.getBuildingMap().set(buildingType, amount);
                }
            }

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM player_unit")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int playerId = set.getInt("player_id");
                    UnitType unitType = Identifiable.getById(unitTypes, set.getInt("unit_id"));
                    int amount = set.getInt("quantity");
                    ServerPlayer player = Identifiable.getById(players, playerId);
                    player.getUnitMap().set(unitType, amount);
                }
            }

            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM building_creation_order")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int playerId = set.getInt("player_id");
                    BuildingType buildingType = Identifiable.getById(buildingTypes, set.getInt("building_type_id"));
                    int amount = set.getInt("amount");
                    ServerPlayer player = Identifiable.getById(players, playerId);
                    player.getPendingBuildingsCreation().set(buildingType, amount);
                }
            }
            try (PreparedStatement statement = this.getDb().prepareStatement("SELECT * FROM unit_creation_order")) {
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int playerId = set.getInt("player_id");
                    UnitType unitType = Identifiable.getById(unitTypes, set.getInt("unit_type_id"));
                    int amount = set.getInt("amount");
                    ServerPlayer player = Identifiable.getById(players, playerId);
                    player.getPendingUnitsCreation().set(unitType, amount);
                }
            }
            try (PreparedStatement attackStatement = this.getDb().prepareStatement("SELECT * FROM attack_player_order")) {
                ResultSet set = attackStatement.executeQuery();
                while (set.next()) {
                    int playerId = set.getInt("player_id");
                    int targetPlayerId = set.getInt("target_player_id");
                    int orderId = set.getInt("id");
                    ServerPlayer player = Identifiable.getById(players, playerId);
                    ServerPlayer targetPlayer = Identifiable.getById(players, targetPlayerId);
                    ObjectIntMap<UnitType> usingUnits = player.getUnitMap().createEmptyClone();
                    try (PreparedStatement unitStatement = this.getDb().prepareStatement("SELECT * FROM attack_player_order_unit WHERE order_id = ?")) {
                        unitStatement.setInt(1, orderId);
                        ResultSet unitSet = unitStatement.executeQuery();
                        while (unitSet.next()) {
                            UnitType unitType = Identifiable.getById(unitTypes, unitSet.getInt("unit_type_id"));
                            usingUnits.set(unitType, unitSet.getInt("amount"));
                        }
                    }
                    player.getPendingAttacks().add(new AttackPlayerOrderData(targetPlayer, usingUnits));
                }
            }

            // Redémarrage des actions en attente
            for (ServerGame game : games) {
                switch (game.getState()) {
                    case WAITING -> {
                        if (game.hasSufficientPlayers()) {
                            game.scheduleGameStart();
                        }
                    }
                    case RUNNING -> game.scheduleNextTurn();
                }
            }
            this.userManager = new UserManager(this, users, players);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des données initiales", e);
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
        LogManager.logMessage("Client connecté : " + client.getName());
        try {
            client.sendPacket(new PacketServerData(
                    ServerData.getRaces(),
                    ServerData.getUnitTypes(),
                    ServerData.getBuildingTypes()
            ));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi des données du serveur au client :", e);
            try {
                client.close();
            } catch (IOException ignored) {
                LogManager.logError("Erreur lors de la fermeture de la connexion du client :", e);
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
        this.authManager.onClientDisconnected(client);
        LogManager.logMessage("Client déconnecté : " + client.getName());
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
            case LOG_OUT -> this.authManager.onClientDisconnected(sender);
            case NEXT_TURN -> this.gameManager.onNextTurn(sender);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.timer.cancel();
    }

    /**
     * Méthode pour obtenir la connexion à la base de données.
     *
     * @return La connexion à la base de données.
     */
    public Connection getDb() {
        return this.databaseManager.getDb();
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
     * Méthode pour obtenir le timer du serveur.
     *
     * @return Le timer du serveur.
     */
    public Timer getTimer() {
        return this.timer;
    }

    /**
     * Méthode pour obtenir le désérialiseur de données spécifique au serveur.
     *
     * @return Le désérialiseur de données spécifique au serveur.
     */
    public ServerDataDeserializer getDataDeserializer() {
        return this.dataDeserializer;
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

        try (DatabaseManager databaseManager = new DatabaseManager()) {
            shutdownTasks.add(() -> {
                LogManager.logMessage("Fermeture du gestionnaire de base de données...");
                databaseManager.close();
            });
            try (RiseAndFallServer server = new RiseAndFallServer(SERVER_PORT, databaseManager)) {
                server.start();
                shutdownTasks.addFirst(() -> {
                    LogManager.logMessage("Arrêt du serveur...");
                    try {
                        server.close();
                    } catch (IOException e) {
                        LogManager.logError("Erreur lors de l'arrêt du serveur :", e);
                    }
                });
                LogManager.logMessage("Serveur démarré sur le port " + SERVER_PORT);
                try {
                    server.join();
                } catch (InterruptedException e) {
                    LogManager.logError("Le serveur a été interrompu.", e);
                }
                LogManager.logMessage("Serveur arrêté.");
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'exécution du serveur :", e);
            }
        }
    }
}
