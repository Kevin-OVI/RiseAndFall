package fr.butinfoalt1.riseandfall.gamelogic.map;

public class EnumIntMap<T extends Enum<T>> {
    private final int[] map;

    public EnumIntMap(int size) {
        this.map = new int[size];
    }

    public EnumIntMap(int[] map) {
        this.map = map;
    }

    public void add(T type, int count) {
        this.map[type.ordinal()] += count;
    }

    public void remove(T type, int count) {
        this.map[type.ordinal()] -= count;
    }

    public int get(T type) {
        return this.map[type.ordinal()];
    }
}
