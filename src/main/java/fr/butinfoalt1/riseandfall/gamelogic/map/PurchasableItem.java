package fr.butinfoalt1.riseandfall.gamelogic.map;

/**
 * Interface représentant un objet achetable dans le jeu.
 * Chaque objet a un nom d'affichage et un prix en or.
 */
public interface PurchasableItem {
    /**
     * Obtient le nom d'affichage de l'objet.
     *
     * @return Le nom d'affichage de l'objet.
     */
    String getDisplayName();

    /**
     * Obtient le prix de l'objet en or.
     *
     * @return Le prix de l'objet en or.
     */
    int getPrice();
}
