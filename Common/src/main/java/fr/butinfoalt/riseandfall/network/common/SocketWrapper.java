package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Objects;

public abstract class SocketWrapper {
    private final Socket socket;
    private final PacketRegistry packetRegistry;
    private final ReadHelper readHelper;
    private final WriteHelper writeHelper;
    private final Thread readThread;

    public SocketWrapper(Socket socket, PacketRegistry packetRegistry) throws IOException {
        this.socket = socket;
        this.packetRegistry = packetRegistry;
        this.readHelper = new ReadHelper(socket.getInputStream());
        this.writeHelper = new WriteHelper(socket.getOutputStream());
        this.readThread = new Thread(this::readTask, "Socket Wrapper Read Thread");
        this.readThread.start();
    }

    public String getName() {
        SocketAddress address = this.socket.getRemoteSocketAddress();
        if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getHostString();
        }
        return "<unknown %d>".formatted(Objects.hashCode(address));
    }

    private void readTask() {
        try {
            while (this.socket.isConnected()) {
                byte packetId = this.readHelper.readByte();
                this.handlePacket(packetId);
            }

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

    private <T extends IPacket> void handlePacket(byte packetId) throws IOException {
        @SuppressWarnings("unchecked")
        PacketRegistry.PacketHandlerAndDecoder<T> packetHandlerAndDecoder = (PacketRegistry.PacketHandlerAndDecoder<T>) this.packetRegistry.getPacketDecoder(packetId);
        IDeserializer<T> packetDecoder = packetHandlerAndDecoder.decoder();
        IPacketHandler<T> packetHandler = packetHandlerAndDecoder.handler();

        T decodedPacket = packetDecoder.deserialize(this.readHelper);
        packetHandler.handlePacket(this, decodedPacket);
    }

    public void waitForSocketClose() throws InterruptedException {
        this.readThread.join();
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public void sendPacket(IPacket packet) throws IOException {
        byte packetId = this.packetRegistry.getSendPacketId(packet.getClass());
        this.writeHelper.writeByte(packetId);
        this.writeHelper.writeSerializable(packet);
        this.socket.getOutputStream().flush();
    }

    protected abstract void onDisconnected(SocketWrapper socketWrapper);
}
