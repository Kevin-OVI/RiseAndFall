package fr.butinfoalt.riseandfall.gamelogic.map;

import fr.butinfoalt.riseandfall.gamelogic.Race;

/**
 * Enum représentant les types d'unités disponibles dans le jeu.
 * Chaque type d'unité a un nom d'affichage et un prix.
 */
public enum UnitType implements PurchasableItem {
    /**
     * Type d'unité représentant un guerrier, qui coûte 10 pièces d'or.
     */
    WARRIOR("Guerrier", 10),
    UNDEAD_SPECIAL("Unité mort vivante", 10, Race.UNDEAD);

    /**
     * Nom d'affichage du type d'unité.
     */
    private final String displayName;

    /**
     * Prix de l'unité en pièces d'or.
     */
    private final int price;

    /**
     * Race qui peut construire cette unité.
     */
    private final Race accessibleByRace;


    /**
     * Constructeur de l'énumération UnitType accessible par toutes les races.
     *
     * @param displayName Le nom d'affichage du type d'unité.
     * @param price       Le prix de l'unité en pièces d'or.
     */
    UnitType(String displayName, int price) {
        this(displayName, price, null);
    }

    /**
     * Constructeur de l'énumération UnitType accessible par une race spécifiée.
     *
     * @param displayName      Le nom d'affichage du type d'unité.
     * @param price            Le prix de l'unité en pièces d'or.
     * @param accessibleByRace La race qui peut construire cette unité.
     */
    UnitType(String displayName, int price, Race accessibleByRace) {
        this.displayName = displayName;
        this.price = price;
        this.accessibleByRace = accessibleByRace;
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

    /**
     * Méthode pour obtenir la race qui peut construire cette unité.
     *
     * @return La race qui peut construire cette unité.
     */
    public Race getAccessibleByRace() {
        return this.accessibleByRace;
    }
}
