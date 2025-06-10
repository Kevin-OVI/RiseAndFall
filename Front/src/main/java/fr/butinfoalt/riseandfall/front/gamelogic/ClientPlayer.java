package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;

/**
 * Représente un joueur dans le jeu du coté client.
 * Hérite de la classe Player commune.
 */
public class ClientPlayer extends Player {
    /**
     * Constructeur de la classe Player.
     *
     * @param id   L'identifiant unique du joueur.
     * @param race La race choisie par le joueur.
     */
    public ClientPlayer(int id, Race race) {
        super(id, race);
    }
}
