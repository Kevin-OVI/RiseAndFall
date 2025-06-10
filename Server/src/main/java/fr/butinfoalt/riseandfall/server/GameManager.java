package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderAttackPlayer;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.packets.PacketError.ErrorType;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.server.data.GameNameGenerator;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
            // On ignore les parties qui sont terminées
            if (game.getState() == GameState.ENDED) {
                continue;
            }
            ServerPlayer player = game.getPlayerFor(user);
            if (player != null) {
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

                ServerGame game = new ServerGame(this.server, gameId, name, maxPlayers, minPlayers, turnInterval, isPrivate, state, nextActionAt, currentTurn);
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

    private void sendDiscoveredPlayers(SocketWrapper connection, ServerPlayer player, ServerGame game) {
        // On envoie la liste de tous les autres joueurs puisqu'il n'y a pas d'espions.
        for (ServerPlayer otherPlayer : game.getPlayers()) {
            if (otherPlayer != player) {
                System.out.println("Envoi du paquet de découverte du joueur " + otherPlayer.getUser().getUsername() + " à la connexion " + connection.getName());
                try {
                    connection.sendPacket(new PacketDiscoverPlayer(otherPlayer.getId(), otherPlayer.getRace(), otherPlayer.getUser().getUsername()));
                } catch (IOException e) {
                    LogManager.logError("Erreur lors de l'envoi du paquet de découverte du joueur " + otherPlayer.getUser().getUsername() + " à la connexion " + connection.getName(), e);
                }
            }
        }
    }

    private void sendDiscoveredPlayers(ServerPlayer player, ServerGame game) {
        for (SocketWrapper connection : this.getConnectionsFor(player)) {
            this.sendDiscoveredPlayers(connection, player, game);
        }
    }

    public void sendDiscoverPlayerPacket(SocketWrapper sender, User user) {
        ServerPlayer player = getPlayerInRunningGame(user);
        if (player != null) {
            this.sendDiscoveredPlayers(sender, player, player.getGame());
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

    private void clearPendingOrders(ServerPlayer player) {
        for (String statement : new String[]{
                "DELETE FROM building_creation_order WHERE player_id = ?",
                "DELETE FROM unit_creation_order WHERE player_id = ?",
                "DELETE FROM attack_player_order WHERE player_id = ?",
        }) {
            try (PreparedStatement preparedStatement = this.server.getDb().prepareStatement(statement)) {
                preparedStatement.setInt(1, player.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de la suppression des ordres en attente du joueur " + player.getUser().getUsername() + " dans la base de données.", e);
            }
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
            try (PreparedStatement statement = this.server.getDb().prepareStatement("UPDATE player SET gold = ?, intelligence = ? WHERE id = ?")) {
                statement.setFloat(1, player.getGoldAmount());
                statement.setFloat(2, player.getIntelligence());
                statement.setInt(3, player.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de la mise à jour des données du joueur " + player.getUser().getUsername() + " dans la base de données.", e);
            }
            this.clearPendingOrders(player);
            // TODO : Sauvegarder les bâtiments et unités déjà créés par le joueur
        }

        for (ServerPlayer player : game.getPlayers()) {
            if (player != exceptPlayer) {
                this.sendPlayerDataUpdates(player);
            }
        }
    }

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
     * @param packet Le paquet de création ou de jointure de partie reçu.
     */
    public synchronized void onCreateOrJoinGame(SocketWrapper sender, PacketCreateOrJoinGame packet) {
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
        PacketUpdateOrders packet = new PacketUpdateOrders(readHelper, new OrderDeserializationContext(player, this.server.getDataDeserializer()));

        if (player.getGame().getState() != GameState.RUNNING) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " essaie de mettre à jour les ordres alors que la partie n'est pas en cours.");
            return;
        }

        // On vérifie coté serveur que le joueur a bien les ressources nécessaires pour exécuter les ordres
        float goldCapacity = player.getGoldAmount();
        int unitsCapacity = player.getAllowedUnitCount();
        float playerIntelligence = player.getIntelligence();
        int buildingsCapacity = 5; // Maximum 5 bâtiments par tour
        ObjectIntMap<UnitType> remainingUnits = player.getUnitMap().clone();

        List<BaseOrder> newOrders = packet.getOrders();

        List<OrderCreateBuilding> createBuildingOrders = new ArrayList<>();
        List<OrderCreateUnit> createUnitOrders = new ArrayList<>();
        List<OrderAttackPlayer> attackPlayerOrders = new ArrayList<>();

        for (BaseOrder order : newOrders) {
            goldCapacity -= order.getPrice();
            switch (order) {
                case OrderCreateBuilding orderCreateBuilding -> {
                    buildingsCapacity -= orderCreateBuilding.getCount();
                    if (playerIntelligence < orderCreateBuilding.getBuildingType().getRequiredIntelligence()) {
                        LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour construire le bâtiment " + orderCreateBuilding.getBuildingType() + ".");
                        return;
                    }
                    createBuildingOrders.add(orderCreateBuilding);
                }
                case OrderCreateUnit orderCreateUnit -> {
                    unitsCapacity -= orderCreateUnit.getCount();
                    if (playerIntelligence < orderCreateUnit.getUnitType().getRequiredIntelligence()) {
                        LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour créer l'unité " + orderCreateUnit.getUnitType() + ".");
                        return;
                    }
                    createUnitOrders.add(orderCreateUnit);
                }
                case OrderAttackPlayer orderAttackPlayer -> {
                    for (ObjectIntMap.Entry<UnitType> entry : orderAttackPlayer.getUsingUnits()) {
                        if (((ServerPlayer) orderAttackPlayer.getTargetPlayer()).getGame() != player.getGame()) {
                            LogManager.logError("Le joueur " + player.getUser().getUsername() + " essaie d'attaquer un joueur qui n'est pas dans la même partie.");
                            return;
                        }

                        if (remainingUnits.decrement(entry.getKey(), entry.getValue()) < 0) {
                            LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'unités de type " + entry.getKey() + " pour exécuter l'ordre d'attaque avec " + entry.getValue() + " unités.");
                            return;
                        }
                    }
                    attackPlayerOrders.add(orderAttackPlayer);
                }
                default -> throw new IllegalStateException("Unexpected value: " + order);
            }
        }
        if (goldCapacity < 0 || unitsCapacity < 0 || buildingsCapacity < 0) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez de ressources pour exécuter les ordres demandés.");
            return;
        }
        // Si on arrive ici, c'est que le joueur a les ressources nécessaires pour exécuter les ordres. On retire les anciens ordres en attente et on ajoute les nouveaux.
        this.clearPendingOrders(player);

        if (!createBuildingOrders.isEmpty()) {
            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO building_creation_order (player_id, building_type_id, amount) VALUES (?, ?, ?)")) {
                for (OrderCreateBuilding order : createBuildingOrders) {
                    statement.setInt(1, player.getId());
                    statement.setInt(2, order.getBuildingType().getId());
                    statement.setInt(3, order.getCount());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de l'ajout des ordres de création de bâtiment pour le joueur " + player.getUser().getUsername() + ".", e);
            }
        }

        if (!createUnitOrders.isEmpty()) {
            try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO unit_creation_order (player_id, unit_type_id, amount) VALUES (?, ?, ?)")) {
                for (OrderCreateUnit order : createUnitOrders) {
                    statement.setInt(1, player.getId());
                    statement.setInt(2, order.getUnitType().getId());
                    statement.setInt(3, order.getCount());
                    statement.addBatch();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de l'ajout des ordres de création d'unité pour le joueur " + player.getUser().getUsername() + ".", e);
            }
        }

        if (!attackPlayerOrders.isEmpty()) {
            try (PreparedStatement attackStatement = this.server.getDb().prepareStatement("INSERT INTO attack_player_order (player_id, target_player_id) VALUES (?, ?) RETURNING id")) {
                for (OrderAttackPlayer order : attackPlayerOrders) {
                    attackStatement.setInt(1, player.getId());
                    attackStatement.setInt(2, order.getTargetPlayer().getId());
                    attackStatement.addBatch();
                }
                attackStatement.executeBatch();
                ResultSet resultSet = attackStatement.getResultSet();
                Iterator<OrderAttackPlayer> iterator = attackPlayerOrders.iterator();

                try (PreparedStatement unitsStatement = this.server.getDb().prepareStatement("INSERT INTO attack_player_order_unit (attack_id, unit_type_id, amount) VALUES (?, ?, ?)")) {
                    while (resultSet.next()) {
                        assert iterator.hasNext() : "Le nombre d'ordres d'attaque ne correspond pas au nombre de résultats retournés par la base de données.";
                        OrderAttackPlayer orderAttackPlayer = iterator.next();
                        int orderId = resultSet.getInt("id");

                        // On ajoute les unités utilisées pour chaque ordre d'attaque
                        for (ObjectIntMap.Entry<UnitType> entry : orderAttackPlayer.getUsingUnits()) {
                            unitsStatement.setInt(1, orderId);
                            unitsStatement.setInt(2, entry.getKey().getId());
                            unitsStatement.setInt(3, entry.getValue());
                            unitsStatement.addBatch();
                        }
                    }
                    assert !iterator.hasNext() : "Il reste des ordres d'attaque à traiter, mais aucun résultat n'a été retourné par la base de données.";
                    unitsStatement.executeBatch();
                }
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de l'ajout des ordres d'attaque pour le joueur " + player.getUser().getUsername() + ".", e);
            }
        }

        player.updatePendingOrders(newOrders);
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
        if (serverGame.getState() != GameState.WAITING) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " a quitté la partie " + serverGame.getName() + " alors qu'elle était déjà en cours.");
            try {
                sender.sendPacket(new PacketError(ErrorType.QUIT_NON_WAITING));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), e);
            }
            return;
        }
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

        for (SocketWrapper connection : this.server.getAuthManager().getConnectionsFor(player.getUser())) {
            try {
                connection.sendPacket(new PacketGameAction(PacketGameAction.Action.QUIT_GAME));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet de déconnexion au client " + sender.getName(), e);
            }
            this.sendWaitingGames(connection);
        }
    }
}
