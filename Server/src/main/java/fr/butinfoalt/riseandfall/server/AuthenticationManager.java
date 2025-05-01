package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.network.packets.PacketToken;

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
    public void onAuthentification(SocketWrapper sender, PacketAuthentification packet) {
    }

    /**
     * Méthode appelée lors de la réception d'un paquet de token.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de token reçu.
     */
    public void onTokenAuthentification(SocketWrapper sender, PacketToken packet) {

    }
}
