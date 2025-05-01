package fr.butinfoalt.riseandfall.gamelogic.map;

import fr.butinfoalt.riseandfall.gamelogic.Race;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Enum représentant les types d'unités disponibles dans le jeu.
 * Chaque type d'unité a un nom d'affichage et un prix.
 */
public class UnitType implements PurchasableItem, ISerializable {
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
     * Prix de l'unité en pièces d'or.
     */
    private final int price;

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
     * Constructeur de l'énumération UnitType accessible par toutes les races.
     *
     * @param id    L'identifiant de l'unité dans la base de données.
     * @param name  Le nom d'affichage du type d'unité.
     * @param price Le prix de l'unité en pièces d'or.
     */
    public UnitType(int id, String name, String description, int price, int health, int damage) {
        this(id, name, description, price, health, damage, null);
    }

    /**
     * Constructeur de l'énumération UnitType accessible par une race spécifiée.
     *
     * @param id               L'identifiant de l'unité dans la base de données.
     * @param name             Le nom d'affichage du type d'unité.
     * @param price            Le prix de l'unité en pièces d'or.
     * @param accessibleByRace La race qui peut construire cette unité.
     */
    public UnitType(int id, String name, String description, int price, int health, int damage, Race accessibleByRace) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.health = health;
        this.damage = damage;
        this.accessibleByRace = accessibleByRace;
    }

    public UnitType(ReadHelper readHelper, Race[] races) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
        this.price = readHelper.readInt();
        this.health = readHelper.readInt();
        this.damage = readHelper.readInt();
        int unitAccessibleRaceId = readHelper.readInt();
        this.accessibleByRace = unitAccessibleRaceId == -1 ? null : races[unitAccessibleRaceId];
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
     * @return Le prix de l'unité en pièces d'or.
     */
    @Override
    public int getPrice() {
        return this.price;
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
        writeHelper.writeInt(this.health);
        writeHelper.writeInt(this.damage);
        writeHelper.writeInt(this.accessibleByRace != null ? this.accessibleByRace.getId() : -1);
    }
}
