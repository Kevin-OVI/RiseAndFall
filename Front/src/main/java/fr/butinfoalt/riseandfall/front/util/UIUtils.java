package fr.butinfoalt.riseandfall.front.util;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import javafx.beans.InvalidationListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class UIUtils {
    public static void setBackgroundImage(String imagePath, Scene scene, ImageView backgroundImageView) {
        // Chargement de l'image de fond
        Image backgroundImage = new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResource(imagePath)).toExternalForm());
        backgroundImageView.setImage(backgroundImage);
        // Adaptation de la taille de l'image de fond à la taille de la fenêtre
        InvalidationListener adaptImageSize = (observable) -> {
            backgroundImageView.setFitWidth(Math.max(scene.getWidth(), scene.getHeight() * backgroundImage.getWidth() / backgroundImage.getHeight()));
            backgroundImageView.setFitHeight(Math.max(scene.getHeight(), scene.getWidth() * backgroundImage.getHeight() / backgroundImage.getWidth()));
            backgroundImageView.setX((scene.getWidth() - backgroundImageView.getFitWidth()) / 2);
            backgroundImageView.setY((scene.getHeight() - backgroundImageView.getFitHeight()) / 2);
        };
        scene.widthProperty().addListener(adaptImageSize);
        scene.heightProperty().addListener(adaptImageSize);
        adaptImageSize.invalidated(null); // Appel initial pour adapter l'image à la taille de la fenêtre

    }
}
