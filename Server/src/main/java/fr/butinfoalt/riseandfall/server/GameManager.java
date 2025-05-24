package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Classe gérant les parties de jeu.
 */
public class GameManager {
    /**
     * Ensemble de toutes les parties crées
     */
    private final HashSet<ServerGame> games = new HashSet<>();
    /**
     * Association entre les connexions et les joueurs actuellement en jeu.
     */
    private final Map<SocketWrapper, ServerPlayer> currentlyPlayingMap = new HashMap<>();
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Constructeur de la classe GameManager.
     *
     * @param server Instance du serveur.
     */
    public GameManager(RiseAndFallServer server, HashSet<ServerGame> games) {
        this.server = server;
        this.games.addAll(games);
    }

    /**
     * Récupère la liste des connexions pour un joueur donné.
     *
     * @param player Le joueur dont on veut récupérer les connexions.
     * @return La liste des connexions pour le joueur donné.
     */
    public synchronized List<SocketWrapper> getConnectionsFor(ServerPlayer player) {
        List<SocketWrapper> connections = new ArrayList<>();
        for (Map.Entry<SocketWrapper, ServerPlayer> entry : this.currentlyPlayingMap.entrySet()) {
            if (entry.getValue().equals(player)) {
                connections.add(entry.getKey());
            }
        }
        return connections;
    }

    /**
     * Fonction pour recuperer la listes des parties
     */
    public ServerGame[] getGames() {
        return this.games.toArray(new ServerGame[0]);
    }

    /**
     * Crée une nouvelle partie de jeu.
     *
     * @param name Le nom de la partie.
     * @return La nouvelle partie créée.
     */
    public synchronized ServerGame newGame(String name) {
        System.out.println("Création de la partie : " + name);
        ServerGame game = new ServerGame(0, name, 15, 1, 30, false, GameState.WAITING, null, 0, new HashMap<>());
        this.games.add(game);
        return game;
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
        int playerId = -1;
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
            e.printStackTrace();
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
                    System.err.printf("Erreur lors de l'envoi du paquet de mise à jour des données du joueur %s à la connexion %s :%n", player.getUser().getUsername(), connection.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public ServerGame getServerGameByGame(Game game) {
        Map<Integer, ServerPlayer> players = new HashMap<>();

        try (PreparedStatement gameStatement = server.getDb().prepareStatement("SELECT * FROM game WHERE id = ?")) {
            gameStatement.setInt(1, game.getId());
            ResultSet gameResultSet = gameStatement.executeQuery();

            if (gameResultSet.next()) {
                try (PreparedStatement playerStatement = server.getDb().prepareStatement("SELECT id FROM player WHERE game_id = ?")) {
                    playerStatement.setInt(1, game.getId());
                    ResultSet playerResultSet = playerStatement.executeQuery();

                    while (playerResultSet.next()) {
                        int playerId = playerResultSet.getInt("id");
                        Player player = server.getUserManager().getPlayer(playerId);

                        if (player instanceof ServerPlayer) {
                            players.put(playerId, (ServerPlayer) player);
                        }
                    }
                }

                return new ServerGame(
                        gameResultSet.getInt("id"),
                        gameResultSet.getString("name"),
                        gameResultSet.getInt("turn_interval"),
                        gameResultSet.getInt("min_players"),
                        gameResultSet.getInt("max_players"),
                        false,
                        GameState.valueOf(gameResultSet.getString("state")),
                        gameResultSet.getTimestamp("last_turn_at"),
                        gameResultSet.getInt("current_turn"),
                        players
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ServerGame getGameById(int id) {
        for (ServerGame game : this.games) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }

    public void addConnectionToGame(ServerPlayer player, SocketWrapper sender) {
        this.currentlyPlayingMap.put(sender, player);
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
            System.err.printf("La connexion %s n'est pas authentifié. Impossible de créer une partie.%n", sender.getName());
            return;
        }

        ServerGame game = getGameById(packet.getGameId());
        if (game == null) {
            Game realGame = null;
            for (Game g : ServerData.getGames()) {
                if (g.getId() == packet.getGameId()) {
                    realGame = g;
                    break;
                }
            }
            if (realGame == null) {
                System.err.printf("La partie %d n'existe pas.%n", packet.getGameId());
                try {
                    sender.sendPacket(new PacketError("La partie n'existe pas.", "Joining game"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            game = this.getServerGameByGame(realGame);
        }

        ServerPlayer player = this.addPlayerToGame(user, game, packet.getChosenRace());

        try {
            sender.sendPacket(new PacketInitialGameData<>(game, player));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du paquet de connexion à la partie : ");
            e.printStackTrace();
        }
        this.currentlyPlayingMap.put(sender, player);
    }

    /**
     * Méthode appelée lorsqu'un client envoie des ordres en attente pour la partie.
     * Elle vérifie si le joueur a les ressources nécessaires pour exécuter les ordres, puis met à jour les ordres en attente du joueur.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de mise à jour des ordres reçu.
     */
    public synchronized void onUpdateOrders(SocketWrapper sender, PacketUpdateOrders packet) {
        ServerPlayer player = this.currentlyPlayingMap.get(sender);
        if (player == null) {
            System.err.printf("La connexion %s n'est pas dans une partie. Impossible de mettre à jour les ordres.%n", sender.getName());
            return;
        }

        // On vérifie coté serveur que le joueur a bien les ressources nécessaires pour exécuter les ordres
        int goldCapacity = player.getGoldAmount();
        int unitsCapacity = player.getAllowedUnitCount();
        int buildingsCapacity = 5; // Maximum 5 bâtiments par tour
        List<BaseOrder> newOrders = packet.getOrders();
        for (BaseOrder order : newOrders) {
            goldCapacity -= order.getPriceGold();
            if (order instanceof OrderCreateBuilding orderCreateBuilding) {
                buildingsCapacity -= orderCreateBuilding.getCount();
            } else if (order instanceof OrderCreateUnit orderCreateUnit) {
                unitsCapacity -= orderCreateUnit.getCount();
            }
        }
        if (goldCapacity < 0 || unitsCapacity < 0 || buildingsCapacity < 0) {
            System.err.printf("Le joueur %s n'a pas assez de ressources pour exécuter les ordres demandés.%n", player.getUser().getUsername());
            return;
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
        ServerPlayer player = this.currentlyPlayingMap.get(sender);
        if (player == null) {
            System.err.printf("La connexion %s n'est pas dans une partie. Impossible de passer au tour suivant.%n", sender.getName());
            return;
        }

        ServerGame game = player.getGame();
        try {
            game.nextTurn();
        } catch (IllegalStateException e) {
            System.err.println("Erreur lors du passage au tour suivant : ");
            e.printStackTrace();
            return;
        }

        for (ServerPlayer p : game.getPlayers()) {
            this.sendPlayerDataUpdates(p);
        }
    }

    /**
     * Méthode appelée lorsqu'un client se déconnecte.
     * Elle supprime le joueur de la liste des joueurs actuellement en jeu.
     *
     * @param client Le socket du client qui s'est déconnecté.
     */
    public synchronized void onClientDisconnected(SocketWrapper client) {
        this.currentlyPlayingMap.remove(client);
    }
}
