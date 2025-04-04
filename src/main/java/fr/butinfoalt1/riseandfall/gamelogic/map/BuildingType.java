package fr.butinfoalt1.riseandfall.gamelogic.map;

public enum BuildingType {
    HUT("Hutte", 5, 1, 3);

    private final String displayName;
    private final int price;
    private final int goldProduction;
    private final int maxUnits;

    BuildingType(String displayName, int price, int goldProduction, int maxUnits) {
        this.displayName = displayName;
        this.price = price;
        this.goldProduction = goldProduction;
        this.maxUnits = maxUnits;
    }

    public String getDisplayName() {
        return displayName;
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
