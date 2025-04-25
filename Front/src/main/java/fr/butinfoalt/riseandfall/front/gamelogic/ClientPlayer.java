package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.front.MainController;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.Race;

/**
 * Représente un joueur dans le jeu du coté client.
 * Hérite de la classe Player commune.
 */
public class ClientPlayer extends Player {
    /**
     * Instance unique du joueur (singleton).
     * Utilisé pour représenter un joueur unique dans le jeu.
     * Ce champ sera retiré lors de l'implémentation du mode multijoueur.
     */
    // TODO : Rendre la race sélectionnable par un menu
    public static final ClientPlayer SINGLE_PLAYER = new ClientPlayer(Race.HUMAN);

    public ClientPlayer(Race race) {
        super(race);
    }

    @Override
    public void executeOrders() {
        super.executeOrders();
        MainController mainController = View.MAIN.getController();
        mainController.updateFields();
    }
}
