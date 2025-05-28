package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.util.List;

/**
 * Classe qui contient les données statiques du serveur.
 * Ces données sont envoyées au client lors de la connexion.
 * Elles contiennent les races, les types d'unités et les types de bâtiments.
 *
 * @param races         Liste des races
 * @param buildingTypes Liste des types de bâtiments
 * @param unitTypes     Liste des types d'unités
 * @param games         Liste des games en attente
 */
public record ServerData<G extends Game>(List<Race> races, List<BuildingType> buildingTypes, List<UnitType> unitTypes,
                                         List<G> games) {
    /**
     * Instance unique de ServerData.
     * Utilisée pour accéder aux données statiques du serveur depuis n'importe où dans le code, mais n'a pas de type spécifique pour <G>.
     */
    private static ServerData<?> instance;

    /**
     * Initialise les données statiques du serveur.
     * Cette méthode est appelé sur le serveur au démarrage et
     * sur le client lors de la réception du paquet de données du serveur.
     *
     * @param races         Liste des races
     * @param buildingTypes Liste des types de bâtiments
     * @param unitTypes     Liste des types d'unités
     * @param games         Liste des parties
     */
    public ServerData(List<Race> races, List<BuildingType> buildingTypes, List<UnitType> unitTypes, List<G> games) {
        this.races = races;
        this.buildingTypes = buildingTypes;
        this.unitTypes = unitTypes;
        this.games = games;
        instance = this;

        LogManager.logMessage("%d races, %d types de bâtiments, %d types d'unités et %d parties chargées".formatted(races.size(), buildingTypes.size(), unitTypes.size(), games.size()));
    }

    public static ServerData<?> getInstance() {
        return instance;
    }
}
