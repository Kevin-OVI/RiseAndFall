package fr.butinfoalt.riseandfall.gamelogic.data;

/**
 * Interface représentant un objet achetable dans le jeu.
 * Chaque objet a un nom d'affichage et un prix en or.
 */
public interface PurchasableItem extends NamedItem {
    /**
     * Obtient le prix de l'objet en or.
     *
     * @return Le prix de l'objet en or.
     */
    float getPrice();

    /**
     * Obtient la quantité d'intelligence requise pour construire l'unité.
     *
     * @return La quantité d'intelligence requise pour construire l'unité.
     */
    float getRequiredIntelligence();

    /**
     * Obtient la race qui peut accéder à cet objet.
     *
     * @return La race qui peut accéder à cet objet, ou null si toutes les races le peuvent.
     */
    Race getAccessibleByRace();

    /**
     * Obtient la description de l'objet.
     *
     * @return La description de l'objet.
     */
    String getDescription();
}
