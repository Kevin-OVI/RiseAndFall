package fr.butinfoalt.riseandfall.network.client;

import fr.butinfoalt.riseandfall.network.common.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Classe de base pour le client du protocole de communication.
 * <p>
 * Cette classe gère la connexion à un serveur socket et l'envoi de paquets.
 * Elle permet également d'enregistrer des paquets à envoyer et à recevoir.
 * Elle implémente l'interface AutoCloseable pour permettre la fermeture automatique de la connexion.
 * </p>
 */
public class BaseSocketClient implements AutoCloseable {
    /**
     * Registre des paquets.
     */
    private final PacketRegistry packetRegistry;

    /**
     * Adresse de l'hôte.
     */
    private final InetAddress host;

    /**
     * Port de connexion.
     */
    private final int port;

    /**
     * Wrapper de socket qui implémente la logique de communication.
     */
    private SocketWrapper socketWrapper;

    /**
     * Constructeur de la classe BaseSocketClient.
     *
     * @param host Adresse de l'hôte.
     * @param port Port de connexion.
     */
    public BaseSocketClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        this.packetRegistry = new PacketRegistry();
    }

    /**
     * Établit une connexion avec le serveur.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la connexion.
     */
    public synchronized void connect() throws IOException {
        if (this.socketWrapper != null) {
            throw new IllegalStateException("Client is already connected");
        }
        this.socketWrapper = new SocketWrapper(new Socket(this.host, this.port), this.packetRegistry) {
            @Override
            protected void onDisconnected(SocketWrapper socketWrapper) {
                boolean callDisconnected;
                synchronized (BaseSocketClient.this) {
                    if (BaseSocketClient.this.socketWrapper == socketWrapper) {
                        BaseSocketClient.this.socketWrapper = null;
                        callDisconnected = true;
                    } else {
                        callDisconnected = false;
                    }
                }
                if (callDisconnected) {
                    BaseSocketClient.this.onDisconnected(socketWrapper);
                }
            }
        };
    }

    /**
     * Enregistre un paquet à envoyer.
     *
     * @param packetId    Identifiant du paquet.
     * @param packetClass Classe du paquet.
     */
    public void registerSendPacket(byte packetId, Class<? extends IPacket> packetClass) {
        this.packetRegistry.registerSendPacket(packetId, packetClass);
    }

    /**
     * Enregistre un paquet à recevoir.
     *
     * @param packetId      Identifiant du paquet.
     * @param packetClass   Classe du paquet.
     * @param packetHandler Gestionnaire du paquet.
     * @param packetDecoder Décodeur du paquet.
     * @param <T>           Type du paquet.
     */
    public <T extends IPacket> void registerReceivePacket(byte packetId, Class<T> packetClass, IPacketHandler<T> packetHandler, IDeserializer<T> packetDecoder) {
        this.packetRegistry.registerReceivePacket(packetId, packetClass, packetHandler, packetDecoder);
    }

    /**
     * Enregistre un paquet à envoyer et à recevoir.
     *
     * @param packetId      Identifiant du paquet.
     * @param packetClass   Classe du paquet.
     * @param packetHandler Gestionnaire du paquet.
     * @param packetDecoder Décodeur du paquet.
     * @param <T>           Type du paquet.
     */
    public <T extends IPacket> void registerSendAndReceivePacket(byte packetId, Class<T> packetClass, IPacketHandler<T> packetHandler, IDeserializer<T> packetDecoder) {
        this.packetRegistry.registerSendAndReceivePacket(packetId, packetClass, packetHandler, packetDecoder);
    }

    /**
     * Méthode appelée lorsque la connexion est perdue.
     *
     * @param socketWrapper Le wrapper de socket qui a été déconnecté.
     */
    protected void onDisconnected(SocketWrapper socketWrapper) {
    }

    /**
     * Attend la fermeture de la socket.
     *
     * @throws InterruptedException Si le thread est interrompu pendant l'attente.
     */
    public synchronized void waitForSocketClose() throws InterruptedException {
        if (this.socketWrapper != null)
            this.socketWrapper.waitForSocketClose();
    }

    /**
     * Envoie un paquet au serveur.
     *
     * @param packet Le paquet à envoyer.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'envoi du paquet.
     */
    public synchronized void sendPacket(IPacket packet) throws IOException {
        if (this.socketWrapper == null) {
            throw new IllegalStateException("Client is not connected");
        }
        this.socketWrapper.sendPacket(packet);
    }

    /**
     * Ferme la connexion du client.
     * Attend la fermeture de la socket avant de terminer.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la fermeture de la connexion.
     */
    @Override
    public void close() throws IOException {
        SocketWrapper thisSocketWrapper;
        synchronized (this) {
            thisSocketWrapper = this.socketWrapper;
        }
        thisSocketWrapper.close();
        try {
            thisSocketWrapper.waitForSocketClose();
        } catch (InterruptedException ignored) {
        }
    }
}
