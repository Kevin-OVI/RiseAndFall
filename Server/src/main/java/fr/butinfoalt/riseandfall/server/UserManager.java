package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.server.data.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserManager {
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Ensemble de tous les utilisateurs
     */
    private final Map<Integer, User> users;

    /**
     * Ensemble de toutes les players
     */
    private final Map<Integer, ServerPlayer> players;

    /**
     * Constructeur de la classe UserManager.
     *
     * @param server Instance du serveur.
     */
    public UserManager(RiseAndFallServer server, List<User> users, List<ServerPlayer> players) {
        this.server = server;
        this.users = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        this.players = players.stream().collect(Collectors.toMap(ServerPlayer::getId, p -> p));
    }

    /**
     * Ajoute un utilisateur au serveur.
     *
     * @param user Utilisateur à ajouter.
     */
    public void addUser(User user) {
        this.users.put(user.getId(), user);
    }

    /**
     * Supprime un utilisateur du serveur.
     *
     * @param user Utilisateur à supprimer.
     */
    public void removeUser(User user) {
        this.users.remove(user.getId());
    }

    /**
     * Ajoute un joueur au serveur.
     *
     * @param player Joueur à ajouter.
     */
    public void addPlayer(ServerPlayer player) {
        this.players.put(player.getId(), player);
    }

    /**
     * Supprime un joueur du serveur.
     *
     * @param player Joueur à supprimer.
     */
    public void removePlayer(ServerPlayer player) {
        this.players.remove(player.getId());
    }

    /**
     * Récupère tous les utilisateurs du serveur.
     *
     * @return Ensemble de tous les utilisateurs.
     */
    public Collection<User> getUsers() {
        return this.users.values();
    }

    /**
     * Récupère tous les joueurs du serveur.
     *
     * @return Ensemble de tous les joueurs.
     */
    public Collection<ServerPlayer> getPlayers() {
        return this.players.values();
    }

    /**
     * Recupere un joueur specifique
     *
     * @param id id du joueur
     * @return le joueur
     */
    public ServerPlayer getPlayer(int id) {
        return this.players.get(id);
    }

    /**
     * Récupère un utilisateur spécifique.
     *
     * @param id Identifiant de l'utilisateur.
     * @return L'utilisateur correspondant à l'identifiant, ou null si aucun utilisateur ne correspond.
     */
    public User getUser(int id) {
        return this.users.get(id);
    }
}
