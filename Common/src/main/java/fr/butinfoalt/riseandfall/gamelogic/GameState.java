package fr.butinfoalt.riseandfall.gamelogic;

/**
 * Enumération représentant les différents états d'une partie.
 */
public enum GameState {
    /**
     * La partie est en attente de joueurs.
     */
    WAITING,
    /**
     * La partie est en cours.
     */
    RUNNING,
    /**
     * La partie est terminée.
     */
    ENDED;
}
