package fr.butinfoalt1.riseandfall.gamelogic.order;

import fr.butinfoalt1.riseandfall.gamelogic.Player;
import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;

public class OrderCreateBuilding implements BaseOrder {
    private final BuildingType buildingType;
    private final int count;

    public OrderCreateBuilding(BuildingType buildingType, int count) {
        this.buildingType = buildingType;
        this.count = count;
    }

    @Override
    public void execute(Player player) {
        player.addBuildings(this.buildingType, this.count);
    }

    @Override
    public int getPrice() {
        return this.buildingType.getPrice() * this.count;
    }
}
