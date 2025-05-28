package fr.butinfoalt.riseandfall.front.gamelist;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.packets.PacketCreateOrJoinGame;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class GameListController {
    /**
     * Label d'instructions.
     */
    @FXML
    public Label instructions;

    /**
     * Champ de sélection de race.
     */
    @FXML
    public ChoiceBox<Race> raceChoiceBox;

    @FXML
    private VBox listContainer;

    @FXML
    private ImageView backgroundImageView;

    /**
     * Animation de clignotement des instructions.
     */
    private Timeline instructionsBlinkTransition;

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);


        Color startColor = Color.BLACK;
        Color endColor = Color.ORANGERED;

        this.instructionsBlinkTransition = new Timeline(
                new KeyFrame(Duration.millis(150), e -> this.instructions.setTextFill(endColor)),
                new KeyFrame(Duration.millis(300), e -> this.instructions.setTextFill(startColor))
        );
        this.instructionsBlinkTransition.setCycleCount(4);

        this.raceChoiceBox.getItems().clear();
        this.raceChoiceBox.getItems().addAll(ServerData.getRaces());
        this.raceChoiceBox.setConverter(new NamedItemStringConverter<>());
    }

    public void refreshGameList(List<ClientGame> waitingGames) {
        this.listContainer.getChildren().clear();

        for (ClientGame game : waitingGames) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/butinfoalt/riseandfall/front/components/game-component.fxml"));
                Node partyNode = loader.load();

                GameComponentController controller = loader.getController();
                controller.init(game.getId(), game.getName(), game.getTurnInterval());

                this.listContainer.getChildren().add(partyNode);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }

    public void tryJoinGame(int gameId) {
        if (this.raceChoiceBox.getValue() == null) {
            this.instructionsBlinkTransition.play();
            return;
        }

        try {
            RiseAndFall.getClient().sendPacket(new PacketCreateOrJoinGame(this.raceChoiceBox.getValue(), gameId));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de création ou de jointure de partie : ", e);
        }
    }

    public void showError(String message) {
        // TODO : Afficher un message d'erreur à l'utilisateur
    }
}
