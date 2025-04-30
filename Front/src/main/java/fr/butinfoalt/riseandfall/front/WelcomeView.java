package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.Race;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Objects;

public class WelcomeView {
    /**
     * Champ de sélection de race.
     */
    @FXML
    public ChoiceBox<Race> raceChoiceBox;

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
        this.raceChoiceBox.getItems().clear();
        this.raceChoiceBox.getItems().addAll(Race.values());
        this.raceChoiceBox.setConverter(new NamedItemStringConverter<>());

        Color startColor = Color.BLACK;
        Color endColor = Color.ORANGERED;

        this.instructionsBlinkTransition = new Timeline(
                new KeyFrame(Duration.millis(150), e -> instructions.setTextFill(endColor)),
                new KeyFrame(Duration.millis(300), e -> instructions.setTextFill(startColor))
        );
        this.instructionsBlinkTransition.setCycleCount(4);

    }

    public void initializeScene(Scene scene) {
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    /**
     * Méthode appelée lorsque l'utilisateur clique sur le bouton "Jouer".
     * Elle vérifie qu'une race a été sélectionnée et crée un joueur, puis passe à la vue principale.
     */
    @FXML
    public void play() {
        if (this.raceChoiceBox.getValue() == null) {
            this.instructionsBlinkTransition.play();
            return;
        }

        RiseAndFall.createPlayer(this.raceChoiceBox.getValue());

        RiseAndFallApplication.switchToView(View.MAIN);

        MainController mainController = View.MAIN.getController();
        mainController.updateFields();
    }

}

