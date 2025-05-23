package fr.butinfoalt.riseandfall.front.GameList;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.packets.PacketCreateOrJoinGame;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

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
            try {
                RiseAndFall.getClient().sendPacket(new PacketCreateOrJoinGame(ServerData.getRaces().get(1), gameId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
