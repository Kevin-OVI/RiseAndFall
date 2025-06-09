package fr.butinfoalt.riseandfall.front.game;

import fr.butinfoalt.riseandfall.front.Environment;
import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Contrôleur pour la vue principale de l'application.
 */
public class MainRunningGameController implements ViewController {
    /**
     * Timer pour mettre à jour le temps restant avant le début de la partie.
     */
    private final AnimationTimer updateTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            updateNextTurnIn();
        }
    };

    /**
     * Champ pour le composant affichant le numéro du tour actuel.
     */
    @FXML
    public Label turnNumberField;

    /**
     * Champ pour le composant affichant le temps restant avant le prochain tour.
     */
    @FXML
    public Label nextTurnInField;

    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    public Label goldField;

    /**
     * Champ pour le composant de l'intelligence.
     */
    public Label intelligenceField;

    /**
     * Champ pour le composant de la race.
     */
    @FXML
    public Label raceField;

    /**
     * Champ pour le composant contenant les unités.
     */
    @FXML
    public VBox unitVBox;

    /**
     * Champ pour le composant contenant les bâtiments.
     */
    @FXML
    public VBox buildingsVBox;
    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Conteneur pour les boutons de la barre d'outils.
     */
    @FXML
    public HBox buttonsContainer;

    /**
     * Bouton pour passer au tour suivant.
     */
    @FXML
    public Button nextTurnButton;

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour ouvrir la page de description.
     */
    @FXML
    public void switchToDescriptionPage() {
        DescriptionStage.INSTANCE.show();
        DescriptionStage.INSTANCE.toFront();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour afficher la page des ordres.
     */
    @FXML
    public void switchToOrders() {
        RiseAndFallApplication.switchToView(View.ORDERS);
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour passer au tour suivant.
     */
    @FXML
    private void handleEndTurn() {
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.NEXT_TURN));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de fin de tour", e);
        }
    }

    /**
     * Met à jour le champ affichant le temps restant avant le prochain tour.
     */
    private void updateNextTurnIn() {
        Timestamp timestamp = RiseAndFall.getGame().getNextActionAt();
        long timeRemaining = timestamp.getTime() - System.currentTimeMillis();
        if (timeRemaining < 0) { // En cas d'une latence entre le serveur et le client
            timeRemaining = 0;
            this.updateTimer.stop();
        }
        int minutes = (int) (timeRemaining / 60000);
        int seconds = (int) ((timeRemaining % 60000) / 1000);
        this.nextTurnInField.setText(String.format("Le prochain tour commence dans %02d:%02d", minutes, seconds));
        this.updateTimer.start(); // Ne fait rien si le timer est déjà en cours
    }

    /**
     * Méthode pour mettre à jour l'affichage des ressources du joueur.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        ClientGame game = RiseAndFall.getGame();
        this.turnNumberField.setText("Tour : " + game.getCurrentTurn());
        updateNextTurnIn();

        ClientPlayer player = RiseAndFall.getPlayer();
        this.goldField.setText("Or : " + player.getGoldAmount());
        this.intelligenceField.setText("Intelligence : " + player.getIntelligence());
        this.raceField.setText("Race : " + player.getRace().getName());

        this.unitVBox.getChildren().clear();
        this.buildingsVBox.getChildren().clear();
        for (var entry : player.getUnitMap()) {
            Label label = new Label(entry.getKey().getName() + " : " + entry.getValue());
            this.unitVBox.getChildren().add(label);
        }
        for (var entry : player.getBuildingMap()) {
            Label label = new Label(entry.getKey().getName() + " : " + entry.getValue());
            this.buildingsVBox.getChildren().add(label);
        }

        // TODO : Afficher les messages d'erreur à l'utilisateur
    }

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);

        if (!Environment.DEBUG_MODE) {
            this.buttonsContainer.getChildren().remove(this.nextTurnButton);
        }
    }
}
