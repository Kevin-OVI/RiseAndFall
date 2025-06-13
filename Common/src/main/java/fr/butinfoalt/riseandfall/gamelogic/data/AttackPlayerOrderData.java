package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;

public final class AttackPlayerOrderData implements ISerializable {
    private final Player targetPlayer;
    private final ObjectIntMap<UnitType> usingUnits;

    public AttackPlayerOrderData(Player targetPlayer, ObjectIntMap<UnitType> usingUnits) {
        this.targetPlayer = targetPlayer;
        this.usingUnits = usingUnits;
    }

    public AttackPlayerOrderData(ReadHelper readHelper, OrderDeserializationContext context) throws IOException {
        this.targetPlayer = context.dataDeserializer().getPlayerById(readHelper.readInt());
        this.usingUnits = context.currentPlayer().getUnitMap().createEmptyClone();
        ObjectIntMap.deserialize(this.usingUnits, readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.targetPlayer.getId());
        ObjectIntMap.serialize(this.usingUnits, writeHelper);
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public ObjectIntMap<UnitType> getUsingUnits() {
        return usingUnits;
    }
}
