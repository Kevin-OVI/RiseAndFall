package fr.butinfoalt1.riseandfall.gamelogic;

import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt1.riseandfall.gamelogic.order.BaseOrder;

import java.util.ArrayList;

public class Player {
    public static final Player SINGLE_PLAYER = new Player();
    private final EnumIntMap<BuildingType> buildingMap = new EnumIntMap<>(BuildingType.values().length);
    private final ArrayList<BaseOrder> nextOrders = new ArrayList<>();
    private int goldAmount = 50;
    // TODO : Make an enum, copy BuildingType
    private int units = 0;

    public int getGoldAmount() {
        return goldAmount;
    }

    public void addGoldAmount(int goldAmount) {
        this.goldAmount += goldAmount;
    }

    public void removeGoldAmount(int goldAmount) {
        this.goldAmount -= goldAmount;
    }

    public void addBuildings(BuildingType type, int count) {
        buildingMap.add(type, count);
    }

    public void removeBuildings(BuildingType type, int count) {
        buildingMap.remove(type, count);
    }

    public int getBuildings(BuildingType type) {
        return buildingMap.get(type);
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
        // TODO : Add goldAmount according to the buildings

        for (BaseOrder order : this.nextOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
                this.removeGoldAmount(order.getPrice());
            }
        }
        this.nextOrders.clear();
    }
}
