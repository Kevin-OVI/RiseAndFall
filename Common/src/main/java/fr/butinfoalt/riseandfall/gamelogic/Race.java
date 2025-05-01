package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente les différentes races disponibles dans le jeu.
 * Chaque race possède des caractéristiques et avantages spécifiques.
 */
public class Race implements NamedItem, ISerializable {
    private final int id;
    private final String name;
    private final String description;

    public Race(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Race(ReadHelper readHelper) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.description = readHelper.readString();
    }

    /**
     * Retourne l'identifiant de la race dans la base de données.
     *
     * @return L'identifiant de la race dans la base de données
     */
    public int getId() {
        return this.id;
    }

    /**
     * Renvoie le nom d'affichage de la race.
     *
     * @return Le nom d'affichage de la race.
     */
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

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeString(this.description);
    }
}
