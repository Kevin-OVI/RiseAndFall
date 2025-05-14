package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketCreateOrJoinGame;
import fr.butinfoalt.riseandfall.network.packets.PacketInitialGameData;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateGameData;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateOrders;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
     * Fonction pour recuperer la listes des parties en base de données
     */
    public ServerGame[] getGames() {
        try {
            try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM games")) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int turnInterval = resultSet.getInt("turn_interval");
                    int maxPlayers = resultSet.getInt("max_players");
                    int currentTurn = resultSet.getInt("current_turn");
                    boolean isStarted = resultSet.getBoolean("is_started");
                    GameState state = GameState.valueOf(resultSet.getString("state"));
                    Timestamp lastTurnTimestamp = resultSet.getTimestamp("last_turn_timestamp");

                    Map<Integer, ServerPlayer> players = new HashMap<>();

                    ServerGame game = new ServerGame(id, name, turnInterval, 3, maxPlayers, false, state, lastTurnTimestamp, 0, lastTurnTimestamp);
                    this.games.add(game);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée une nouvelle partie de jeu.
     *
     * @param name Le nom de la partie.
     * @return La nouvelle partie créée.
     */
    public synchronized ServerGame newGame(String name) {
        System.out.println("Création de la partie : " + name);
        // Création d'une partie temporaire avec un seul joueur pour le moment
        ServerGame game = new ServerGame(this.gameIdCounter++, name, 15, 1, 30, false, GameState.WAITING, null, 0, new HashMap<>());
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
    public ServerPlayer addPlayerToGame(User user, ServerGame game, Race race) {
        // TODO : Récupérer l'identifiant du joueur depuis la base de données.
        //  Pour le moment l'identifiant est 0 car il y a toujours un seul joueur par partie
        ServerPlayer player = new ServerPlayer(0, user, game, race);
        game.addPlayer(player);
        return player;
    }

    /**
     * Envoie les mises à jour de données du joueur à toutes les connexions associées.
     *
     * @param player Le joueur dont les données doivent être mises à jour.
     */
    private void sendPlayerDataUpdates(ServerPlayer player) {
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

        // Temporairement, on crée une partie pour chaque nom de joueur, qui est démarrée immédiatement
        String gameName = "Partie de " + user.getUsername();
        Race chosenRace = packet.getChosenRace();

        // TODO : Séparer le fait de créer une partie et de rejoindre une partie. Pour le moment la partie est créée si elle n'existe pas
        ServerGame game = null;
        ServerPlayer player;
        for (ServerGame g : games) {
            if (g.getName().equals(gameName)) {
                player = g.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
                if (player != null && player.getRace().equals(chosenRace)) {
                    game = g;
                    break;
                }
            }
        }
        if (game == null) {
            game = this.newGame(gameName);
            player = this.addPlayerToGame(user, game, chosenRace);
            game.start();
        } else {
            System.out.println("La partie " + gameName + " existe déjà. Le joueur va rejoindre cette partie.");
            player = game.getPlayers().iterator().next(); // TODO : Gérer plusieurs joueurs sur une même partie
        }

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
