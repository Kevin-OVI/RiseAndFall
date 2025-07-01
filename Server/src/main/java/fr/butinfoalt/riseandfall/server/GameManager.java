package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.packets.PacketError.ErrorType;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.server.data.GameNameGenerator;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.server.orders.AttacksExecutionContext;
import fr.butinfoalt.riseandfall.util.Iterables;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Classe gérant les parties de jeu.
 */
public class GameManager {
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Liste des parties
     */
    private final List<ServerGame> games;

    /**
     * Constructeur de la classe GameManager.
     *
     * @param server Instance du serveur.
     */
    public GameManager(RiseAndFallServer server, List<ServerGame> games) {
        this.server = server;
        this.games = games;

        if (games.stream().noneMatch(game -> game.getState() == GameState.WAITING)) {
            // Si aucune partie n'est en attente, on en crée une nouvelle
            this.newRandomGame();
        }
    }

    /**
     * Récupère la liste des connexions pour un joueur donné.
     *
     * @param player Le joueur dont on veut récupérer les connexions.
     * @return La liste des connexions pour le joueur donné.
     */
    public synchronized List<SocketWrapper> getConnectionsFor(ServerPlayer player) {
        return this.server.getAuthManager().getConnectionsFor(player.getUser());
    }

    /**
     * Récupère la joueur dans une partie en cours pour un utilisateur donné.
     *
     * @param user L'utilisateur pour lequel on veut récupérer le joueur.
     * @return Le joueur dans une partie en cours, ou null si l'utilisateur n'est pas dans une partie en cours.
     */
    public ServerPlayer getPlayerInRunningGame(User user) {
        for (ServerGame game : this.games) {
            ServerPlayer player = game.getPlayerFor(user);
            if (player != null && !player.hasExitedGame()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Récupère le joueur dans une partie en cours pour un client donné.
     *
     * @param client Le client pour lequel on veut récupérer le joueur.
     * @return Le joueur dans une partie en cours, ou null si le client n'est pas authentifié ou n'est pas dans une partie en cours.
     */
    public ServerPlayer getPlayerInRunningGame(SocketWrapper client) {
        User user = this.server.getAuthManager().getUser(client);
        if (user == null) {
            return null;
        }
        return getPlayerInRunningGame(user);
    }

    /**
     * Crée une nouvelle partie et l'enregistre en base de données.
     *
     * @param name Le nom de la partie.
     * @return La nouvelle partie créée.
     */
    public synchronized ServerGame newGame(String name) {
        LogManager.logMessage("Création de la partie : " + name);
        try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO game(name) VALUES (?) RETURNING id, turn_interval, current_turn, min_players, max_players, state, password_hash IS NOT NULL as is_private, state, next_action_at")) {
            statement.setString(1, name);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                int gameId = resultSet.getInt("id");
                int turnInterval = resultSet.getInt("turn_interval");
                int currentTurn = resultSet.getInt("current_turn");
                int minPlayers = resultSet.getInt("min_players");
                int maxPlayers = resultSet.getInt("max_players");
                boolean isPrivate = resultSet.getBoolean("is_private");
                GameState state = GameState.valueOf(resultSet.getString("state"));
                Timestamp nextActionAt = resultSet.getTimestamp("next_action_at");

                ServerGame game = new ServerGame(this.server, gameId, name, turnInterval, minPlayers, maxPlayers, isPrivate, state, nextActionAt, currentTurn);
                this.games.add(game);
                LogManager.logMessage("Partie créée avec succès : " + name + " (ID: " + gameId + ")");
                return game;
            } else {
                LogManager.logError("Erreur lors de la création de la partie : aucune ligne retournée.");
            }
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la création de la partie " + name + " dans la base de données.", e);
        }
        return null;
    }

    /**
     * Crée une nouvelle partie de jeu avec un nom aléatoire.
     *
     * @return La nouvelle partie créée.
     */
    public ServerGame newRandomGame() {
        String gameName = GameNameGenerator.generateGameName();
        return this.newGame(gameName);
    }

    /**
     * Ajoute un joueur à une partie de jeu à partir d'un utilisateur.
     *
     * @param user L'utiliser pour lequelle créer un joueur dans une partie.
     * @param game La partie à laquelle le joueur doit être ajouté.
     * @param race La race choisie par le joueur.
     * @return Le joueur ajouté à la partie.
     */
    private ServerPlayer addPlayerToGame(User user, ServerGame game, Race race) {
        int playerId;
        try (PreparedStatement statement = server.getDb().prepareStatement(
                "INSERT INTO player (user_id, game_id, race_id) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, user.getId());
            statement.setInt(2, game.getId());
            statement.setInt(3, race.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating player failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    playerId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating player failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            LogManager.logError("Erreur lors de l'ajout du joueur à la partie " + game.getName() + " pour l'utilisateur " + user.getUsername(), e);
            return null;
        }
        ServerPlayer player = new ServerPlayer(playerId, user, game, race);
        this.server.getUserManager().addPlayer(player);
        game.addPlayer(player);
        return player;
    }

    /**
     * Envoie les mises à jour de données du joueur à toutes les connexions associées.
     *
     * @param player Le joueur dont les données doivent être mises à jour.
     */
    public void sendPlayerDataUpdates(ServerPlayer player) {
        List<SocketWrapper> connections = this.getConnectionsFor(player);
        if (!connections.isEmpty()) {
            PacketUpdateGameData packet = new PacketUpdateGameData(player.getGame(), player);
            for (SocketWrapper connection : connections) {
                try {
                    connection.sendPacket(packet);
                } catch (IOException e) {
                    LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des données du joueur " + player.getUser().getUsername() + " à la connexion " + connection.getName(), e);
                }
            }
        }
    }

    /**
     * Envoie un paquet de jointure de partie au client spécifié.
     * Méthode privée utilisée lorsqu'on a déjà récupéré toutes les informations nécessaires pour envoyer le paquet.
     *
     * @param packet Le paquet de jointure de partie à envoyer.
     * @param client Le client qui reçoit le paquet.
     */
    private void sendJoinGamePacket(PacketJoinedGame<ServerGame, ServerPlayer> packet, SocketWrapper client) {
        try {
            client.sendPacket(packet);
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de jointure de partie au client " + client.getName(), e);
        }
    }

    /**
     * Envoie un paquet de jointure de partie au client spécifié.
     *
     * @param connection La connexion du client qui reçoit le paquet.
     * @param user       L'utilisateur pour lequel on envoie le paquet de jointure de partie.
     */
    public void sendJoinGamePacket(SocketWrapper connection, User user) {
        ServerPlayer player = getPlayerInRunningGame(user);
        if (player == null) {
            // Affichage de la liste des parties en attente si le joueur n'est pas dans une partie en cours
            this.sendWaitingGames(connection);
        } else {
            ServerGame game = player.getGame();
            this.sendJoinGamePacket(new PacketJoinedGame<>(game, player), connection);
        }
    }

    /**
     * Envoie un paquet de jointure de partie à tous les clients associés à un utilisateur.
     *
     * @param game   La partie à laquelle le joueur a rejoint.
     * @param player Le joueur qui a rejoint la partie.
     * @param user   L'utilisateur pour lequel on envoie le paquet de jointure de partie.
     */
    public void sendJoinGamePacket(ServerGame game, ServerPlayer player, User user) {
        for (SocketWrapper connection : this.server.getAuthManager().getConnectionsFor(user)) {
            this.sendJoinGamePacket(new PacketJoinedGame<>(game, player), connection);
        }
    }

    /**
     * Envoie la liste des parties en attente au client spécifié.
     *
     * @param sender Le socket du client qui recevra la liste des parties en attente.
     */
    public void sendWaitingGames(SocketWrapper sender) {
        try {
            sender.sendPacket(new PacketWaitingGames<>(this.games.stream().filter(game -> game.getState() == GameState.WAITING).toList()));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi des parties en attente au client :", e);
        }
    }

    /**
     * Envoie la liste des joueurs découverts à un joueur et une connexion spécifique.
     * Cette méthode est utilisée pour envoyer les informations des joueurs aux clients lorsqu'ils rejoignent une partie.
     *
     * @param connection La connexion du client qui recevra les paquets de découverte des joueurs.
     * @param player     Le joueur à qui correspond la connexion.
     * @param game       La partie dans laquelle est le joueur.
     */
    private void sendDiscoveredPlayers(SocketWrapper connection, ServerPlayer player, ServerGame game) {
        // On envoie la liste de tous les autres joueurs puisqu'il n'y a pas d'espions.
        for (ServerPlayer otherPlayer : game.getPlayers()) {
            if (otherPlayer != player) {
                try {
                    connection.sendPacket(new PacketDiscoverPlayer(otherPlayer.getId(), otherPlayer.getRace(), otherPlayer.getUser().getUsername()));
                } catch (IOException e) {
                    LogManager.logError("Erreur lors de l'envoi du paquet de découverte du joueur " + otherPlayer.getUser().getUsername() + " à la connexion " + connection.getName(), e);
                }
            }
        }
        sendChats(connection, player);
    }

    /**
     * Récupère les messages de chat pour un joueur spécifique à partir de la base de données.
     *
     * @param player Le joueur pour lequel on veut récupérer les messages de chat.
     * @return Une liste de messages de chat pour le joueur spécifié.
     */
    private List<ChatMessage> getMessagesForPlayer(ServerPlayer player) {
        ArrayList<ChatMessage> messages = new ArrayList<>();
        UserManager userManager = this.server.getUserManager();

        try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT * FROM chat_message WHERE sender_player_id = ? OR receiver_player_id = ? ORDER BY sent_at")) {
            statement.setInt(1, player.getId());
            statement.setInt(2, player.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int senderId = resultSet.getInt("sender_player_id");
                int receiverId = resultSet.getInt("receiver_player_id");
                String message = resultSet.getString("message");
                long timestamp = resultSet.getTimestamp("sent_at").getTime();
                ServerPlayer sender = userManager.getPlayer(senderId);
                ServerPlayer receiver = userManager.getPlayer(receiverId);
                messages.add(new ChatMessage(sender, receiver, message, -1, timestamp));
            }
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la récupération des messages de chat du joueur " + player.getUser().getUsername() + " dans la base de données.", e);
        }

        return messages;
    }

    /**
     * Envoie les messages de chat à un joueur spécifique lors de sa connexion.
     * Cette méthode est appelée pour envoyer les messages de chat précédents au joueur lorsqu'il se connecte.
     *
     * @param connection La connexion du joueur qui reçoit les messages de chat.
     * @param player     Le joueur qui reçoit les messages de chat.
     */
    private void sendChats(SocketWrapper connection, ServerPlayer player) {
        List<ChatMessage> allMessages = getMessagesForPlayer(player);
        if (allMessages.isEmpty()) {
            return;
        }
        try {
            for (ChatMessage message : allMessages) {
                PacketMessage packetMessage = new PacketMessage(message);
                connection.sendPacket(packetMessage);
            }
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de chat au joueur " + player.getUser().getUsername() + " à la connexion " + connection.getName(), e);
        }
    }

    /**
     * Envoie la liste des joueurs découverts à un joueur et à tous ses clients connectés.
     * Cette méthode est utilisée pour envoyer les informations des joueurs aux clients lorsqu'ils
     * rejoignent une partie ou que la partie démarre.
     *
     * @param player Le joueur pour lequel on envoie les paquets de découverte des joueurs.
     * @param game   La partie dans laquelle est le joueur.
     */
    private void sendDiscoveredPlayers(ServerPlayer player, ServerGame game) {
        for (SocketWrapper connection : this.getConnectionsFor(player)) {
            this.sendDiscoveredPlayers(connection, player, game);
        }
    }

    /**
     * Envoie un paquet de découverte de joueur à un client spécifique.
     * Cette méthode est utilisée pour envoyer les informations d'un joueur découvert à un client.
     *
     * @param sender Le socket du client qui recevra le paquet de découverte du joueur.
     * @param user   L'utilisateur pour lequel on envoie le paquet de découverte du joueur.
     */
    public void sendDiscoverPlayerPacket(SocketWrapper sender, User user) {
        ServerPlayer player = getPlayerInRunningGame(user);
        if (player != null) {
            this.sendDiscoveredPlayers(sender, player, player.getGame());
        }
    }

    /**
     * Envoie les résultats d'un tour à un client spécifique.
     *
     * @param connection La connexion du client qui recevra les résultats du tour.
     * @param packet     Le paquet contenant les résultats du tour à envoyer au client.
     */
    private void sendTurnResults(SocketWrapper connection, PacketTurnResults packet) {
        try {
            connection.sendPacket(packet);
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi des résultats du tour au client " + connection.getName(), e);
        }
    }

    /**
     * Envoie les résultats des attaques effectuées durant le tour à un joueur spécifique.
     *
     * @param player            Le joueur à qui envoyer les résultats des attaques.
     * @param attacksResults    La liste des résultats des attaques effectuées durant le tour.
     * @param eliminatedPlayers La liste des joueurs éliminés durant le tour.
     */
    private void sendTurnResults(ServerPlayer player, int turn, List<AttackResult> attacksResults, List<Player> eliminatedPlayers) {
        List<SocketWrapper> connections = this.getConnectionsFor(player);
        if (!connections.isEmpty()) {
            PacketTurnResults packet = new PacketTurnResults(turn, attacksResults, eliminatedPlayers);
            for (SocketWrapper connection : connections) {
                this.sendTurnResults(connection, packet);
            }
        }
    }

    /**
     * Envoie les résultats de tous les tours précédents à une connexion spécifique.
     *
     * @param connection La connexion du client qui recevra les résultats des tours.
     * @param user       L'utilisateur pour lequel on envoie les résultats des tours.
     */
    public void sendTurnsResults(SocketWrapper connection, User user) {
        ServerPlayer player = this.getPlayerInRunningGame(user);
        if (player != null) {
            ServerGame game = player.getGame();
            Map<Integer, List<AttackResult>> attackResults = this.loadAttackResultsInvolvingPlayer(player);
            Map<Integer, List<Player>> eliminatedPlayers = game.getPlayers().stream()
                    .filter(playerInGame -> playerInGame.getEliminationTurn() != -1)
                    .collect(Collectors.groupingBy(Player::getEliminationTurn, Collectors.toCollection(ArrayList::new)));

            int currentTurn = game.getCurrentTurn();
            int maxTurn = game.getState() == GameState.ENDED ? currentTurn + 1 : currentTurn;
            for (int turn = 1; turn < maxTurn; turn++) {
                this.sendTurnResults(connection, new PacketTurnResults(
                        turn,
                        attackResults.getOrDefault(turn, Collections.emptyList()),
                        eliminatedPlayers.getOrDefault(turn, Collections.emptyList())
                ));
            }
        }
    }

    /**
     * Appelée lorsqu'une partie est mise à jour.
     * Elle met à jour l'état de la partie dans la base de données et envoie les mises à jour de données aux joueurs de la partie.
     * Cette méthode est une surcharge de la méthode {@link #handleGameUpdate(ServerGame game, ServerPlayer exceptPlayer)} avec exceptPlayer à null.
     *
     * @param game La partie mise à jour.
     */
    public void handleGameUpdate(ServerGame game) {
        this.handleGameUpdate(game, null);
    }

    /**
     * Supprime le contenu d'une table spécifique pour un joueur donné.
     * On suppose que la table contient une colonne `player_id` pour identifier les données du joueur,
     * et que le nom de la table passé en paramètre n'est pas dangereux (pas de vérification d'injections SQL).
     *
     * @param player    Le joueur dont on veut supprimer les données de la table.
     * @param tableName Le nom de la table à nettoyer (pas de vérification d'injection SQL).
     */
    private void emptyTableByPlayer(ServerPlayer player, String tableName) {
        try (PreparedStatement preparedStatement = this.server.getDb().prepareStatement("DELETE FROM " + tableName + " WHERE player_id = ?")) {
            preparedStatement.setInt(1, player.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la suppression des données du joueur " + player.getUser().getUsername() + " dans la table " + tableName + ".", e);
        }
    }

    /**
     * Supprime les ordres en attente du joueur dans la base de données.
     * Cette méthode est appelée pour nettoyer les ordres en attente lorsque le joueur met à jour ses ordres.
     *
     * @param player Le joueur dont on veut supprimer les ordres en attente.
     */
    private void clearPendingOrders(ServerPlayer player) {
        this.emptyTableByPlayer(player, "building_creation_order");
        this.emptyTableByPlayer(player, "unit_creation_order");
        this.emptyTableByPlayer(player, "attack_player_order");
    }

    private void clearBuildingsAndUnits(ServerPlayer player) {
        this.emptyTableByPlayer(player, "player_building");
        this.emptyTableByPlayer(player, "player_unit");
    }

    /**
     * Prépare les détails des résultats d'attaques pour l'insertion dans la base de données.
     *
     * @param map         La map contenant ces détails du résultat d'attaque, où la clé est un objet identifiable (par exemple, un type de bâtiment ou d'unité) et la valeur est le nombre d'instances détruites ou perdues.
     * @param statement   La requête préparée pour insérer les détails des résultats d'attaques.
     * @param attackLogId L'identifiant du journal d'attaque auquel ces détails sont associés.
     * @throws SQLException Si une erreur SQL se produit lors de l'exécution de la requête.
     */
    private void saveAttackResultDetails(ObjectIntMap<? extends Identifiable> map, PreparedStatement statement, int attackLogId) throws SQLException {
        for (ObjectIntMap.Entry<? extends Identifiable> entry : map) {
            statement.setInt(1, attackLogId);
            statement.setInt(2, entry.getKey().getId());
            statement.setInt(3, entry.getValue());
            statement.addBatch();
        }
    }

    /**
     * Charge les détails d'un résultat d'attaque à partir de la base de données.
     *
     * @param map              La map dans laquelle les détails seront chargés, où la clé est un objet identifiable (par exemple, un type de bâtiment ou d'unité) et la valeur est le nombre d'instances détruites ou perdues.
     * @param tableName        Le nom de la table à partir de laquelle charger les détails des résultats d'attaques.
     * @param objectColumnName Le nom de la colonne dans la table qui contient l'identifiant de l'objet (par exemple, `building_type_id` ou `unit_type_id`).
     * @param attackLogId      L'identifiant du journal d'attaque auquel ces détails sont associés.
     * @param typeResolver     Une fonction qui résout l'identifiant de l'objet en un type spécifique (par exemple, `BuildingType` ou `UnitType`).
     * @param <T>              Le type de l'objet identifiable (par exemple, `BuildingType` ou `UnitType`).
     * @throws SQLException Si une erreur SQL se produit lors de l'exécution de la requête.
     */
    private <T> void loadAttackResultDetails(ObjectIntMap<T> map, String tableName, String objectColumnName, int attackLogId, IntFunction<T> typeResolver) throws SQLException {
        try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT * FROM " + tableName + " WHERE attack_log_id = ?")) {
            statement.setInt(1, attackLogId);
            ResultSet destroyedBuildingsResultSet = statement.executeQuery();
            while (destroyedBuildingsResultSet.next()) {
                int objectId = destroyedBuildingsResultSet.getInt(objectColumnName);
                int amount = destroyedBuildingsResultSet.getInt("amount");
                map.set(typeResolver.apply(objectId), amount);
            }
        }
    }

    /**
     * Charge les résultats des attaques impliquant un joueur spécifique à partir de la base de données.
     *
     * @param player Le joueur pour lequel on veut charger les résultats des attaques.
     * @return Une map où la clé est le numéro du tour et la valeur est une liste des résultats d'attaques pour ce tour.
     */
    private Map<Integer, List<AttackResult>> loadAttackResultsInvolvingPlayer(ServerPlayer player) {
        Map<Integer, List<AttackResult>> attackResults = new HashMap<>();
        try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT * FROM attacks_logs WHERE attacker_player_id = ? OR target_player_id = ?")) {
            statement.setInt(1, player.getId());
            statement.setInt(2, player.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int attackLogId = resultSet.getInt("id");
                int attackerId = resultSet.getInt("attacker_player_id");
                int targetId = resultSet.getInt("target_player_id");
                int turn = resultSet.getInt("turn");

                ServerPlayer attacker = this.server.getUserManager().getPlayer(attackerId);
                ServerPlayer target = this.server.getUserManager().getPlayer(targetId);

                ObjectIntMap<BuildingType> destroyedBuildings = target.getBuildingMap().createEmptyClone();
                this.loadAttackResultDetails(destroyedBuildings, "attacks_destroyed_buildings", "building_type_id", attackLogId, value -> Identifiable.getById(ServerData.getBuildingTypes(), value));
                ObjectIntMap<UnitType> destroyedUnits = target.getUnitMap().createEmptyClone();
                this.loadAttackResultDetails(destroyedUnits, "attacks_destroyed_units", "unit_type_id", attackLogId, value -> Identifiable.getById(ServerData.getUnitTypes(), value));

                ObjectIntMap<UnitType> lostUnits = attacker.getUnitMap().createEmptyClone();
                if (attacker.equals(player)) { // On n'envoie pas les unités perdues si le joueur n'est pas l'attaquant
                    this.loadAttackResultDetails(lostUnits, "attacks_lost_units", "unit_type_id", attackLogId, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
                }

                attackResults.computeIfAbsent(turn, k -> new ArrayList<>()).add(new AttackResult(attacker, target, destroyedBuildings, destroyedUnits, lostUnits));
            }
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la récupération des résultats d'attaques impliquant le joueur " + player.getUser().getUsername() + ".", e);
        }
        return attackResults;
    }

    /**
     * Enregistre les résultats des attaques effectuées durant le tour dans la base de données.
     *
     * @param game                    La partie dans laquelle les attaques ont été effectuées.
     * @param attacksExecutionContext Le contexte d'exécution des attaques, contenant les résultats des attaques effectuées durant le tour.
     */
    public void saveAttackResults(ServerGame game, AttacksExecutionContext attacksExecutionContext) {
        // Préparation de la requête primaire
        try (PreparedStatement attacksStatement = this.server.getDb().prepareStatement("INSERT INTO attacks_logs(attacker_player_id, target_player_id, turn) VALUES (?, ?, ?) RETURNING id")) {
            for (AttackResult result : attacksExecutionContext.getAttackResults()) {
                attacksStatement.setInt(1, result.getAttacker().getId());
                attacksStatement.setInt(2, result.getTarget().getId());
                attacksStatement.setInt(3, game.getCurrentTurn());
                attacksStatement.addBatch();
            }
            // Exécution de la requête primaire
            attacksStatement.executeBatch();

            // On récupère les identifiants des attaques insérées et on prépare les requêtes secondaires.
            ResultSet resultSet = attacksStatement.getResultSet();
            Iterator<AttackResult> iterator = attacksExecutionContext.getAttackResults().iterator();
            try (PreparedStatement destroyedBuildingsStatement = this.server.getDb().prepareStatement("INSERT INTO attacks_destroyed_buildings(attack_log_id, building_type_id, amount) VALUES (?, ?, ?)");
                 PreparedStatement destroyedUnitsStatement = this.server.getDb().prepareStatement("INSERT INTO attacks_destroyed_units(attack_log_id, unit_type_id, amount) VALUES (?, ?, ?)");
                 PreparedStatement lostUnitsStatement = this.server.getDb().prepareStatement("INSERT INTO attacks_lost_units(attack_log_id, unit_type_id, amount) VALUES (?, ?, ?)")) {
                while (resultSet.next()) {
                    assert iterator.hasNext() : "Des résultats d'attaques supplémentaires ont été retournés par la base de données !";
                    int attackLogId = resultSet.getInt("id");
                    AttackResult result = iterator.next();

                    this.saveAttackResultDetails(result.getDestroyedBuildings(), destroyedBuildingsStatement, attackLogId);
                    this.saveAttackResultDetails(result.getDestroyedUnits(), destroyedUnitsStatement, attackLogId);
                    this.saveAttackResultDetails(result.getLostUnits(), lostUnitsStatement, attackLogId);
                }
                assert !iterator.hasNext() : "Il reste des attaques à traiter mais la base de données n'a pas retourné assez de résultats !";

                // On exécute les requêtes secondaires.
                destroyedBuildingsStatement.executeBatch();
                destroyedUnitsStatement.executeBatch();
                lostUnitsStatement.executeBatch();
            }
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de l'enregistrement des attaques pour le tour " + game.getCurrentTurn() + " de la partie " + game.getName() + ".", e);
        }
    }

    /**
     * Appelée lorsqu'un tour est exécuté.
     *
     * @param game                    La partie dans laquelle le tour a été exécuté.
     * @param attacksExecutionContext Le contexte d'exécution des attaques, contenant les résultats des attaques effectuées durant le tour.
     * @param eliminatedPlayers       La liste des joueurs éliminés durant le tour.
     */
    public void handleTurnExecuted(ServerGame game, AttacksExecutionContext attacksExecutionContext, List<Player> eliminatedPlayers) {
        if (!attacksExecutionContext.getAttackResults().isEmpty()) {
            this.saveAttackResults(game, attacksExecutionContext);
            Map<Player, List<AttackResult>> attackResultsByPlayer = new HashMap<>();
            for (AttackResult result : attacksExecutionContext.getAttackResults()) {
                attackResultsByPlayer.computeIfAbsent(result.getAttacker(), k -> new ArrayList<>()).add(result);
                // On filtre les résultats pour ne pas envoyer les unités perdues au joueur cible.
                AttackResult filteredAttackResult = result.getLostUnits().isEmpty() ? result : new AttackResult(result.getAttacker(), result.getTarget(), result.getDestroyedBuildings(), result.getDestroyedUnits(), result.getLostUnits().createEmptyClone());
                attackResultsByPlayer.computeIfAbsent(result.getTarget(), k -> new ArrayList<>()).add(filteredAttackResult);
            }
            for (ServerPlayer player : game.getPlayers()) {
                List<AttackResult> results = attackResultsByPlayer.getOrDefault(player, Collections.emptyList());
                this.sendTurnResults(player, game.getCurrentTurn(), results, eliminatedPlayers);
            }
        }
    }

    private void savePlayer(ServerPlayer player) {
        try (PreparedStatement statement = this.server.getDb().prepareStatement("UPDATE player SET gold = ?, intelligence = ?, elimination_turn = ?, exited_game = ? WHERE id = ?")) {
            statement.setFloat(1, player.getGoldAmount());
            statement.setFloat(2, player.getIntelligence());
            if (player.getEliminationTurn() == -1) {
                statement.setNull(3, Types.INTEGER); // Si le joueur n'est pas éliminé, on met la colonne à NULL
            } else {
                statement.setInt(3, player.getEliminationTurn());
            }
            statement.setBoolean(4, player.hasExitedGame());
            statement.setInt(5, player.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la mise à jour des données du joueur " + player.getUser().getUsername() + " dans la base de données.", e);
        }
    }

    /**
     * Appelée lorsqu'une partie est mise à jour.
     * Elle met à jour l'état de la partie dans la base de données et envoie les mises à jour de données aux joueurs de la partie.
     *
     * @param game         La partie mise à jour.
     * @param exceptPlayer Le joueur à qui on ne doit pas envoyer les mises à jour, ou null si aucun joueur ne doit être omis.
     *                     Utilisé pour éviter d'envoyer une mise à jour à un joueur qui vient de rejoindre la partie et qui
     *                     n'a pas encore reçu les données initiales.
     */
    public void handleGameUpdate(ServerGame game, ServerPlayer exceptPlayer) {
        try (PreparedStatement statement = this.server.getDb().prepareStatement("UPDATE game SET state = ?, next_action_at = ?, current_turn = ? WHERE id = ?")) {
            statement.setString(1, game.getState().name());
            statement.setTimestamp(2, game.getNextActionAt());
            statement.setInt(3, game.getCurrentTurn());
            statement.setInt(4, game.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la mise à jour de la partie " + game.getName() + " dans la base de données.", e);
        }

        for (ServerPlayer player : game.getPlayers()) {
            this.savePlayer(player);
            this.clearPendingOrders(player);

            this.clearBuildingsAndUnits(player);

            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO player_building (player_id, building_id, quantity) VALUES (?, ?, ?)")) {
                for (ObjectIntMap.Entry<BuildingType> entry : player.getBuildingMap()) {
                    statement.setInt(1, player.getId());
                    statement.setInt(2, entry.getKey().getId());
                    statement.setInt(3, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de la sauvegarde des bâtiments pour le joueur " + player.getUser().getUsername() + ".", e);
            }

            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO player_unit (player_id, unit_id, quantity) VALUES (?, ?, ?)")) {
                for (ObjectIntMap.Entry<UnitType> entry : player.getUnitMap()) {
                    statement.setInt(1, player.getId());
                    statement.setInt(2, entry.getKey().getId());
                    statement.setInt(3, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de la sauvegarde des unites pour le joueur " + player.getUser().getUsername() + ".", e);
            }
        }

        for (ServerPlayer player : game.getPlayers()) {
            if (player != exceptPlayer) {
                this.sendPlayerDataUpdates(player);
            }
        }
    }

    /**
     * Appelée lorsque la partie démarre.
     * Elle envoie les joueurs découverts à tous les joueurs de la partie.
     *
     * @param game La partie qui vient de démarrer.
     */
    public void handleGameStart(ServerGame game) {
        for (ServerPlayer player : game.getPlayers()) {
            this.sendDiscoveredPlayers(player, game);
        }
    }

    /**
     * Méthode appelée lorsqu'un client demande de créer ou de rejoindre une partie.
     * Elle vérifie si le client est authentifié, vérifie si la partie existe déjà, ou en crée une nouvelle si nécessaire.
     * Ensuite, elle envoie les données initiales de la partie au client.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet pour rejoindre une partie reçu.
     */
    public synchronized void onJoinGame(SocketWrapper sender, PacketJoinGame packet) {
        User user = this.server.getAuthManager().getUser(sender);
        if (user == null) {
            LogManager.logError("La connexion " + sender.getName() + " n'est pas authentifiée. Impossible de créer une partie.");
            return;
        }

        ServerPlayer player = this.getPlayerInRunningGame(user);
        if (player != null) {
            ServerGame game = player.getGame();
            // L'utilisateur est déjà dans une partie en cours, on lui envoie les données de cette partie là
            this.sendJoinGamePacket(game, player, user);
            if (game.getState() != GameState.WAITING) {
                this.sendDiscoveredPlayers(sender, player, game);
            }
            return;
        }

        ServerGame game;
        ErrorType joinError;
        Optional<ServerGame> optionalGame = Identifiable.getOptionalById(this.games, packet.getGameId());
        if (optionalGame.isEmpty()) {
            LogManager.logError("La partie " + packet.getGameId() + " n'existe pas.");
            joinError = ErrorType.JOINING_GAME_NOT_FOUND;
        } else if ((game = optionalGame.get()).getState() != GameState.WAITING) {
            LogManager.logError("La partie " + game.getName() + " n'est pas en attente. Impossible de rejoindre.");
            joinError = ErrorType.JOINING_NON_WAITING;
        } else if (game.getPlayers().size() >= game.getMaxPlayers()) {
            LogManager.logError("La partie " + game.getName() + " est pleine. Impossible de rejoindre.");
            joinError = ErrorType.JOINING_GAME_FULL;
        } else if ((player = this.addPlayerToGame(user, game, packet.getChosenRace())) == null) {
            LogManager.logError("Impossible d'ajouter le joueur " + user.getUsername() + " à la partie " + game.getName() + ".");
            joinError = ErrorType.JOINING_GAME_FAILED;
        } else {
            // Tous les tests sont passés, et la partie a bien été rejointe.
            this.sendJoinGamePacket(game, player, user);
            if (game.getState() != GameState.WAITING) {
                this.sendDiscoveredPlayers(player, game);
            }
            return;
        }
        // Si on arrive ici, c'est qu'il y a eu une erreur lors de la jointure de la partie
        try {
            sender.sendPacket(new PacketError(joinError));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), e);
        }
        // Le client aura besoin de rafraîchir la liste des parties en attente
        this.sendWaitingGames(sender);
    }

    /**
     * Méthode appelée lorsqu'un client envoie des ordres en attente pour la partie.
     * Elle vérifie si le joueur a les ressources nécessaires pour exécuter les ordres, puis met à jour les ordres en attente du joueur.
     * Le paquet est désérialisé manuellement car il nécessite de récupérer le joueur depuis la connexion pour être désérialisé correctement.
     *
     * @param sender     Le socket du client qui a envoyé le paquet.
     * @param readHelper L'outil de lecture pour lire les données du paquet.
     */
    public synchronized void onUpdateOrders(SocketWrapper sender, ReadHelper readHelper) throws IOException {
        ServerPlayer player = this.getPlayerInRunningGame(sender);
        if (player == null) {
            LogManager.logError("La connexion " + sender.getName() + " n'est pas dans une partie. Impossible de mettre à jour les ordres.");
            return;
        }
        if (player.getGame().getState() != GameState.RUNNING) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " essaie de mettre à jour les ordres alors que la partie n'est pas en cours.");
            return;
        }
        PacketUpdateOrders packet = new PacketUpdateOrders(readHelper, new OrderDeserializationContext(player, this.server.getDataDeserializer()));
        ObjectIntMap<UnitType> pendingUnitsCreation = packet.getPendingUnitsCreation();
        ObjectIntMap<BuildingType> pendingBuildingsCreation = packet.getPendingBuildingsCreation();
        Collection<AttackPlayerOrderData> pendingAttacks = packet.getPendingAttacks();

        // On vérifie coté serveur que le joueur a bien les ressources nécessaires pour exécuter les ordres
        float goldCapacity = player.getGoldAmount();
        int unitsCapacity = player.getAllowedUnitCount();
        float playerIntelligence = player.getIntelligence();
        int buildingsCapacity = 5; // Maximum 5 bâtiments par tour
        ObjectIntMap<UnitType> remainingUnits = player.getUnitMap().clone();

        if (pendingUnitsCreation != null) {
            for (ObjectIntMap.Entry<UnitType> entry : pendingUnitsCreation) {
                UnitType unitType = entry.getKey();
                int count = entry.getValue();
                if (count <= 0) { // On ne peut pas créer un nombre négatif d'unités
                    entry.setValue(0);
                    continue;
                }
                goldCapacity -= unitType.getPrice() * count;
                unitsCapacity -= count;
                if (playerIntelligence < unitType.getRequiredIntelligence()) {
                    LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour créer l'unité " + unitType + ".");
                    return;
                }
            }
        }

        if (pendingBuildingsCreation != null) {
            for (ObjectIntMap.Entry<BuildingType> entry : packet.getPendingBuildingsCreation()) {
                BuildingType buildingType = entry.getKey();
                int count = entry.getValue();
                if (count <= 0) { // On ne peut pas créer un nombre négatif de bâtiments
                    entry.setValue(0);
                    continue;
                }
                goldCapacity -= buildingType.getPrice() * count;
                buildingsCapacity -= count;
                if (playerIntelligence < buildingType.getRequiredIntelligence()) {
                    LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour construire le bâtiment " + buildingType + ".");
                    return;
                }
            }
        }

        if (pendingAttacks != null) {
            for (AttackPlayerOrderData attack : pendingAttacks) {
                for (ObjectIntMap.Entry<UnitType> entry : attack.getUsingUnits()) {
                    if (((ServerPlayer) attack.getTargetPlayer()).getGame() != player.getGame()) {
                        LogManager.logError("Le joueur " + player.getUser().getUsername() + " essaie d'attaquer un joueur qui n'est pas dans la même partie.");
                        return;
                    }

                    if (remainingUnits.decrement(entry.getKey(), entry.getValue()) < 0) {
                        LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'unités de type " + entry.getKey() + " pour exécuter l'ordre d'attaque avec " + entry.getValue() + " unités.");
                        return;
                    }
                }
            }
        }

        if (goldCapacity < 0 || unitsCapacity < 0 || buildingsCapacity < 0) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez de ressources pour exécuter les ordres demandés.");
            return;
        }
        // Si on arrive ici, c'est que le joueur a les ressources nécessaires pour exécuter les ordres. On retire les anciens ordres en attente et on ajoute les nouveaux.
        if (pendingUnitsCreation != null) {
            this.emptyTableByPlayer(player, "unit_creation_order");
            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO unit_creation_order (player_id, unit_type_id, amount) VALUES (?, ?, ?)")) {
                for (ObjectIntMap.Entry<UnitType> order : pendingUnitsCreation) {
                    if (order.getValue() == 0) continue; // Pas besoin de sauvegarder un ordre avec une quantité nulle
                    statement.setInt(1, player.getId());
                    statement.setInt(2, order.getKey().getId());
                    statement.setInt(3, order.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de l'ajout des ordres de création d'unité pour le joueur " + player.getUser().getUsername() + ".", e);
            }
            player.setPendingUnitsCreation(pendingUnitsCreation);
        }

        if (pendingBuildingsCreation != null) {
            this.emptyTableByPlayer(player, "building_creation_order");
            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO building_creation_order (player_id, building_type_id, amount) VALUES (?, ?, ?)")) {
                for (ObjectIntMap.Entry<BuildingType> order : pendingBuildingsCreation) {
                    statement.setInt(1, player.getId());
                    statement.setInt(2, order.getKey().getId());
                    statement.setInt(3, order.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de l'ajout des ordres de création de bâtiment pour le joueur " + player.getUser().getUsername() + ".", e);
            }
            player.setPendingBuildingsCreation(pendingBuildingsCreation);
        }

        if (pendingAttacks != null) {
            this.emptyTableByPlayer(player, "attack_player_order");
            if (!pendingAttacks.isEmpty()) {
                try (PreparedStatement attackStatement = this.server.getDb().prepareStatement("INSERT INTO attack_player_order (player_id, target_player_id) VALUES (?, ?) RETURNING id")) {
                    for (AttackPlayerOrderData order : pendingAttacks) {
                        attackStatement.setInt(1, player.getId());
                        attackStatement.setInt(2, order.getTargetPlayer().getId());
                        attackStatement.addBatch();
                    }
                    attackStatement.executeBatch();
                    ResultSet resultSet = attackStatement.getResultSet();
                    Iterator<AttackPlayerOrderData> iterator = pendingAttacks.iterator();

                    try (PreparedStatement unitsStatement = this.server.getDb().prepareStatement("INSERT INTO attack_player_order_unit (order_id, unit_type_id, amount) VALUES (?, ?, ?)")) {
                        while (resultSet.next()) {
                            assert iterator.hasNext() : "Le nombre d'ordres d'attaque ne correspond pas au nombre de résultats retournés par la base de données.";
                            AttackPlayerOrderData order = iterator.next();
                            int orderId = resultSet.getInt("id");

                            // On ajoute les unités utilisées pour chaque ordre d'attaque
                            for (ObjectIntMap.Entry<UnitType> entry : order.getUsingUnits()) {
                                unitsStatement.setInt(1, orderId);
                                unitsStatement.setInt(2, entry.getKey().getId());
                                unitsStatement.setInt(3, entry.getValue());
                                unitsStatement.addBatch();
                            }
                        }
                        assert !iterator.hasNext() : "Il reste des ordres d'attaque à traiter, mais il n'y a pas assez de résultats retournés par la base de données.";
                        unitsStatement.executeBatch();
                    }
                } catch (SQLException e) {
                    LogManager.logError("Erreur lors de l'ajout des ordres d'attaque pour le joueur " + player.getUser().getUsername() + ".", e);
                }
            }
            player.setPendingAttacks(pendingAttacks);
        }

        this.sendPlayerDataUpdates(player);
    }

    /**
     * Méthode appelée lorsqu'un client demande de passer au tour suivant.
     * Elle vérifie si le joueur est dans une partie, puis passe au tour suivant.
     * Elle envoie ensuite les mises à jour de données aux joueurs de la partie.
     *
     * @param sender Le socket du client qui a envoyé la demande.
     */
    public synchronized void onNextTurn(SocketWrapper sender) {
        if (!Environment.DEBUG_MODE) {
            LogManager.logError("Le paquet de passage au tour suivant manuel n'est autorisé qu'en mode débogage.");
            return;
        }

        ServerPlayer player = this.getPlayerInRunningGame(sender);
        if (player == null) {
            LogManager.logError("La connexion " + sender.getName() + " n'est pas dans une partie. Impossible de passer au tour suivant.");
            return;
        }

        ServerGame game = player.getGame();
        switch (game.getState()) {
            case WAITING -> {
                LogManager.logMessage("[DEBUG] Le joueur " + player.getUser().getUsername() + " a démarré la partie " + game.getName() + ".");
                game.start();
            }
            case RUNNING -> {
                LogManager.logMessage("[DEBUG] Le joueur " + player.getUser().getUsername() + " a passé la partie " + game.getName() + " au tour suivant.");
                try {
                    game.nextTurn();
                } catch (IllegalStateException e) {
                    LogManager.logError("Erreur lors du passage au tour suivant pour le joueur " + player.getUser().getUsername() + " dans la partie " + game.getName() + ": ", e);
                }
            }
            case ENDED -> {
                LogManager.logError("Le joueur " + player.getUser().getUsername() + " a essayé de passer au tour suivant dans une partie qui est déjà terminée.");
            }
        }
    }

    /**
     * Méthode appelée lorsqu'un client demande à quitter la partie.
     * Elle vérifie si le joueur est dans une partie en cours, et si la partie est en attente,
     * puis supprime le joueur de la partie et de la base de données.
     * Si la partie est déjà en cours, elle envoie un paquet d'erreur au client.
     *
     * @param sender Le socket du client qui a envoyé la demande de déconnexion.
     */
    public synchronized void onClientQuitGame(SocketWrapper sender) {
        ServerPlayer player = this.getPlayerInRunningGame(sender);
        if (player == null) {
            this.sendWaitingGames(sender);
            return;
        }
        ServerGame serverGame = player.getGame();
        switch (serverGame.getState()) {
            case WAITING -> {
                try (PreparedStatement statement = server.getDb().prepareStatement("DELETE FROM player WHERE id = ?")) {
                    statement.setInt(1, player.getId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    LogManager.logError("Erreur lors de la suppression du joueur " + player.getUser().getUsername() + " de la base de données.", e);
                    try {
                        sender.sendPacket(new PacketError(ErrorType.QUIT_GAME_FAILED));
                    } catch (IOException ioException) {
                        LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), ioException);
                    }
                    return;
                }
                serverGame.removePlayer(player.getUser());
            }
            case RUNNING -> {
                LogManager.logError("Le joueur " + player.getUser().getUsername() + " a quitté la partie " + serverGame.getName() + " alors qu'elle était déjà en cours.");
                try {
                    sender.sendPacket(new PacketError(ErrorType.QUIT_NON_WAITING));
                } catch (IOException e) {
                    LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), e);
                }
                return;
            }
            case ENDED -> {
                player.setExitedGame(true);
                this.savePlayer(player);
            }
        }

        for (SocketWrapper connection : this.server.getAuthManager().getConnectionsFor(player.getUser())) {
            try {
                connection.sendPacket(new PacketGameAction(PacketGameAction.Action.QUIT_GAME));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet de déconnexion au client " + sender.getName(), e);
            }
            this.sendWaitingGames(connection);
        }
    }

    /**
     * Appelée lorsqu'un message de chat est reçu d'un joueur.
     *
     * @param sender La connexion du joueur qui envoie le message.
     * @param packet Le paquet de message de chat reçu, contenant l'ID du joueur récepteur, le message et un nonce.
     */
    public void onChatMessage(SocketWrapper sender, PacketMessage packet) {
        // Le sender du paquet n'est pas pris en compte pour éviter les usurpations d'identité.
        ServerPlayer senderPlayer = this.getPlayerInRunningGame(sender);
        if (senderPlayer == null) {
            LogManager.logError("La connexion %s n'est pas dans une partie en cours.".formatted(sender.getName()));
            return;
        }
        ServerPlayer receiverPlayer = this.server.getUserManager().getPlayer(packet.getReceiverId());
        if (receiverPlayer.getGame() != senderPlayer.getGame()) {
            LogManager.logError("Le joueur %s a tenté d'envoyer un message à %s, mais il n'est pas dans la même partie.".formatted(senderPlayer.getUser().getUsername(), receiverPlayer.getUser().getUsername()));
        }
        long sentAtTimestamp;
        try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO chat_message (sender_player_id, receiver_player_id, message) VALUES (?, ?, ?) RETURNING sent_at")) {
            statement.setInt(1, senderPlayer.getId());
            statement.setInt(2, receiverPlayer.getId());
            statement.setString(3, packet.getMessage());
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                sentAtTimestamp = resultSet.getTimestamp("sent_at").getTime();
            } else {
                LogManager.logError("Erreur lors de l'enregistrement du message en base de données : aucune ligne retournée.");
                return;
            }
        } catch (Exception e) {
            LogManager.logError("Erreur lors de l'enregistrement du message en base de données", e);
            return;
        }

        PacketMessage packetMessage = new PacketMessage(senderPlayer.getId(), receiverPlayer.getId(), packet.getMessage(), packet.getNonce(), sentAtTimestamp);
        for (SocketWrapper connection : Iterables.concat(this.getConnectionsFor(senderPlayer), this.getConnectionsFor(receiverPlayer))) {
            try {
                connection.sendPacket(packetMessage);
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message au joueur " + senderPlayer.getUser().getUsername() + " à la connexion " + connection.getName(), e);
            }
        }
    }
}
