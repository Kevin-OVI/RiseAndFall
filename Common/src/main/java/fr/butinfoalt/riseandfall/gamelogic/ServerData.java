package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;

import java.util.Arrays;

public final class ServerData {
    private static Race[] races;
    private static BuildingType[] buildingTypes;
    private static UnitType[] unitTypes;

    private ServerData() {
    }


    public static void init(Race[] races, BuildingType[] buildingTypes, UnitType[] unitTypes) {
        ServerData.races = races;
        ServerData.buildingTypes = buildingTypes;
        ServerData.unitTypes = unitTypes;

        System.out.println("Races: " + Arrays.toString(races));
        System.out.println("Building Types: " + Arrays.toString(buildingTypes));
        System.out.println("Unit Types: " + Arrays.toString(unitTypes));
    }

    public static Race[] getRaces() {
        return races;
    }

    public static BuildingType[] getBuildingTypes() {
        return buildingTypes;
    }

    public static UnitType[] getUnitTypes() {
        return unitTypes;
    }
}
