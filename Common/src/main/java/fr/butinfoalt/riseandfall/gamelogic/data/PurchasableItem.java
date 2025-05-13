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
    int getPriceGold();

    /**
     * Obtient le prix de l'objet en intelligence.
     *
     * @return Le prix de l'objet en intelligence.
     */
    int getPriceIntelligence();
}
