package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

public class PacketAuthentification implements IPacket {
    private final String username;
    private final String passwordHash;

    public PacketAuthentification(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public PacketAuthentification(ReadHelper readHelper) throws IOException {
        this.username = readHelper.readString();
        this.passwordHash = readHelper.readString();
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.username);
        writeHelper.writeString(this.passwordHash);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }
}
