package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.description.DescriptionStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class RiseAndFallController {
    @FXML
    public Button switchToOrderButton;

    @FXML
    public void switchToDescriptionPage(ActionEvent actionEvent) {
        DescriptionStage descriptionPage = new DescriptionStage();
        descriptionPage.show();
    }

    @FXML
    public void switchToOrders() throws IOException {
        RiseAndFallApplication.switchToView(View.ORDERS);
    }
}