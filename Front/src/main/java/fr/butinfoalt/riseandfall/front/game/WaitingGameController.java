package fr.butinfoalt.riseandfall.front.game;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.Timestamp;

public class WaitingGameController implements ViewController {
    /**
     * Timer pour mettre à jour le temps restant avant le début de la partie.
     */
    private final AnimationTimer updateTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            updateStartingIn();
        }
    };

    /**
     * Champ pour le composant de l'image de fond.
     */
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant de la race.
     */
    public Label raceField;

    /**
     * Champ pour le composant de temps restant avant le début de la partie.
     */
    @FXML
    public Label startingInField;

    /**
     * Méthode appelée par JavaFX quand la vue est initialisée.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    /**
     * Met à jour le champ affichant le temps restant avant le début de la partie.
     */
    private void updateStartingIn() {
        Timestamp timestamp = RiseAndFall.getGame().getNextActionAt();
        if (timestamp == null) {
            this.startingInField.setText("Il n'y a pas assez de joueurs pour commencer la partie");
            this.updateTimer.stop();
        } else {
            long timeRemaining = timestamp.getTime() - System.currentTimeMillis();
            if (timeRemaining <= 0) {
                timeRemaining = 0;
                this.updateTimer.stop();
            }
            int minutes = (int) (timeRemaining / 60000);
            int seconds = (int) ((timeRemaining % 60000) / 1000);
            this.startingInField.setText(String.format("La partie commence dans %02d:%02d", minutes, seconds));
            this.updateTimer.start(); // Ne fait rien si le timer est déjà en cours
        }
    }

    /**
     * Met à jour les champs de la vue à l'affichage.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);
        this.raceField.setText("Race : " + RiseAndFall.getPlayer().getRace().getName());
        updateStartingIn();

        // TODO : Afficher les messages d'erreur à l'utilisateur
    }

    /**
     * Arrête le timer de mise à jour du temps restant avant le début de la partie quand la vue est masquée.
     */
    @Override
    public void onHidden() {
        ViewController.super.onHidden();
        this.updateTimer.stop();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour quitter le jeu.
     */
    @FXML
    public void handleQuitGame() {
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.QUIT_GAME));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet pour quitter la partie", e);
            return;
        }
        RiseAndFallApplication.switchToView(View.LOADING);
    }
}
