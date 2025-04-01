package fr.butinfoalt1.riseandfall.front;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RiseAndFallController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}