package fr.butinfoalt.riseandfall.front.GameList;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
        ServerData<ClientGame> serverData = RiseAndFall.getServerData();
        this.listContainer.getChildren().clear();
        this.raceChoiceBox.getItems().clear();
        this.raceChoiceBox.getItems().addAll(serverData.races());
        this.raceChoiceBox.setConverter(new NamedItemStringConverter<>());
        this.raceChoiceBox.setValue(serverData.races().getFirst());
        for (Race race : serverData.races()) {
            if (race.getName().equals("Humain")) {
                this.raceChoiceBox.setValue(race);
            }
        }

        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
        for (ClientGame game : serverData.games()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/butinfoalt/riseandfall/front/components/game-component.fxml"));
                Node partyNode = loader.load();

                GameComponentController controller = loader.getController();
                controller.init(game.getId(), game.getName(), game.getTurnInterval(), 3, 30);

                listContainer.getChildren().add(partyNode);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }

    public void showError(String message) {
        // TODO : Afficher un message d'erreur à l'utilisateur
    }
}
