package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.io.IOException;

/**
 * Représente un type d'unité disponible dans le jeu.
 */
public class UnitType implements Identifiable, PurchasableItem, ISerializable {
    /**
     * L'identifiant de l'unité dans la base de données.
     */
    public final int id;

    /**
     * Nom d'affichage du type d'unité.
     */
    private final String name;

    /**
     * Description de l'unité.
     */
    private final String description;

    /**
     * Prix de l'unité en or.
     */
    private final int price;

    /**
     * La quantité d'intelligence requise pour construire l'unité.
     */
    private final int requiredIntelligence;

    /**
     * Points de vie de l'unité.
     */
    private final int health;

    /**
     * Dégâts infligés par l'unité.
     */
    private final int damage;

    /**
     * Race qui peut construire cette unité.
     */
    private final Race accessibleByRace;


    /**
     * Constructeur du type UnitType à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param id                   L'identifiant de l'unité dans la base de données.
     * @param name                 Le nom d'affichage du type d'unité.
     * @param description          La description de l'unité
     * @param price                Le prix de l'unité en or.
     * @param requiredIntelligence La quantité d'intelligence requise pour construire l'unité.
     * @param health               Le nombre de points de vie de l'unité
     * @param damage               La quantité de dégâts qu'inflige l'unité
     * @param accessibleByRace     La race qui peut construire cette unité.
     */
    public UnitType(int id, String name, String description, int price, int requiredIntelligence, int health, int damage, Race accessibleByRace) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.requiredIntelligence = requiredIntelligence;
        this.health = health;
        this.damage = damage;
        this.accessibleByRace = accessibleByRace;
    }

    /**
     * Contructeur de la classe UnitType à partir de données sérialisées.
     * Il est utilisé sur le client pour désérialiser les données provenant du serveur.
     *
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @param races      Un tableau contenant les races déjà désérialisées
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public UnitType(ReadHelper readHelper, Race[] races) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
        this.price = readHelper.readInt();
        this.requiredIntelligence = readHelper.readInt();
        this.health = readHelper.readInt();
        this.damage = readHelper.readInt();
        int unitAccessibleRaceId = readHelper.readInt();
        this.accessibleByRace = Identifiable.getByIdOrNull(races, unitAccessibleRaceId);
    }

    /**
     * Méthode pour obtenir l'identifiant de l'unité dans la base de données.
     *
     * @return L'identifiant de l'unité dans la base de données.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Méthode pour obtenir le nom d'affichage du type d'unité.
     *
     * @return Le nom d'affichage du type d'unité.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Méthode pour obtenir la description de l'unité.
     *
     * @return La description de l'unité.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Méthode pour obtenir le prix de l'unité en pièces d'or.
     *
     * @return Le prix de l'unité en or.
     */
    @Override
    public int getPrice() {
        return this.price;
    }

    /**
     * Méthode pour obtenir la quantité d'intelligence requise pour construire l'unité.
     *
     * @return La quantité d'intelligence requise pour construire l'unité.
     */
    @Override
    public int getRequiredIntelligence() {
        return this.requiredIntelligence;
    }

    /**
     * Méthode pour obtenir les points de vie de l'unité.
     *
     * @return Les points de vie de l'unité.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Méthode pour obtenir les dégâts infligés par l'unité.
     *
     * @return Les dégâts infligés par l'unité.
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * Méthode pour obtenir la race qui peut construire cette unité.
     *
     * @return La race qui peut construire cette unité.
     */
    public Race getAccessibleByRace() {
        return this.accessibleByRace;
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeString(this.description);
        writeHelper.writeInt(this.price);
        writeHelper.writeInt(this.requiredIntelligence);
        writeHelper.writeInt(this.health);
        writeHelper.writeInt(this.damage);
        writeHelper.writeInt(this.accessibleByRace != null ? this.accessibleByRace.getId() : -1);
    }

    @Override
    public String toString() {
        return new ToStringFormatter("UnitType")
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("price", this.price)
                .add("requiredIntelligence)", this.requiredIntelligence)
                .add("health", this.health)
                .add("damage", this.damage)
                .add("accessibleByRace", this.accessibleByRace)
                .build();
    }
}
