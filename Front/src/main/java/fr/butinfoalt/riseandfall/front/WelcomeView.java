package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class WelcomeView {
    /**
     * Label d'instructions.
     */
    @FXML
    public Label instructions;

    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Animation de clignotement des instructions.
     */
    private Timeline instructionsBlinkTransition;

    /**
     * Méthode d'initialisation de la vue.
     * Elle est appelée automatiquement par JavaFX après le chargement du fichier FXML.
     */
    @FXML
    public void initialize() {
        Color startColor = Color.BLACK;
        Color endColor = Color.ORANGERED;

        this.instructionsBlinkTransition = new Timeline(
                new KeyFrame(Duration.millis(150), e -> instructions.setTextFill(endColor)),
                new KeyFrame(Duration.millis(300), e -> instructions.setTextFill(startColor))
        );
        this.instructionsBlinkTransition.setCycleCount(4);

        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    /**
     * Méthode appelée lorsque l'utilisateur clique sur le bouton "Jouer".
     * Elle vérifie qu'une race a été sélectionnée et envoie le paquet de création de partie au serveur.
     * Enfin, elle change la vue de l'application pour afficher l'écran de chargement.
     */
    @FXML
    public void play() {
        RiseAndFallApplication.switchToView(View.GAME_LIST);
    }
}
