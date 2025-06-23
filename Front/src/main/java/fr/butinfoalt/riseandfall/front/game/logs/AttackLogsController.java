package fr.butinfoalt.riseandfall.front.game.logs;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttackLogsController implements ViewController {
    public VBox root;
    public ImageView backgroundImageView;
    public VBox listContainer;

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);
    }

    @FXML
    public void switchBack() {
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        int currentTurn = RiseAndFall.getGame().getCurrentTurn();
        this.listContainer.getChildren().clear();
        Map<Integer, List<Player>> eliminatedPlayers = RiseAndFall.getGame().getAllPlayers().stream()
                .filter(clientPlayer -> clientPlayer.getEliminationTurn() != -1)
                .collect(Collectors.groupingBy(Player::getEliminationTurn, Collectors.toList()));

        for (int i = 1; i < currentTurn; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(AttackLogsController.class.getResource("/fr/butinfoalt/riseandfall/front/components/attack-log-component.fxml"));
                Node node = loader.load();

                AttackLogsListItemController controller = loader.getController();
                controller.init(i, RiseAndFall.getPlayer(), RiseAndFall.getGame().getAttackResults(i), eliminatedPlayers.getOrDefault(i, Collections.emptyList()));

                this.listContainer.getChildren().addFirst(node);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }
}
