package fr.butinfoalt.riseandfall.front.util;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.util.MathUtils;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Objects;

public class UIUtils {
    private static final int BACKGROUND_IMAGE_PADDING_HORIZONTAL = 242, BACKGROUND_IMAGE_PADDING_VERTICAL = 89;

    private static final HashMap<String, Image> imagesCache = new HashMap<>();

    /**
     * Méthode pour charger une image à partir d'un chemin donné.
     *
     * @param imagePath le chemin de l'image à charger
     * @return l'objet Image chargé
     */
    private static Image loadImageFromPath(String imagePath) {
        return new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResource(imagePath)).toExternalForm());
    }

    /**
     * Méthode pour définir l'image de fond de la scène.
     *
     * @param imagePath           le chemin de l'image de fond
     * @param scene               la scène à laquelle l'image de fond sera appliquée
     * @param backgroundImageView le composant ImageView qui affichera l'image de fond
     * @param adaptablePane       Le pane principal qui s'adaptera à la zone utilisable du fond, ou null si non utilisé
     */
    public static void setBackgroundImage(String imagePath, Scene scene, ImageView backgroundImageView, Region adaptablePane) {
        // Chargement de l'image de fond à partir du cache si possible
        Image backgroundImage = imagesCache.computeIfAbsent(imagePath, UIUtils::loadImageFromPath);
        backgroundImageView.setImage(backgroundImage);
        double imgWidth = backgroundImage.getWidth();
        double imgHeight = backgroundImage.getHeight();

        // Adaptation de la taille de l'image de fond à la taille de la fenêtre
        InvalidationListener adaptImageSize = (observable) -> {
            double finalImageWidth = Math.max(scene.getWidth(), scene.getHeight() * imgWidth / imgHeight);
            double finalImageHeight = Math.max(scene.getHeight(), scene.getWidth() * imgHeight / imgWidth);
            double finalImageX = (scene.getWidth() - finalImageWidth) / 2;
            double finalImageY = (scene.getHeight() - finalImageHeight) / 2;

            // Ajustement de la taille et de la position de l'image de fond
            backgroundImageView.setFitWidth(finalImageWidth);
            backgroundImageView.setFitHeight(finalImageHeight);
            backgroundImageView.setX(finalImageX);
            backgroundImageView.setY(finalImageY);

            // Ajustement de la taille et de la position du pane adaptable
            if (adaptablePane != null) {
                double marginX = Math.max(finalImageX + BACKGROUND_IMAGE_PADDING_HORIZONTAL * (finalImageWidth / imgWidth), 20);
                double marginY = Math.max(finalImageY + BACKGROUND_IMAGE_PADDING_VERTICAL * (finalImageHeight / imgHeight), 20);
                adaptablePane.setPadding(new Insets(marginY, marginX, marginY, marginX));
            }
        };
        scene.widthProperty().addListener(adaptImageSize);
        scene.heightProperty().addListener(adaptImageSize);
        adaptImageSize.invalidated(null); // Appel initial pour adapter l'image à la taille de la fenêtre
    }

    /**
     * Méthode pour définir l'image de fond de la scène sans adaptablePane.
     *
     * @param imagePath           le chemin de l'image de fond
     * @param scene               la scène à laquelle l'image de fond sera appliquée
     * @param backgroundImageView le composant ImageView qui affichera l'image de fond
     */
    public static void setBackgroundImage(String imagePath, Scene scene, ImageView backgroundImageView) {
        setBackgroundImage(imagePath, scene, backgroundImageView, null);
    }

    public static String displayOptimisedFloat(float value) {
        int intValue = (int) value;
        if (value == intValue) {
            return String.valueOf(intValue);
        } else {
            return String.valueOf(MathUtils.roundToDecimalPlaces(value, 2));
        }
    }
}
