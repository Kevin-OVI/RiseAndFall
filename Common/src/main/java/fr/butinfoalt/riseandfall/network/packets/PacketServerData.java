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
        int raceCount = readHelper.readInt();
        this.races = new Race[raceCount];
        for (int i = 0; i < raceCount; i++) {
            this.races[i] = readHelper.readSerializable(Race::new);
        }
        int unitTypeCount = readHelper.readInt();
        this.unitTypes = new UnitType[unitTypeCount];
        for (int i = 0; i < unitTypeCount; i++) {
            this.unitTypes[i] = new UnitType(readHelper, this.races);
        }
        int buildingTypeCount = readHelper.readInt();
        this.buildingTypes = new BuildingType[buildingTypeCount];
        for (int i = 0; i < buildingTypeCount; i++) {
            this.buildingTypes[i] = new BuildingType(readHelper, this.races);
        }
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.races.length);
        for (Race race : this.races) {
            writeHelper.writeSerializable(race);
        }
        writeHelper.writeInt(this.unitTypes.length);
        for (UnitType unitType : this.unitTypes) {
            unitType.toBytes(writeHelper);
        }
        writeHelper.writeInt(this.buildingTypes.length);
        for (BuildingType buildingType : this.buildingTypes) {
            buildingType.toBytes(writeHelper);
        }
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
