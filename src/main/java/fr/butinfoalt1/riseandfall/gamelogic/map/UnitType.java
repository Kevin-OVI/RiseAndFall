package fr.butinfoalt1.riseandfall.gamelogic.map;

public enum UnitType {
    WARRIOR(10);

    private final int price;

    UnitType(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
