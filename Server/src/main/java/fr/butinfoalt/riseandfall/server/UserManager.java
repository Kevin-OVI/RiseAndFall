package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;

import java.util.HashSet;

public class UserManager {
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Ensemble de toutes les utilisateurs
     */
    private final HashSet<User> users = new HashSet<>();

    /**
     * Ensemble de toutes les players
     */
    private final HashSet<ServerPlayer> players = new HashSet<>();

    /**
     * Constructeur de la classe UserManager.
     *
     * @param server Instance du serveur.
     */
    public UserManager(RiseAndFallServer server, HashSet<User> users, HashSet<ServerPlayer> players) {
        this.server = server;
        this.users.addAll(users);
        this.players.addAll(players);
    }

    /**
     * Ajoute un utilisateur au serveur.
     *
     * @param user Utilisateur à ajouter.
     */
    public void addUser(User user) {
        this.users.add(user);
    }

    /**
     * Supprime un utilisateur du serveur.
     *
     * @param user Utilisateur à supprimer.
     */
    public void removeUser(User user) {
        this.users.remove(user);
    }

    /**
     * Ajoute un joueur au serveur.
     *
     * @param player Joueur à ajouter.
     */
    public void addPlayer(ServerPlayer player) {
        this.players.add(player);
    }

    /**
     * Supprime un joueur du serveur.
     *
     * @param player Joueur à supprimer.
     */
    public void removePlayer(ServerPlayer player) {
        this.players.remove(player);
    }

    /**
     * Récupère tous les utilisateurs du serveur.
     *
     * @return Ensemble de tous les utilisateurs.
     */
    public HashSet<User> getUsers() {
        return this.users;
    }

    /**
     * Récupère tous les joueurs du serveur.
     *
     * @return Ensemble de tous les joueurs.
     */
    public HashSet<ServerPlayer> getPlayers() {
        return this.players;
    }

    /**
     * Recupere un joueur specifique
     *
     * @param id id du joueur
     * @return le joueur
     */
    public ServerPlayer getPlayer(int id) {
        for (ServerPlayer player : this.players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * Récupère un utilisateur spécifique.
     *
     * @param id Identifiant de l'utilisateur.
     * @return L'utilisateur correspondant à l'identifiant, ou null si aucun utilisateur ne correspond.
     */
    public User getUser(int id) {
        for (User user : this.users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
