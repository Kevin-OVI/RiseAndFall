package fr.butinfoalt1.riseandfall.gamelogic.map;

/**
 * Enum représentant les types de bâtiments disponibles dans le jeu.
 * Chaque type de bâtiment a un nom d'affichage, un prix, une production d'or et un nombre maximum d'unités.
 */
public enum BuildingType implements PurchasableItem {
    /**
     * Type de bâtiment représentant une carrière, qui coûte 5 pièces d'or, produit 1 pièce d'or par tour.
     */
    QUARRY("Carrière", 5, 1, 0, 4),
    /**
     * Type de bâtiment représentant une caserne, qui coûte 10 pièces d'or, peut produire 3 unités par tour.
     */
    BARRACKS("Caserne", 10, 0, 3, 1),
    ;


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
     * Nombre initial de bâtiments de ce type.
     */
    private final int initialAmount;

    /**
     * Constructeur de l'énumération BuildingType.
     *
     * @param displayName    Le nom d'affichage du type de bâtiment.
     * @param price          Le prix du bâtiment en pièces d'or.
     * @param goldProduction La production d'or du bâtiment par tour.
     * @param maxUnits       Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     */
    BuildingType(String displayName, int price, int goldProduction, int maxUnits) {
        this(displayName, price, goldProduction, maxUnits, 0);
    }

    /**
     * Constructeur de l'énumération BuildingType.
     *
     * @param displayName    Le nom d'affichage du type de bâtiment.
     * @param price          Le prix du bâtiment en pièces d'or.
     * @param goldProduction La production d'or du bâtiment par tour.
     * @param maxUnits       Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     * @param initialAmount  Le nombre initial de bâtiments de ce type.
     */
    BuildingType(String displayName, int price, int goldProduction, int maxUnits, int initialAmount) {
        this.displayName = displayName;
        this.price = price;
        this.goldProduction = goldProduction;
        this.maxUnits = maxUnits;
        this.initialAmount = initialAmount;
    }

    /**
     * Méthode pour obtenir le nom d'affichage du type de bâtiment.
     *
     * @return Le nom d'affichage du type de bâtiment.
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Méthode pour obtenir le prix du bâtiment en pièces d'or.
     *
     * @return Le prix du bâtiment en pièces d'or.
     */
    @Override
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

    /**
     * Méthode pour obtenir le nombre initial de bâtiments de ce type.
     *
     * @return Le nombre initial de bâtiments de ce type.
     */
    public int getInitialAmount() {
        return this.initialAmount;
    }
}
