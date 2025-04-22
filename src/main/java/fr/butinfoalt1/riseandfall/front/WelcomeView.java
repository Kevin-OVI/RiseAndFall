package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.description.DescriptionStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.awt.*;

public class WelcomeView {
    @FXML
    public void play(ActionEvent actionEvent) {
        RiseAndFallApplication.switchToView(View.MAIN);
    }
}
