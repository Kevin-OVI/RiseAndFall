package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Race;

/**
 * Classe principale du jeu RiseAndFall coté client.
 * Stocke le joueur actuel et les opérations avec lui.
 */
public class RiseAndFall {
    /**
     * Instance du joueur actuel.
     * Il n'y a qu'un seul joueur dans le jeu.
     */
    private static ClientPlayer player;

    /**
     * Méthode pour obtenir le joueur actuel.
     *
     * @return Le joueur actuel.
     */
    public static ClientPlayer getPlayer() {
        return player;
    }

    /**
     * Méthode pour créer un nouveau joueur.
     *
     * @param race La race choisie par le joueur.
     */
    public static void createPlayer(Race race) {
        player = new ClientPlayer(race);
    }

    /**
     * Méthode pour réinitialiser le joueur.
     * Utilisée pour réinitialiser le joueur après une partie.
     */
    public static void resetPlayer() {
        player = null;
    }
}
