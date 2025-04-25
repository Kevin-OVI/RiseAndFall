package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;

/**
 * Représente un ordre de création d'un bâtiment.
 * Cet ordre est exécuté sur le joueur pour créer un certain nombre de bâtiments d'un type donné.
 */
public class OrderCreateBuilding implements BaseOrder {
    /**
     * Type de bâtiment à créer.
     */
    private final BuildingType buildingType;
    /**
     * Nombre de bâtiments de ce type à créer.
     */
    private final int count;

    /**
     * Constructeur de l'ordre de création de bâtiment.
     *
     * @param buildingType Le type de bâtiment à créer.
     * @param count        Le nombre de bâtiments de ce type à créer.
     */
    public OrderCreateBuilding(BuildingType buildingType, int count) {
        this.buildingType = buildingType;
        this.count = count;
    }

    /**
     * Exécute l'ordre de création de bâtiment sur le joueur donné.
     *
     * @param player Le joueur sur lequel exécuter l'ordre.
     */
    @Override
    public void execute(Player player) {
        player.getBuildingMap().increment(this.buildingType, this.count);
    }

    /**
     * Obtient le prix de l'ordre en or.
     * On calcule le prix total en multipliant le prix du bâtiment à l'unité par le nombre de bâtiments à créer.
     *
     * @return Le prix de l'ordre en or.
     */
    @Override
    public int getPrice() {
        return this.buildingType.getPrice() * this.count;
    }

    /**
     * Obtient le type de bâtiment à créer.
     *
     * @return Le type de bâtiment à créer.
     */
    public BuildingType getBuildingType() {
        return this.buildingType;
    }

    /**
     * Obtient le nombre de bâtiments à créer.
     *
     * @return Le nombre de bâtiments à créer.
     */
    public int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return "OrderCreateBuilding{buildingType=%s, count=%d}".formatted(this.buildingType, this.count);
    }
}
