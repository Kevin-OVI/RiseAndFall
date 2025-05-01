package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.network.packets.PacketToken;

public class AuthenticationManager {
    private final RiseAndFallServer server;

    public AuthenticationManager(RiseAndFallServer server) {
        this.server = server;
    }

    public void onAuthentification(SocketWrapper sender, PacketAuthentification packet) {
    }

    public void onTokenAuthentification(SocketWrapper sender, PacketToken packet) {

    }
}
