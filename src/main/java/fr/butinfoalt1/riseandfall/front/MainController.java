package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt1.riseandfall.gamelogic.Player;
import javafx.fxml.FXML;

/**
 * Contrôleur pour la vue principale de l'application.
 */
public class MainController {
    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour ouvrir la page de description.
     */
    @FXML
    public void switchToDescriptionPage() {
        DescriptionStage descriptionPage = new DescriptionStage();
        descriptionPage.show();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour afficher la page des ordres.
     */
    @FXML
    public void switchToOrders() {
        RiseAndFallApplication.switchToView(View.ORDERS);
        OrderController orderController = View.ORDERS.getController();
        orderController.loadPendingOrders();
    }

    @FXML
    private void handleEndTurn() {
        Player.SINGLE_PLAYER.executeOrders();
    }
}
