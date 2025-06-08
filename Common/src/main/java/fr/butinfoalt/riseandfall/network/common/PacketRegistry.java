package fr.butinfoalt.riseandfall.network.common;

import java.util.HashMap;

/**
 * Registre pour associer les identifiants de paquets aux classes de paquets et aux gestionnaires de paquets.
 */
public class PacketRegistry {
    /**
     * Association entre l'identifiant du paquet et la classe du paquet pour l'envoi.
     */
    private final HashMap<Byte, Class<? extends IPacket>> sendIdToPacket = new HashMap<>();
    /**
     * Association entre la classe du paquet et l'identifiant du paquet pour l'envoi.
     */
    private final HashMap<Class<? extends IPacket>, Byte> sendPacketToId = new HashMap<>();

    /**
     * Association entre l'identifiant du paquet et le gestionnaire de paquets pour la réception.
     */
    private final HashMap<Byte, IRawHandler> receiveIdToPacket = new HashMap<>();
    /**
     * Association entre la classe du paquet et l'identifiant du paquet pour la réception.
     */
    private final HashMap<Class<? extends IPacket>, Byte> receivePacketToId = new HashMap<>();

    /**
     * Enregistre un paquet à envoyer.
     *
     * @param packetId    Identifiant du paquet.
     * @param packetClass Classe du paquet.
     */
    public void registerSendPacket(byte packetId, Class<? extends IPacket> packetClass) {
        if (this.sendIdToPacket.containsKey(packetId)) {
            throw new RuntimeException("Id %d is already used".formatted(packetId));
        }
        if (this.sendPacketToId.containsKey(packetClass)) {
            throw new RuntimeException("Packet class %s is already registered".formatted(packetClass.getCanonicalName()));
        }
        this.sendIdToPacket.put(packetId, packetClass);
        this.sendPacketToId.put(packetClass, packetId);
    }

    /**
     * Enregistre un paquet à recevoir.
     *
     * @param packetId      Identifiant du paquet.
     * @param packetClass   Classe du paquet.
     * @param packetHandler Gestionnaire de paquets.
     * @param packetDecoder Décodeur de paquets.
     * @param <T>           Type de paquet.
     */
    public <T extends IPacket> void registerReceivePacket(byte packetId, Class<T> packetClass, IPacketHandler<T> packetHandler, IDeserializer<T> packetDecoder) {
        this.registerReceivePacket(packetId, packetClass, (sender, readHelper) -> {
            T packet = packetDecoder.deserialize(readHelper);
            packetHandler.handlePacket(sender, packet);
        });
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
        if (this.receiveIdToPacket.containsKey(packetId)) {
            throw new RuntimeException("Id %d is already used".formatted(packetId));
        }
        if (this.receivePacketToId.containsKey(packetClass)) {
            throw new RuntimeException("Packet class %s is already registered".formatted(packetClass.getCanonicalName()));
        }
        this.receiveIdToPacket.put(packetId, packetDecoderAndHandler);
        this.receivePacketToId.put(packetClass, packetId);
    }

    /**
     * Enregistre un paquet à envoyer et à recevoir.
     *
     * @param packetId      Identifiant du paquet.
     * @param packetClass   Classe du paquet.
     * @param packetHandler Gestionnaire de paquets.
     * @param packetDecoder Décodeur de paquets.
     * @param <T>           Type de paquet.
     */
    public <T extends IPacket> void registerSendAndReceivePacket(byte packetId, Class<T> packetClass, IPacketHandler<T> packetHandler, IDeserializer<T> packetDecoder) {
        this.registerSendPacket(packetId, packetClass);
        this.registerReceivePacket(packetId, packetClass, packetHandler, packetDecoder);
    }

    /**
     * Récupère l'identifiant du paquet associé à une classe de paquet pour l'envoi.
     *
     * @param packetClass La classe du paquet.
     * @return L'identifiant du paquet associé.
     */
    public byte getSendPacketId(Class<? extends IPacket> packetClass) {
        return this.sendPacketToId.get(packetClass);
    }

    /**
     * Récupère le gestionnaire de paquets et le décodeur associés à un identifiant de paquet pour la réception.
     *
     * @param packetId Identifiant du paquet.
     * @return Gestionnaire de paquets et décodeur associés.
     */
    public IRawHandler getRawHandler(byte packetId) {
        return this.receiveIdToPacket.get(packetId);
    }
}
