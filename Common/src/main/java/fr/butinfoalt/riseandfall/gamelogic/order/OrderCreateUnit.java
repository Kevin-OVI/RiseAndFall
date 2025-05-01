package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;

/**
 * Représente un ordre de création d'une unité.
 * Cet ordre est exécuté sur le joueur pour créer un certain nombre d'unités d'un type donné.
 */
public class OrderCreateUnit implements BaseOrder {
    /**
     * Type d'unité à créer.
     */
    private final UnitType unitType;
    /**
     * Nombre d'unités de ce type à créer.
     */
    private final int count;

    /**
     * Constructeur de l'ordre de création d'unité.
     *
     * @param unitType Le type d'unité à créer.
     * @param count    Le nombre d'unités de ce type à créer.
     */
    public OrderCreateUnit(UnitType unitType, int count) {
        this.unitType = unitType;
        this.count = count;
    }

    /**
     * Exécute l'ordre de création d'unité sur le joueur donné.
     *
     * @param player Le joueur sur lequel exécuter l'ordre.
     */
    @Override
    public void execute(Player player) {
        player.getUnitMap().increment(this.unitType, this.count);
    }

    /**
     * Obtient le prix de l'ordre en or.
     * On calcule le prix total en multipliant le prix de l'unité à l'unité par le nombre d'unités à créer.
     *
     * @return Le prix de l'ordre en or.
     */
    @Override
    public int getPrice() {
        return this.unitType.getPrice() * this.count;
    }

    /**
     * Obtient le type d'unité à créer.
     *
     * @return Le type d'unité à créer.
     */
    public UnitType getUnitType() {
        return this.unitType;
    }

    /**
     * Obtient le nombre d'unités à créer.
     *
     * @return Le nombre d'unités à créer.
     */
    public int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return "OrderCreateUnit{unitType=%s, count=%d}".formatted(this.unitType, this.count);
    }
}
