package fr.butinfoalt.riseandfall.gamelogic.map;

import fr.butinfoalt.riseandfall.gamelogic.Race;
import fr.butinfoalt.riseandfall.gamelogic.ServerData;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Enum représentant les types de bâtiments disponibles dans le jeu.
 * Chaque type de bâtiment a un nom d'affichage, un prix, une production d'or et un nombre maximum d'unités.
 */
public class BuildingType implements PurchasableItem, ISerializable {
    /**
     * L'identifiant du bâtiment dans la base de données.
     */
    private final int id;

    /**
     * Nom d'affichage du type de bâtiment.
     */
    private final String name;

    /**
     * Description du bâtiment.
     */
    private final String description;

    /**
     * Prix du bâtiment en pièces d'or.
     */
    private final int price;
    /**
     * Production d'or du bâtiment par tour.
     */
    private final int goldProduction;

    /**
     * Production d'intelligence du bâtiment par tour.
     */
    private final int intelligenceProduction;
    /**
     * Nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     */
    private final int maxUnits;
    /**
     * Nombre initial de bâtiments de ce type.
     */
    private final int initialAmount;

    /**
     * Race qui peut construire ce bâtiment.
     */
    private final Race accessibleByRace;


    /**
     * Constructeur de l'énumération BuildingType accessible par une race et avec une quantité initiale spécifiées.
     *
     * @param name             Le nom d'affichage du type de bâtiment.
     * @param description      La description du bâtiment.
     * @param price            Le prix du bâtiment en pièces d'or.
     * @param goldProduction   La production d'or du bâtiment par tour.
     * @param maxUnits         Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     * @param initialAmount    Le nombre initial de bâtiments de ce type.
     * @param accessibleByRace La race qui peut construire ce bâtiment.
     */
    public BuildingType(int id, String name, String description, int price, int goldProduction, int intelligenceProduction, int maxUnits, int initialAmount, Race accessibleByRace) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.goldProduction = goldProduction;
        this.intelligenceProduction = intelligenceProduction;
        this.maxUnits = maxUnits;
        this.initialAmount = initialAmount;
        this.accessibleByRace = accessibleByRace;
    }

    public BuildingType(ReadHelper readHelper, Race[] races) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
        this.price = readHelper.readInt();
        this.goldProduction = readHelper.readInt();
        this.intelligenceProduction = readHelper.readInt();
        this.maxUnits = readHelper.readInt();
        this.initialAmount = readHelper.readInt();
        int unitAccessibleRaceId = readHelper.readInt();
        this.accessibleByRace = ServerData.getRaceByDbId(races, unitAccessibleRaceId);
    }

    /**
     * Méthode pour obtenir le nom d'affichage du type de bâtiment.
     *
     * @return Le nom d'affichage du type de bâtiment.
     */
    @Override
    public String getName() {
        return this.name;
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
     * Méthode pour obtenir la production d'intelligence du bâtiment par tour.
     *
     * @return La production d'intelligence du bâtiment par tour.
     */
    public int getIntelligenceProduction() {
        return this.intelligenceProduction;
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

    public Race getAccessibleByRace() {
        return this.accessibleByRace;
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeString(this.description);
        writeHelper.writeInt(this.price);
        writeHelper.writeInt(this.goldProduction);
        writeHelper.writeInt(this.intelligenceProduction);
        writeHelper.writeInt(this.maxUnits);
        writeHelper.writeInt(this.initialAmount);
        writeHelper.writeInt(this.accessibleByRace == null ? -1 : this.accessibleByRace.getId());
    }

    @Override
    public String toString() {
        return "BuildingType{id=%d, name='%s', description='%s', price=%d, goldProduction=%d, intelligenceProduction=%d, maxUnits=%d, initialAmount=%d, accessibleByRace=%s}".formatted(id, name, description, price, goldProduction, intelligenceProduction, maxUnits, initialAmount, accessibleByRace);
    }
}
