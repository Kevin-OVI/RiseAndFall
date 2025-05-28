package fr.butinfoalt.riseandfall.front.gamelist;

import fr.butinfoalt.riseandfall.front.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameComponentController {

        @FXML
        private Label partyNameLabel;

        @FXML
        private Label dayDuration;

        @FXML
        private Label currentPlayer;

        @FXML
        private Label maxPlayer;

        private int gameId;

        public void init(int gameId, String name, int dayDuration, int currentPlayer, int maxPlayer) {
            partyNameLabel.setText(name);
            this.dayDuration.setText(String.valueOf(dayDuration));
            this.currentPlayer.setText(String.valueOf(currentPlayer));
            this.maxPlayer.setText(String.valueOf(maxPlayer));
            this.gameId = gameId;
        }

        @FXML
        private void onJoinClicked() {
            ((GameListController) View.GAME_LIST.getController()).tryJoinGame(this.gameId);
        }
    }
