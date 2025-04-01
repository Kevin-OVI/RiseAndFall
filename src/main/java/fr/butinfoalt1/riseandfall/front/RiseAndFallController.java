package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.description.DescriptionStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class RiseAndFallController {
    @FXML
    public Button switchToOrderButton;

    @FXML
    public void switchToDescriptionPage(ActionEvent actionEvent) throws IOException {
        DescriptionStage descriptionPage = new DescriptionStage();
        descriptionPage.show();
    }

    @FXML
    public void switchToTest() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ordre.fxml"));
        RiseAndFallApplication.getMainWindow().getScene().setRoot(loader.load());
    }
}