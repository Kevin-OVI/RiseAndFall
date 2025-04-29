package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

public class PacketUpdate implements IPacket {
    private final byte[] updatedFileBytes;

    public PacketUpdate(byte[] updatedFileBytes) {
        this.updatedFileBytes = updatedFileBytes;
    }

    public PacketUpdate(ReadHelper readHelper) throws IOException {
        this.updatedFileBytes = readHelper.readByteArray();
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeByteArray(this.updatedFileBytes);
    }

    public byte[] getUpdatedFileBytes() {
        return updatedFileBytes;
    }
}
