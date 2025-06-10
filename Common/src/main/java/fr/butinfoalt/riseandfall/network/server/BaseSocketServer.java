package fr.butinfoalt.riseandfall.network.server;

import fr.butinfoalt.riseandfall.network.common.*;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de base pour le serveur du protocole de communication.
 * <p>
 * Cette classe gère la création d'un serveur socket et la gestion des connexions des clients.
 * Elle permet également d'enregistrer des paquets à envoyer et à recevoir.
 * Elle étend la classe Thread pour exécuter le serveur dans un thread séparé.
 * </p>
 */
public class BaseSocketServer extends Thread implements Closeable {
    /**
     * Ensemble des clients connectés au serveur.
     * Utilisé pour gérer les connexions et déconnexions des clients.
     */
    protected final HashSet<SocketWrapper> connectedClients = new HashSet<>();

    /**
     * Socket du serveur.
     * Utilisé pour accepter les connexions des clients.
     */
    private final ServerSocket server;

    /**
     * Registre des paquets.
     * Utilisé pour gérer l'envoi et la réception des paquets.
     */
    private final PacketRegistry packetRegistry = new PacketRegistry();

    /**
     * Constructeur de la classe BaseSocketServer.
     * Initialise le serveur socket sur le port spécifié.
     *
     * @param port Le port sur lequel le serveur écoute les connexions des clients.
     * @throws IOException Si une erreur se produit lors de la création du serveur socket.
     */
    public BaseSocketServer(int port) throws IOException {
        super("Socket Server Thread");
        this.server = new ServerSocket(port, 5);
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
     * Enregistre un paquet à recevoir avec un gestionnaire brut.
     *
     * @param packetId                Identifiant du paquet.
     * @param packetClass             Classe du paquet.
     * @param packetDecoderAndHandler Gestionnaire brut de paquets, qui gère la désérialisation et le traitement du paquet.
     * @param <T>                     Type de paquet.
     */
    public <T extends IPacket> void registerReceivePacket(byte packetId, Class<T> packetClass, IRawHandler packetDecoderAndHandler) {
        packetRegistry.registerReceivePacket(packetId, packetClass, packetDecoderAndHandler);
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
     * Méthode principale exécutée par le thread du serveur.
     * Accepte les connexions des clients et crée un SocketWrapper pour chaque client.
     */
    public void run() {
        try {
            do {
                Socket clientSocket = server.accept();
                SocketWrapper socketWrapper = new SocketWrapper(clientSocket, this.packetRegistry) {
                    @Override
                    protected void onDisconnected(SocketWrapper socketWrapper) {
                        BaseSocketServer.this.onClientDisconnected(socketWrapper);
                    }
                };
                this.onClientConnected(socketWrapper);
            } while (!this.isInterrupted());
            this.close();
        } catch (IOException e) {
            if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Ferme le serveur, ainsi que tous les clients connectés.
     * Attend que tous les clients soient déconnectés et que le thread soit terminé.
     *
     * @throws IOException Si une erreur se produit lors de la fermeture du serveur ou des clients.
     */
    @Override
    public void close() throws IOException {
        this.server.close();
        for (SocketWrapper socketWrapper : this.connectedClients) {
            socketWrapper.close();
        }
        try {
            // Impossible d'utiliser forEach car une ConcurrentModificationException sera levée quand un client se déconnectera
            while (!this.connectedClients.isEmpty()) {
                this.connectedClients.iterator().next().waitForSocketClose();
            }
            this.join();
        } catch (InterruptedException e) {
            LogManager.logError("Interrompu lors de la fermeture du serveur", e);
        }
    }

    /**
     * Méthode appelée lorsqu'un client se connecte au serveur.
     *
     * @param client Le wrapper de socket du client connecté.
     */
    protected synchronized void onClientConnected(SocketWrapper client) {
        this.connectedClients.add(client);
    }

    /**
     * Méthode appelée lorsqu'un client se déconnecte du serveur.
     *
     * @param client Le wrapper de socket du client déconnecté.
     */
    protected synchronized void onClientDisconnected(SocketWrapper client) {
        this.connectedClients.remove(client);
    }

    /**
     * Récupère l'ensemble des clients connectés au serveur.
     *
     * @return Un ensemble non modifiable contenant les clients connectés.
     */
    public Set<SocketWrapper> getConnectedClients() {
        return Collections.unmodifiableSet(this.connectedClients);
    }
}
