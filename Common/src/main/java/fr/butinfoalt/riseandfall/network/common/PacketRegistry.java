package fr.butinfoalt.riseandfall.network.common;

import java.util.HashMap;

public class PacketRegistry {
    private final HashMap<Byte, Class<? extends IPacket>> sendIdToPacket = new HashMap<>();
    private final HashMap<Class<? extends IPacket>, Byte> sendPacketToId = new HashMap<>();

    private final HashMap<Byte, PacketHandlerAndDecoder<?>> receiveIdToPacket = new HashMap<>();
    private final HashMap<Class<? extends IPacket>, Byte> receivePacketToId = new HashMap<>();

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

    public <T extends IPacket> void registerSendAndReceivePacket(byte packetId, Class<T> packetClass, IPacketHandler<T> packetHandler, IDeserializer<T> packetDecoder) {
        this.registerSendPacket(packetId, packetClass);
        this.registerReceivePacket(packetId, packetClass, packetHandler, packetDecoder);
    }

    public byte getSendPacketId(Class<? extends IPacket> packetClass) {
        return this.sendPacketToId.get(packetClass);
    }

    public PacketHandlerAndDecoder<?> getPacketDecoder(byte packetId) {
        return this.receiveIdToPacket.get(packetId);
    }

    public record PacketHandlerAndDecoder<T extends IPacket>(IPacketHandler<T> handler, IDeserializer<T> decoder) {
    }
}
