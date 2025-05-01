package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

public class PacketToken implements IPacket {
    private final String token;

    public PacketToken(String token) {
        this.token = token;
    }

    public PacketToken(ReadHelper readHelper) throws IOException {
        this.token = readHelper.readString();
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.token);
    }

    public String getToken() {
        return this.token;
    }
}
