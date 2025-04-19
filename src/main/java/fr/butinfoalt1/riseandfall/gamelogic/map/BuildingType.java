package fr.butinfoalt1.riseandfall.gamelogic.map;

/**
 * Enum représentant les types de bâtiments disponibles dans le jeu.
 * Chaque type de bâtiment a un nom d'affichage, un prix, une production d'or et un nombre maximum d'unités.
 */
public enum BuildingType {
    /**
     * Type de bâtiment représentant une hutte, qui coûte 5 pièces d'or, produit 1 pièce d'or et un maximum de 3 unités par tour.
     */
    HUT("Hutte", 5, 1, 3);

    /**
     * Nom d'affichage du type de bâtiment.
     */
    private final String displayName;
    /**
     * Prix du bâtiment en pièces d'or.
     */
    private final int price;
    /**
     * Production d'or du bâtiment par tour.
     */
    private final int goldProduction;
    /**
     * Nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     */
    private final int maxUnits;

    /**
     * Constructeur de l'énumération BuildingType.
     *
     * @param displayName    Le nom d'affichage du type de bâtiment.
     * @param price          Le prix du bâtiment en pièces d'or.
     * @param goldProduction La production d'or du bâtiment par tour.
     * @param maxUnits       Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     */
    BuildingType(String displayName, int price, int goldProduction, int maxUnits) {
        this.displayName = displayName;
        this.price = price;
        this.goldProduction = goldProduction;
        this.maxUnits = maxUnits;
    }

    /**
     * Méthode pour obtenir le nom d'affichage du type de bâtiment.
     *
     * @return Le nom d'affichage du type de bâtiment.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Méthode pour obtenir le prix du bâtiment en pièces d'or.
     *
     * @return Le prix du bâtiment en pièces d'or.
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Méthode pour obtenir la production d'or du bâtiment par tour.
     *
     * @return La production d'or du bâtiment par tour.
     */
    public int getGoldProduction() {
        return this.goldProduction;
    }

    /**
     * Méthode pour obtenir le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     *
     * @return Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     */
    public int getMaxUnits() {
        return this.maxUnits;
    }
}
