package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.packets.PacketError.ErrorType;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Classe gérant les parties de jeu.
 */
public class GameManager {
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
    public GameManager(RiseAndFallServer server) {
        this.server = server;
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
     * Crée une nouvelle partie de jeu.
     *
     * @param name Le nom de la partie.
     * @return La nouvelle partie créée.
     */
    public synchronized ServerGame newGame(String name) {
        LogManager.logMessage("Création de la partie : " + name);
        ServerGame game = new ServerGame(0, name, 15, 1, 30, false, GameState.WAITING, null, 0, new HashMap<>());
        this.server.getData().games().add(game);
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
            LogManager.logError("La connexion " + sender.getName() + " n'est pas authentifiée. Impossible de créer une partie.");
            return;
        }

        Optional<ServerGame> optionalGame = Identifiable.getOptionalById(this.server.getData().games(), packet.getGameId());
        if (optionalGame.isEmpty()) {
            LogManager.logError("La partie " + packet.getGameId() + " n'existe pas.");
            try {
                sender.sendPacket(new PacketError(ErrorType.JOINING_GAME_GAME_NOT_FOUND));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), e);
            }
            return;
        }
        ServerGame game = optionalGame.get();
        ServerPlayer player = this.addPlayerToGame(user, game, packet.getChosenRace());
        if (player == null) {
            try {
                sender.sendPacket(new PacketError(ErrorType.JOINING_GAME_FAILED));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + sender.getName(), e);
            }
            return;
        }
        this.currentlyPlayingMap.put(sender, player);
        try {
            sender.sendPacket(new PacketInitialGameData<>(game, player));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi des données initiales de la partie au client " + sender.getName(), e);
        }
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
            LogManager.logError("La connexion " + sender.getName() + " n'est pas dans une partie. Impossible de mettre à jour les ordres.");
            return;
        }

        // On vérifie coté serveur que le joueur a bien les ressources nécessaires pour exécuter les ordres
        int goldCapacity = player.getGoldAmount();
        int unitsCapacity = player.getAllowedUnitCount();
        int playerIntelligence = player.getIntelligence();
        int buildingsCapacity = 5; // Maximum 5 bâtiments par tour
        List<BaseOrder> newOrders = packet.getOrders();
        for (BaseOrder order : newOrders) {
            goldCapacity -= order.getPrice();
            if (order instanceof OrderCreateBuilding orderCreateBuilding) {
                buildingsCapacity -= orderCreateBuilding.getCount();
                if (playerIntelligence < orderCreateBuilding.getBuildingType().getRequiredIntelligence()) {
                    LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour construire le bâtiment " + orderCreateBuilding.getBuildingType() + ".");
                    return;
                }
            } else if (order instanceof OrderCreateUnit orderCreateUnit) {
                unitsCapacity -= orderCreateUnit.getCount();
                if (playerIntelligence < orderCreateUnit.getUnitType().getRequiredIntelligence()) {
                    LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez d'intelligence pour créer l'unité " + orderCreateUnit.getUnitType() + ".");
                    return;
                }
            }
        }
        if (goldCapacity < 0 || unitsCapacity < 0 || buildingsCapacity < 0) {
            LogManager.logError("Le joueur " + player.getUser().getUsername() + " n'a pas assez de ressources pour exécuter les ordres demandés.");
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
            LogManager.logError("La connexion " + sender.getName() + " n'est pas dans une partie. Impossible de passer au tour suivant.");
            return;
        }

        ServerGame game = player.getGame();
        try {
            game.nextTurn();
        } catch (IllegalStateException e) {
            LogManager.logError("Erreur lors du passage au tour suivant pour le joueur " + player.getUser().getUsername() + " dans la partie " + game.getName() + ": ", e);
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

    public void onClientQuitGame(SocketWrapper client) {
        ServerPlayer player = this.currentlyPlayingMap.remove(client);
        if (player == null) {
            return;
        }
        ServerGame serverGame = player.getGame();
        serverGame.removePlayer(player.getUser());
        try (PreparedStatement statement = server.getDb().prepareStatement("DELETE FROM player WHERE id = ?")) {
            statement.setInt(1, player.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la suppression du joueur " + player.getUser().getUsername() + " de la base de données.", e);
            try {
                client.sendPacket(new PacketError(ErrorType.QUIT_GAME_FAILED));
            } catch (IOException ioException) {
                LogManager.logError("Erreur lors de l'envoi du paquet d'erreur au client " + client.getName(), ioException);
            }
        }
    }
}
