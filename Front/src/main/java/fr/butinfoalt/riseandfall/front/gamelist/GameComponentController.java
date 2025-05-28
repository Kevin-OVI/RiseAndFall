package fr.butinfoalt.riseandfall.front.gamelist;

import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.network.packets.PacketCreateOrJoinGame;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
                RiseAndFall.getClient().sendPacket(new PacketCreateOrJoinGame(((GameListController)View.GAME_LIST.getController()).raceChoiceBox.getValue(), gameId));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du paquet de création ou de jointure de partie : ", e);
            }
        }
    }
