package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.gamelogic.building.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.Player;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateBuilding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class OrderController {
    @FXML
    private void switchToHello() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Stage stage = RiseAndFallApplication.getMainWindow();
        stage.getScene().setRoot(loader.load());
    }

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
