package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.common.IDeserializer;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.util.List;

/**
 * Un paquet envoyé au client lors de sa connexion au serveur.
 * Il contient les données statiques du serveur, c'est-à-dire les races,
 * les types d'unités et les types de bâtiments.
 */
public class PacketServerData<G extends Game> implements IPacket {
    /**
     * Liste des races
     */
    private final List<Race> races;
    /**
     * Liste des types d'unités
     */
    private final List<UnitType> unitTypes;
    /**
     * Liste des types de bâtiments
     */
    private final List<BuildingType> buildingTypes;

    /**
     * Liste des parties de jeu en attente
     */
    private final List<G> games;

    /**
     * Constructeur du paquet de données du serveur
     *
     * @param raceList      Liste des races
     * @param unitTypes     Liste des types d'unités
     * @param buildingTypes Liste des types de bâtiments
     */
    public PacketServerData(List<Race> raceList, List<UnitType> unitTypes, List<BuildingType> buildingTypes, List<G> games) {
        this.races = raceList;
        this.unitTypes = unitTypes;
        this.buildingTypes = buildingTypes;
        this.games = games;
    }

    /**
     * Constructeur du paquet de données du serveur pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketServerData(ReadHelper readHelper, IDeserializer<G> gameDeserializer) throws IOException {
        this.races = readHelper.readSerializableList(Race::new);
        this.unitTypes = readHelper.readSerializableList(UnitType::new, this.races);
        this.buildingTypes = readHelper.readSerializableList(BuildingType::new, this.races);
        this.games = readHelper.readSerializableList(gameDeserializer);
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeSerializableList(this.races);
        writeHelper.writeSerializableList(this.unitTypes);
        writeHelper.writeSerializableList(this.buildingTypes);
        writeHelper.writeSerializableList(this.games);
    }

    /**
     * Récupère la liste des races
     *
     * @return La liste des races
     */
    public List<Race> getRaces() {
        return this.races;
    }

    /**
     * Récupère la liste des types d'unités
     *
     * @return La liste des types d'unités
     */
    public List<UnitType> getUnitTypes() {
        return this.unitTypes;
    }

    /**
     * Récupère la liste des types de bâtiments
     *
     * @return La liste des types de bâtiments
     */
    public List<BuildingType> getBuildingTypes() {
        return this.buildingTypes;
    }

    /**
     * Récupère la liste des parties de jeu en attente
     *
     * @return La liste des parties de jeu en attente
     */
    public List<G> getGames() {
        return this.games;
    }
}
