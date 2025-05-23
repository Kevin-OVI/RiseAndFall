package fr.butinfoalt.riseandfall.front.GameList;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;
import java.util.List;

public class GameListController {
    @FXML
    private VBox listContainer;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    public void initialize(List<Game> games) {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
        for (Game game : games) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/butinfoalt/riseandfall/front/components/game-component.fxml"));
                Node partyNode = loader.load();

                GameComponentController controller = loader.getController();
                controller.init(game.getId(), game.getName(), game.getTurnInterval(), 3, 30);

                listContainer.getChildren().add(partyNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
