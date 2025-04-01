package fr.butinfoalt1.riseandfall.front;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OrdreController {
    @FXML
    private void switchToHello() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        RiseAndFallApplication.getMainWindow().getScene().setRoot(loader.load());
    }
}
