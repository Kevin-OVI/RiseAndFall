package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.io.IOException;

/**
 * Représente un type de bâtiment disponible dans le jeu.
 */
public class BuildingType implements Identifiable, PurchasableItem, ISerializable {
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
     * Prix du bâtiment en or.
     */
    private final int priceGold;

    /**
     * Prix du bâtiment en intelligence
     */
    private final int priceIntelligence;

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
     * Constructeur de la classe BuildingType à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param id                     L'identifiant du type de bâtiment dans la base de données
     * @param name                   Le nom d'affichage du type de bâtiment.
     * @param description            La description du bâtiment.
     * @param priceGold              Le prix du bâtiment en or.
     * @param priceIntelligence      Le prix du bâtiment en intelligence
     * @param goldProduction         La production d'or du bâtiment par tour.
     * @param intelligenceProduction La production d'intelligence du bâtiment par tour.
     * @param maxUnits               Le nombre maximum d'unités pouvant être construites par ce type de bâtiment par tour.
     * @param initialAmount          Le nombre initial de bâtiments de ce type.
     * @param accessibleByRace       La race qui peut construire ce bâtiment.
     */
    public BuildingType(int id, String name, String description, int priceGold, int priceIntelligence, int goldProduction, int intelligenceProduction, int maxUnits, int initialAmount, Race accessibleByRace) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceGold = priceGold;
        this.priceIntelligence = priceIntelligence;
        this.goldProduction = goldProduction;
        this.intelligenceProduction = intelligenceProduction;
        this.maxUnits = maxUnits;
        this.initialAmount = initialAmount;
        this.accessibleByRace = accessibleByRace;
    }

    /**
     * Contructeur de la classe BuildingType à partir de données sérialisées.
     * Il est utilisé sur le client pour désérialiser les données provenant du serveur.
     *
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @param races      Un tableau contenant les races déjà désérialisées
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public BuildingType(ReadHelper readHelper, Race[] races) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
        this.priceGold = readHelper.readInt();
        this.priceIntelligence = readHelper.readInt();
        this.goldProduction = readHelper.readInt();
        this.intelligenceProduction = readHelper.readInt();
        this.maxUnits = readHelper.readInt();
        this.initialAmount = readHelper.readInt();
        int unitAccessibleRaceId = readHelper.readInt();
        this.accessibleByRace = Identifiable.getByIdOrNull(races, unitAccessibleRaceId);
    }

    /**
     * Méthode pour obtenir l'identifiant du bâtiment dans la base de données.
     *
     * @return L'identifiant du bâtiment dans la base de données.
     */
    @Override
    public int getId() {
        return this.id;
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
     * Méthode pour obtenir la description du type de bâtiment.
     *
     * @return La description du type de bâtiment.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Méthode pour obtenir le prix du bâtiment en or.
     *
     * @return Le prix du bâtiment en or.
     */
    @Override
    public int getPriceGold() {
        return this.priceGold;
    }

    /**
     * Méthode pour obtenir le prix du bâtiment en intelligence.
     *
     * @return Le prix du bâtiment en intelligence.
     */
    @Override
    public int getPriceIntelligence() {
        return this.priceIntelligence;
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

    /**
     * Méthode pour obtenir la race qui peut construire des bâtiments de ce type.
     *
     * @return La race qui peut construire ce type de bâtiment, ou null si toutes les races le peuvent.
     */
    public Race getAccessibleByRace() {
        return this.accessibleByRace;
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeString(this.description);
        writeHelper.writeInt(this.priceGold);
        writeHelper.writeInt(this.priceIntelligence);
        writeHelper.writeInt(this.goldProduction);
        writeHelper.writeInt(this.intelligenceProduction);
        writeHelper.writeInt(this.maxUnits);
        writeHelper.writeInt(this.initialAmount);
        writeHelper.writeInt(this.accessibleByRace == null ? -1 : this.accessibleByRace.getId());
    }

    @Override
    public String toString() {
        return new ToStringFormatter("BuildingType")
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("priceGold", this.priceGold)
                .add("priceIntelligence", this.priceIntelligence)
                .add("goldProduction", this.goldProduction)
                .add("intelligenceProduction", this.intelligenceProduction)
                .add("maxUnits", this.maxUnits)
                .add("initialAmount", this.initialAmount)
                .add("accessibleByRace", this.accessibleByRace)
                .build();
    }
}
