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
