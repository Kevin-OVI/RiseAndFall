package fr.butinfoalt.riseandfall.front.game.gamelist;

import fr.butinfoalt.riseandfall.front.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameComponentController {
        @FXML
        private Label partyNameLabel;

        @FXML
        private Label turnInterval;

        private int gameId;

        public void init(int gameId, String name, int dayDuration) {
            this.partyNameLabel.setText(name);
            this.turnInterval.setText(String.valueOf(dayDuration));
            this.gameId = gameId;
        }

        @FXML
        private void onJoinClicked() {
            ((GameListController) View.GAME_LIST.getController()).tryJoinGame(this.gameId);
        }
    }
