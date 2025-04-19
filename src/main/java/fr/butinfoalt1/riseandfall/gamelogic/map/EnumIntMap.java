package fr.butinfoalt1.riseandfall.gamelogic.map;

/**
 * Une classe pour associer plusieurs types d'énumération à des entiers.
 */
public class EnumIntMap<T extends Enum<T>> {
    /**
     * Un tableau d'entiers pour stocker les valeurs associées à chaque valeur de l'énumération.
     */
    private final int[] map;

    /**
     * Constructeur de la classe EnumIntMap.
     * On crée un tableau d'entiers de la même taille que le nombre d'éléments dans l'énumération.
     *
     * @param clazz La classe de l'énumération pour laquelle les valeurs doivent être associées.
     */
    public EnumIntMap(Class<T> clazz) {
        this.map = new int[clazz.getEnumConstants().length];
    }

    /**
     * Permet de définir la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être définie.
     * @param count La valeur à associer au type d'énumération.
     */
    public void set(T type, int count) {
        this.map[type.ordinal()] = count;
    }

    /**
     * Permet d'obtenir la valeur associée à un type d'énumération.
     *
     * @param type Le type d'énumération pour lequel la valeur doit être obtenue.
     * @return La valeur associée au type d'énumération.
     */
    public int get(T type) {
        return this.map[type.ordinal()];
    }

    /**
     * Permet d'incrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être incrémentée.
     * @param count La valeur à ajouter au type d'énumération.
     */
    public void add(T type, int count) {
        this.map[type.ordinal()] += count;
    }

    /**
     * Permet de décrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être décrémentée.
     * @param count La valeur à soustraire au type d'énumération.
     */
    public void remove(T type, int count) {
        this.map[type.ordinal()] -= count;
    }
}
