package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Game;

import java.util.Arrays;
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
     * Liste des types de bâtiments
     */
    private static List<BuildingType> buildingTypes;
    /**
     * Liste des types d'unités
     */
    private static List<UnitType> unitTypes;

    /**
     * Liste des games en attente
     */
    private static List<Game> games;

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
    public static void init(List<Race> races, List<BuildingType> buildingTypes, List<UnitType> unitTypes, List<Game> games) {
        ServerData.races = races;
        ServerData.buildingTypes = buildingTypes;
        ServerData.unitTypes = unitTypes;
        ServerData.games = games;

        System.out.println("Races: " + races.toString());
        System.out.println("Building types: " + buildingTypes.toString());
        System.out.println("Unit types: " + unitTypes.toString());
        System.out.println("Games: " + games.toString());
    }

    /**
     * Récupère la liste des races
     */
    public static List<Race> getRaces() {
        return races;
    }

    /**
     * Récupère la liste des types de bâtiments
     */
    public static List<BuildingType> getBuildingTypes() {
        return buildingTypes;
    }

    /**
     * Récupère la liste des types d'unités
     */
    public static List<UnitType> getUnitTypes() {
        return unitTypes;
    }

    /**
     * Récupère la liste des games
     */
    public static List<Game> getGames() {
        return games;
    }
}
