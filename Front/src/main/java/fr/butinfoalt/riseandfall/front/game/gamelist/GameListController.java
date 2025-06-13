package fr.butinfoalt.riseandfall.front.game.gamelist;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class GameListController implements ViewController {
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
     * Champ pour le composant racine de la vue.
     */
    @FXML
    public ScrollPane root;

    /**
     * Animation de clignotement des instructions.
     */
    private Timeline instructionsBlinkTransition;

    /**
     * Champ pour le composant du message d'erreur.
     */
    @FXML
    public Label errorMessage;

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);


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

        this.showError(null);
        try {
            RiseAndFall.getClient().sendPacket(new PacketCreateOrJoinGame(this.raceChoiceBox.getValue(), gameId));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de création ou de jointure de partie : ", e);
            return;
        }
        RiseAndFallApplication.switchToView(View.LOADING);
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);
        if (errorMessage != null) {
            this.showError(errorMessage);
        }
    }

    /**
     * Méthode pour afficher un message d'erreur.
     *
     * @param error Le message d'erreur à afficher.
     */
    public void showError(String error) {
        if (error == null) {
            this.errorMessage.setVisible(false);
        } else {
            this.errorMessage.setText(error);
            this.errorMessage.setVisible(true);
        }
    }
}
