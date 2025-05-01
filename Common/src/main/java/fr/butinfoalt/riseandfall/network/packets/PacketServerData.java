package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Un paquet envoyé au client lors de sa connexion au serveur.
 * Il contient les données statiques du serveur, c'est-à-dire les races,
 * les types d'unités et les types de bâtiments.
 */
public class PacketServerData implements IPacket {
    /**
     * Liste des races
     */
    private final Race[] races;
    /**
     * Liste des types d'unités
     */
    private final UnitType[] unitTypes;
    /**
     * Liste des types de bâtiments
     */
    private final BuildingType[] buildingTypes;

    /**
     * Constructeur du paquet de données du serveur
     *
     * @param raceList      Liste des races
     * @param unitTypes     Liste des types d'unités
     * @param buildingTypes Liste des types de bâtiments
     */
    public PacketServerData(Race[] raceList, UnitType[] unitTypes, BuildingType[] buildingTypes) {
        this.races = raceList;
        this.unitTypes = unitTypes;
        this.buildingTypes = buildingTypes;
    }

    /**
     * Constructeur du paquet de données du serveur pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketServerData(ReadHelper readHelper) throws IOException {
        this.races = readHelper.readSerializableArray(Race.class, Race::new);
        this.unitTypes = readHelper.readSerializableArray(UnitType.class, UnitType::new, this.races);
        this.buildingTypes = readHelper.readSerializableArray(BuildingType.class, BuildingType::new, this.races);
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeSerializableArray(this.races);
        writeHelper.writeSerializableArray(this.unitTypes);
        writeHelper.writeSerializableArray(this.buildingTypes);
    }

    /**
     * Récupère la liste des races
     *
     * @return La liste des races
     */
    public Race[] getRaces() {
        return this.races;
    }

    /**
     * Récupère la liste des types d'unités
     *
     * @return La liste des types d'unités
     */
    public UnitType[] getUnitTypes() {
        return this.unitTypes;
    }

    /**
     * Récupère la liste des types de bâtiments
     *
     * @return La liste des types de bâtiments
     */
    public BuildingType[] getBuildingTypes() {
        return this.buildingTypes;
    }
}
