package fr.butinfoalt.riseandfall.gamelogic.map;

/**
 * Enum représentant les types d'unités disponibles dans le jeu.
 * Chaque type d'unité a un nom d'affichage et un prix.
 */
public enum UnitType implements PurchasableItem {
    /**
     * Type d'unité représentant un guerrier, qui coûte 10 pièces d'or.
     */
    WARRIOR("Guerrier", 10);

    /**
     * Nom d'affichage du type d'unité.
     */
    private final String displayName;
    /**
     * Prix de l'unité en pièces d'or.
     */
    private final int price;

    /**
     * Constructeur de l'énumération UnitType.
     *
     * @param displayName Le nom d'affichage du type d'unité.
     * @param price       Le prix de l'unité en pièces d'or.
     */
    UnitType(String displayName, int price) {
        this.displayName = displayName;
        this.price = price;
    }

    /**
     * Méthode pour obtenir le nom d'affichage du type d'unité.
     *
     * @return Le nom d'affichage du type d'unité.
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Méthode pour obtenir le prix de l'unité en pièces d'or.
     *
     * @return Le prix de l'unité en pièces d'or.
     */
    @Override
    public int getPrice() {
        return this.price;
    }
}
