package fr.butinfoalt1.riseandfall.gamelogic;

import fr.butinfoalt1.riseandfall.gamelogic.building.BuildingMap;
import fr.butinfoalt1.riseandfall.gamelogic.building.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.order.BaseOrder;

import java.util.ArrayList;

public class Player {
    public static final Player SINGLE_PLAYER = new Player();
    private final BuildingMap buildingMap = new BuildingMap();
    private final ArrayList<BaseOrder> nextOrders = new ArrayList<>();
    private int goldAmount = 50;
    // TODO : Make an enum, copy BuildingType
    private int units = 0;

    public int getGoldAmount() {
        return goldAmount;
    }

    public void addRessources(int resources) {
        this.goldAmount += resources;
    }

    public void removeResources(int resources) {
        this.goldAmount -= resources;
    }

    public void addBuildings(BuildingType type, int count) {
        buildingMap.addBuildings(type, count);
    }

    public void removeBuildings(BuildingType type, int count) {
        buildingMap.removeBuildings(type, count);
    }

    public int getBuildings(BuildingType type) {
        return buildingMap.getBuildings(type);
    }

    public int getUnits() {
        return units;
    }

    public void addUnits(int units) {
        this.units += units;
    }

    public void removeUnits(int units) {
        this.units -= units;
    }

    public void addOrder(BaseOrder order) {
        this.nextOrders.add(order);
    }

    public void executeOrders() {
        for (BaseOrder order : this.nextOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
                this.removeResources(order.getPrice());
            }
        }
        this.nextOrders.clear();
    }
}

