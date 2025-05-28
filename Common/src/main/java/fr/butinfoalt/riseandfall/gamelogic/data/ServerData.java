package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.util.List;

/**
 * Classe qui contient les données statiques du serveur.
 * Ces données sont envoyées au client lors de la connexion.
 * Elles contiennent les races, les types d'unités et les types de bâtiments.
 */
public final class ServerData {
    /**
     * Liste des races
     */
    private static List<Race> races;
    /**
     * Liste des bâtiments
     */
    private static List<BuildingType> buildingTypes;
    /**
     * Liste des unités
     */
    private static List<UnitType> unitTypes;

    /**
     * On interdit la création d'instances de cette classe.
     */
    private ServerData() {
    }

    /**
     * Initialise les données statiques du serveur.
     * Cette méthode est appelé sur le serveur au démarrage et
     * sur le client lors de la réception du paquet de données du serveur.
     *
     * @param races         Liste des races
     * @param buildingTypes Liste des types de bâtiments
     * @param unitTypes     Liste des types d'unités
     */
    public static void init(List<Race> races, List<BuildingType> buildingTypes, List<UnitType> unitTypes) {
        ServerData.races = races;
        ServerData.buildingTypes = buildingTypes;
        ServerData.unitTypes = unitTypes;

        LogManager.logMessage("%d races, %d types de bâtiments et %d types d'unités chargées".formatted(races.size(), buildingTypes.size(), unitTypes.size()));
    }

    public static List<Race> getRaces() {
        return races;
    }

    public static List<BuildingType> getBuildingTypes() {
        return buildingTypes;
    }

    public static List<UnitType> getUnitTypes() {
        return unitTypes;
    }
}
