package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.io.IOException;

/**
 * Représente une race disponible dans le jeu.
 * Chaque race possède des caractéristiques et avantages spécifiques.
 */
public class Race implements Identifiable, NamedItem, ISerializable {
    /**
     * L'identifiant de la race dans la base de données.
     */
    private final int id;

    /**
     * Le nom de la race.
     */
    private final String name;

    /**
     * La description de la race.
     */
    private final String description;

    /**
     * Le multiplicateur à appliquer sur tous les gains d'or des joueurs ayant cette race.
     */
    private final float goldMultiplier;

    /**
     * Le multiplicateur à appliquer sur tous les gains d'intelligence des joueurs ayant cette race.
     */
    private final float intelligenceMultiplier;

    /**
     * Le multiplicateur à appliquer sur les dégâts infligés par les unités des joueurs ayant cette race.
     */
    private final float damageMultiplier;

    /**
     * Le multiplicateur à appliquer sur tous les points de vie qu'ont les unités des joueurs ayant cette race.
     */
    private final float healthMultiplier;

    /**
     * Constructeur de la classe Race à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param id                     L'identifiant de la race dans la base de données.
     * @param name                   Le nom de la race.
     * @param description            La description de la race.
     * @param goldMultiplier         Le multiplicateur à appliquer sur tous les gains d'or des joueurs ayant cette race.
     * @param intelligenceMultiplier Le multiplicateur à appliquer sur tous les gains d'intelligence des joueurs ayant cette race.
     * @param damageMultiplier       Le multiplicateur à appliquer sur les dégâts infligés par les unités des joueurs ayant cette race.
     * @param healthMultiplier       Le multiplicateur à appliquer sur tous les points de vie qu'ont les unités des joueurs ayant cette race.
     */
    public Race(int id, String name, String description, float goldMultiplier, float intelligenceMultiplier, float damageMultiplier, float healthMultiplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goldMultiplier = goldMultiplier;
        this.intelligenceMultiplier = intelligenceMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.healthMultiplier = healthMultiplier;
    }

    /**
     * Contructeur de la classe Race à partir de données sérialisées.
     * Il est utilisé sur le client pour désérialiser les données provenant du serveur.
     *
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public Race(ReadHelper readHelper) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
        this.goldMultiplier = readHelper.readFloat();
        this.intelligenceMultiplier = readHelper.readFloat();
        this.damageMultiplier = readHelper.readFloat();
        this.healthMultiplier = readHelper.readFloat();
    }

    /**
     * Méthode pour obtenir l'identifiant de la race dans la base de données.
     *
     * @return L'identifiant de la race dans la base de données
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Méthode pour obtenir le nom d'affichage de la race.
     *
     * @return Le nom d'affichage de la race.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Méthode pour obtenir la description de la race.
     *
     * @return La description de la race.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Méthode pour obtenir le multiplicateur à appliquer sur tous les gains d'or des joueurs ayant cette race.
     * @return Le multiplicateur à appliquer sur tous les gains d'or des joueurs ayant cette race.
     */
    public float getGoldMultiplier() {
        return this.goldMultiplier;
    }

    /**
     * Méthode pour obtenir le multiplicateur à appliquer sur tous les gains d'intelligence des joueurs ayant cette race.
     * @return Le multiplicateur à appliquer sur tous les gains d'intelligence des joueurs ayant cette race.
     */
    public float getIntelligenceMultiplier() {
        return this.intelligenceMultiplier;
    }

    /**
     * Méthode pour obtenir le multiplicateur à appliquer sur les dégâts infligés par les unités des joueurs ayant cette race.
     * @return Le multiplicateur à appliquer sur les dégâts infligés par les unités des joueurs ayant cette race.
     */
    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    /**
     * Méthode pour obtenir le multiplicateur à appliquer sur tous les points de vie qu'ont les unités des joueurs ayant cette race.
     * @return Le multiplicateur à appliquer sur tous les points de vie qu'ont les unités des joueurs ayant cette race.
     */
    public float getHealthMultiplier() {
        return this.healthMultiplier;
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeString(this.description);
        writeHelper.writeFloat(this.goldMultiplier);
        writeHelper.writeFloat(this.intelligenceMultiplier);
        writeHelper.writeFloat(this.damageMultiplier);
        writeHelper.writeFloat(this.healthMultiplier);
    }

    @Override
    public String toString() {
        return new ToStringFormatter("Race")
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("goldMultiplier", this.goldMultiplier)
                .add("intelligenceMultiplier", this.intelligenceMultiplier)
                .add("damageMultiplier", this.damageMultiplier)
                .add("healthMultiplier", this.healthMultiplier)
                .build();
    }
}
