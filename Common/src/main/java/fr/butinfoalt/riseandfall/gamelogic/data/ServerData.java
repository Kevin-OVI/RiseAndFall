package fr.butinfoalt.riseandfall.gamelogic.data;

import java.util.Arrays;

/**
 * Classe qui contient les données statiques du serveur.
 * Ces données sont envoyées au client lors de la connexion.
 * Elles contiennent les races, les types d'unités et les types de bâtiments.
 */
public final class ServerData {
    /**
     * Liste des races
     */
    private static Race[] races;
    /**
     * Liste des types de bâtiments
     */
    private static BuildingType[] buildingTypes;
    /**
     * Liste des types d'unités
     */
    private static UnitType[] unitTypes;

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe.
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
    public static void init(Race[] races, BuildingType[] buildingTypes, UnitType[] unitTypes) {
        ServerData.races = races;
        ServerData.buildingTypes = buildingTypes;
        ServerData.unitTypes = unitTypes;

        System.out.println("Races: " + Arrays.toString(races));
        System.out.println("Building Types: " + Arrays.toString(buildingTypes));
        System.out.println("Unit Types: " + Arrays.toString(unitTypes));
    }

    /**
     * Récupère la liste des races
     */
    public static Race[] getRaces() {
        return races;
    }

    /**
     * Récupère la liste des types de bâtiments
     */
    public static BuildingType[] getBuildingTypes() {
        return buildingTypes;
    }

    /**
     * Récupère la liste des types d'unités
     */
    public static UnitType[] getUnitTypes() {
        return unitTypes;
    }
}
