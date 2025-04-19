package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.gamelogic.Player;
import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateBuilding;
import javafx.fxml.FXML;

/**
 * Contrôleur pour la vue de gestion des ordres.
 */
public class OrderController {
    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton retour.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToPreviousView();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour créer un bâtiment.
     */
    @FXML
    private void addBuildings() {
        // TODO : Get the building type and count from the UI
        int count = 5;
        BuildingType buildingType = BuildingType.HUT;

        OrderCreateBuilding order = new OrderCreateBuilding(buildingType, count);
        // TODO : Check gold resources using order.getPrice()

        Player.SINGLE_PLAYER.addOrder(order);
    }
}
