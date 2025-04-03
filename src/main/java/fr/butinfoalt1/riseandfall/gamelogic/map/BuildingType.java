package fr.butinfoalt1.riseandfall.gamelogic.map;

public enum BuildingType {
    HUT(5, 1, 3);

    private final int price;
    private final int goldProduction;
    private final int maxUnits;

    BuildingType(int price, int goldProduction, int maxUnits) {
        this.price = price;
        this.goldProduction = goldProduction;
        this.maxUnits = maxUnits;
    }

    public int getPrice() {
        return price;
    }

    public int getGoldProduction() {
        return goldProduction;
    }

    public int getMaxUnits() {
        return maxUnits;
    }
}
