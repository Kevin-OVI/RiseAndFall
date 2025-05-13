package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente les différentes races disponibles dans le jeu.
 * Chaque race possède des caractéristiques et avantages spécifiques.
 */
public class Race implements Identifiable, NamedItem, ISerializable {
    private final int id;
    private final String name;
    private final String description;
    private final float goldMultiplier;
    private final float intelligenceMultiplier;
    private final float damageMultiplier;
    private final float healthMultiplier;


    public Race(int id, String name, String description, float goldMultiplier, float intelligenceMultiplier, float damageMultiplier, float healthMultiplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goldMultiplier = goldMultiplier;
        this.intelligenceMultiplier = intelligenceMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.healthMultiplier = healthMultiplier;
    }

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
     * Retourne l'identifiant de la race dans la base de données.
     *
     * @return L'identifiant de la race dans la base de données
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Renvoie le nom d'affichage de la race.
     *
     * @return Le nom d'affichage de la race.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Renvoie la description de la race.
     *
     * @return La description de la race.
     */
    public String getDescription() {
        return this.description;
    }

    public float getGoldMultiplier() {
        return goldMultiplier;
    }

    public float getIntelligenceMultiplier() {
        return intelligenceMultiplier;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
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
        return "Race{id=%d, name='%s', description='%s', goldMultiplier='%f' , intelligenceMultiplier='%f' , damageMultiplier='%f', healthMultiplier='%f' }".formatted(id, name, description,goldMultiplier, intelligenceMultiplier, damageMultiplier, healthMultiplier);
    }
}
