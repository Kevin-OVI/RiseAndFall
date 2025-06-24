package fr.butinfoalt.riseandfall.front.game;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class VictoryScreenController implements ViewController {
    @FXML
    private void onReturnToMainMenu() {
        // TODO : Faire quitter la partie pour autoriser à rejoindre une nouvelle coté serveur
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.REQUEST_GAME_LIST));
        } catch (IOException e) {
            LogManager.logError("Failed to send game list request packet", e);
            return;
        }
        RiseAndFallApplication.switchToView(View.LOADING);
    }

    @FXML
    private ImageView backgroundImageView;

    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }
}
