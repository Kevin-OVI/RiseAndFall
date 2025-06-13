package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;

/**
 * Interface pour la désérialisation des données de jeu.
 * Permet de récupérer des instances spécifiques au client ou au serveur de manière générique.
 */
public interface DataDeserializer {
    /**
     * Récupère un joueur à partir de son identifiant.
     *
     * @param playerId L'identifiant du joueur à récupérer.
     * @return Le joueur récupéré.
     */
    Player getPlayerById(int playerId);
}
