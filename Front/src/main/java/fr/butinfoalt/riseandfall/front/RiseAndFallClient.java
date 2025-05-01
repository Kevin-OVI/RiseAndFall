package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.ServerData;
import fr.butinfoalt.riseandfall.network.client.BaseSocketClient;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.network.packets.PacketServerData;
import fr.butinfoalt.riseandfall.network.packets.PacketToken;
import javafx.application.Platform;

public class RiseAndFallClient extends BaseSocketClient {
    public RiseAndFallClient() {
        super(Environment.SERVER_HOST, Environment.SERVER_PORT);
        this.registerSendPacket((byte) 0, PacketAuthentification.class);
        this.registerSendAndReceivePacket((byte) 1, PacketToken.class, this::onToken, PacketToken::new);
        this.registerReceivePacket((byte) 2, PacketServerData.class, this::onServerData, PacketServerData::new);
    }

    private void onToken(SocketWrapper sender, PacketToken packet) {
    }

    private void onServerData(SocketWrapper sender, PacketServerData packet) {
        ServerData.init(packet.getRaces(), packet.getBuildingTypes(), packet.getUnitTypes());
        Platform.runLater(() -> {
            RiseAndFallApplication.switchToView(View.WELCOME);
        });
    }
}
