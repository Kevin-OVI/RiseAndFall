package fr.butinfoalt1.riseandfall.gamelogic.building;

public class BuildingMap {
    private final int[] buildings;

    public BuildingMap() {
        this.buildings = new int[BuildingType.values().length];
    }

    public BuildingMap(int[] buildings) {
        this.buildings = buildings;
    }

    public void addBuildings(BuildingType type, int count) {
        this.buildings[type.ordinal()] += count;
    }

    public void removeBuildings(BuildingType type, int count) {
        this.buildings[type.ordinal()] -= count;
    }

    public int getBuildings(BuildingType type) {
        return this.buildings[type.ordinal()];
    }
}
