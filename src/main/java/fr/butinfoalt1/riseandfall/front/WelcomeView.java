package fr.butinfoalt1.riseandfall.front;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WelcomeView {
    @FXML
    public void play(ActionEvent actionEvent) {
        RiseAndFallApplication.switchToView(View.MAIN);

        MainController mainController = View.MAIN.getController();
        mainController.updateFields();
    }
}
