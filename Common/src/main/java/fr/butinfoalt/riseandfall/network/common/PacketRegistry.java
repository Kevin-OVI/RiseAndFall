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
    private final HashMap<Byte, PacketHandlerAndDecoder<?>> receiveIdToPacket = new HashMap<>();
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
        if (this.receiveIdToPacket.containsKey(packetId)) {
            throw new RuntimeException("Id %d is already used".formatted(packetId));
        }
        if (this.receivePacketToId.containsKey(packetClass)) {
            throw new RuntimeException("Packet class %s is already registered".formatted(packetClass.getCanonicalName()));
        }
        this.receiveIdToPacket.put(packetId, new PacketHandlerAndDecoder<T>(packetHandler, packetDecoder));
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
     * Récupère la classe de paquet associée à un identifiant de paquet pour l'envoi.
     *
     * @param packetId Identifiant du paquet.
     * @return Classe de paquet associée.
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
    public PacketHandlerAndDecoder<?> getPacketDecoder(byte packetId) {
        return this.receiveIdToPacket.get(packetId);
    }


    /**
     * Enregistrement d'un gestionnaire de paquets et d'un décodeur.
     *
     * @param <T>     Type de paquet.
     * @param handler Gestionnaire de paquets.
     * @param decoder Décodeur de paquets.
     */
    public record PacketHandlerAndDecoder<T extends IPacket>(IPacketHandler<T> handler, IDeserializer<T> decoder) {
    }
}
