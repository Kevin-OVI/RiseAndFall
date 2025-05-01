package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Objects;

/**
 * Classe abstraite représentant un wrapper pour une connexion implémentant le protocole de paquets.
 * Cette classe gère la lecture et l'écriture de paquets sur un socket.
 */
public abstract class SocketWrapper {
    /**
     * Socket de la connexion.
     */
    private final Socket socket;

    /**
     * Registre des paquets.
     * Utilisé pour gérer l'envoi et la réception des paquets.
     */
    private final PacketRegistry packetRegistry;

    /**
     * Helper pour la lecture des paquets.
     * Utilisé pour lire les données du socket.
     */
    private final ReadHelper readHelper;

    /**
     * Helper pour l'écriture des paquets.
     * Utilisé pour écrire les données dans le socket.
     */
    private final WriteHelper writeHelper;

    /**
     * Thread de lecture.
     * Utilisé pour lire les paquets dans un thread séparé.
     */
    private final Thread readThread;

    /**
     * Constructeur de la classe SocketWrapper.
     * Initialise le socket, le registre de paquets et les helpers de lecture et d'écriture.
     *
     * @param socket         Le socket de la connexion.
     * @param packetRegistry Le registre des paquets.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'initialisation.
     */
    public SocketWrapper(Socket socket, PacketRegistry packetRegistry) throws IOException {
        System.out.println("Création du wrapper de socket");
        this.socket = socket;
        this.packetRegistry = packetRegistry;
        this.readHelper = new ReadHelper(socket.getInputStream());
        this.writeHelper = new WriteHelper(socket.getOutputStream());
        this.readThread = new Thread(this::readTask, "Socket Wrapper Read Thread");
        this.readThread.start();
        System.out.println("Thread de lecture démarré");
    }

    /**
     * Renvoie le nom de la connexion.
     * Cette méthode renvoie l'adresse IP de la connexion sous forme de chaîne.
     *
     * @return Le nom de la connexion (adresse IP).
     */
    public String getName() {
        SocketAddress address = this.socket.getRemoteSocketAddress();
        if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getHostString();
        }
        return "<unknown %d>".formatted(Objects.hashCode(address));
    }

    /**
     * Méthode de lecture des paquets dans un thread séparé.
     * Cette méthode lit les paquets du socket tant que la connexion est active.
     * Elle gère les exceptions d'entrée/sortie et ferme la connexion en cas d'erreur.
     */
    private void readTask() {
        try {
            while (this.socket.isConnected()) {
                byte packetId = this.readHelper.readByte();
                System.out.println("Reception du paquet" + packetId);
                this.handlePacket(packetId);
            }
            System.out.println("Fermeture de la socket");
        } catch (IOException e) {
            if (!(e instanceof SocketException && "Socket closed".equals(e.getMessage()))) {
                throw new RuntimeException(e);
            }
        } finally {
            try {
                this.close();
            } catch (IOException ignored) {
            }
            this.onDisconnected(this);
        }
    }

    /**
     * Gère la réception d'un paquet.
     * Cette méthode décode le paquet en fonction de son ID et appelle le gestionnaire de paquets approprié.
     *
     * @param packetId L'ID du paquet à traiter.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors du traitement du paquet.
     */
    private <T extends IPacket> void handlePacket(byte packetId) throws IOException {
        @SuppressWarnings("unchecked")
        PacketRegistry.PacketHandlerAndDecoder<T> packetHandlerAndDecoder = (PacketRegistry.PacketHandlerAndDecoder<T>) this.packetRegistry.getPacketDecoder(packetId);
        IDeserializer<T> packetDecoder = packetHandlerAndDecoder.decoder();
        IPacketHandler<T> packetHandler = packetHandlerAndDecoder.handler();

        T decodedPacket = packetDecoder.deserialize(this.readHelper);
        packetHandler.handlePacket(this, decodedPacket);
    }

    /**
     * Attend la fermeture de la socket.
     *
     * @throws InterruptedException Si le thread est interrompu pendant l'attente.
     */
    public void waitForSocketClose() throws InterruptedException {
        this.readThread.join();
    }

    /**
     * Ferme la connexion.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la fermeture de la connexion.
     */
    public void close() throws IOException {
        this.socket.close();
    }

    /**
     * Envoie un paquet au serveur.
     *
     * @param packet Le paquet à envoyer.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'envoi du paquet.
     */
    public void sendPacket(IPacket packet) throws IOException {
        byte packetId = this.packetRegistry.getSendPacketId(packet.getClass());
        this.writeHelper.writeByte(packetId);
        this.writeHelper.writeSerializable(packet);
        this.socket.getOutputStream().flush();
    }

    /**
     * Appelée lorsque la connexion est perdue.
     *
     * @param socketWrapper Le wrapper de socket qui a été déconnecté.
     */
    protected abstract void onDisconnected(SocketWrapper socketWrapper);
}
