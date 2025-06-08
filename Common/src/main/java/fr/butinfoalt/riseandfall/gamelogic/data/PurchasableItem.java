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
}
