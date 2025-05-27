package fr.butinfoalt.riseandfall.front.GameList;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;
import java.util.List;

public class GameListController {
    /**
     * Champ de sélection de race.
     */
    @FXML
    public ChoiceBox<Race> raceChoiceBox;

    @FXML
    private VBox listContainer;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    public void initialize() {
        this.listContainer.getChildren().clear();
        this.raceChoiceBox.getItems().clear();
        this.raceChoiceBox.getItems().addAll(ServerData.getRaces());
        this.raceChoiceBox.setConverter(new NamedItemStringConverter<>());
        this.raceChoiceBox.setValue(ServerData.getRaces().getFirst());
        for (Race race : ServerData.getRaces()) {
            if (race.getName().equals("Humain")) {
                this.raceChoiceBox.setValue(race);
            }
        }

        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
        for (Game game : ServerData.getGames()) {
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
