package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.network.packets.PacketToken;
import fr.butinfoalt.riseandfall.server.data.User;

import java.util.*;

/**
 * Classe responsable de la gestion de l'authentification des clients.
 * Elle traite les paquets d'authentification reçus du client et gère les tokens.
 */
public class AuthenticationManager {
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Ensemble des utilisateurs
     * TODO : Utiliser la base de données en temps voulu, pour le moment l'ensemble des joueurs est éphémère.
     */
    private final Set<User> users = new HashSet<>();

    /**
     * Map des connexions des utilisateurs.
     * Utilisée pour associer un socket à un utilisateur.
     */
    private final Map<SocketWrapper, User> userConnections = new HashMap<>();

    /**
     * Constructeur de la classe AuthenticationManager.
     *
     * @param server Instance du serveur.
     */
    public AuthenticationManager(RiseAndFallServer server) {
        this.server = server;
    }

    /**
     * Méthode appelée lors de la réception d'un paquet d'authentification.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet d'authentification reçu.
     */
    public synchronized void onAuthentification(SocketWrapper sender, PacketAuthentification packet) {
        if (this.userConnections.containsKey(sender)) {
            System.out.printf("Le client %s est déjà authentifié.%n", sender.getName());
            return;
        }
        // TODO : Vérifier le mot de passe
        String username = packet.getUsername();
        User foundUser = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                foundUser = user;
                break;
            }
        }
        if (foundUser == null) {
            foundUser = new User(this.users.size(), username);
            this.users.add(foundUser);
        }
        this.userConnections.put(sender, foundUser);
    }

    /**
     * Méthode appelée lors de la réception d'un paquet de token.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de token reçu.
     */
    public void onTokenAuthentification(SocketWrapper sender, PacketToken packet) {

    }

    /**
     * Méthode pour obtenir l'utilisateur associé à un socket.
     *
     * @param sender Le socket du client.
     * @return L'utilisateur associé au socket, ou null si aucun utilisateur n'est trouvé.
     */
    public synchronized User getUser(SocketWrapper sender) {
        return this.userConnections.get(sender);
    }

    /**
     * Méthode pour obtenir la liste des connexions associées à un utilisateur.
     *
     * @param user L'utilisateur dont on veut obtenir les connexions.
     * @return La liste des connexions associées à l'utilisateur.
     */
    public synchronized List<SocketWrapper> getConnectionsFor(User user) {
        List<SocketWrapper> connections = new ArrayList<>();
        for (Map.Entry<SocketWrapper, User> entry : this.userConnections.entrySet()) {
            if (entry.getValue().equals(user)) {
                connections.add(entry.getKey());
            }
        }
        return connections;
    }

    /**
     * Méthode appelée lorsque la connexion d'un client est perdue ou qu'il se déconnecte.
     *
     * @param client Le wrapper de socket qui se déconnecte.
     */
    public synchronized void onClientDisconnected(SocketWrapper client) {
        this.userConnections.remove(client);
    }
}
