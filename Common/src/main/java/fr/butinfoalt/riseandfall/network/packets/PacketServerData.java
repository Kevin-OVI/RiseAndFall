package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Race;
import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

public class PacketServerData implements IPacket {
    private final Race[] races;
    private final UnitType[] unitTypes;
    private final BuildingType[] buildingTypes;

    public PacketServerData(Race[] raceList, UnitType[] unitTypes, BuildingType[] buildingTypes) {
        this.races = raceList;
        this.unitTypes = unitTypes;
        this.buildingTypes = buildingTypes;
    }

    public PacketServerData(ReadHelper readHelper) throws IOException {
        this.races = readHelper.readSerializableArray(Race::new);
        this.unitTypes = readHelper.readSerializableArray(UnitType::new, this.races);
        this.buildingTypes = readHelper.readSerializableArray(BuildingType::new, this.races);
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeSerializableArray(this.races);
        writeHelper.writeSerializableArray(this.unitTypes);
        writeHelper.writeSerializableArray(this.buildingTypes);
    }

    public Race[] getRaces() {
        return this.races;
    }

    public UnitType[] getUnitTypes() {
        return this.unitTypes;
    }

    public BuildingType[] getBuildingTypes() {
        return this.buildingTypes;
    }
}
