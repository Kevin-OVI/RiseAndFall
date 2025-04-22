package fr.butinfoalt1.riseandfall.front.description;

import fr.butinfoalt1.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt1.riseandfall.front.View;
import javafx.beans.InvalidationListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Classe représentant la scène de description du jeu.
 * Elle affiche une image de fond et un texte descriptif.
 */
public class DescriptionStage extends Stage {
    public static final DescriptionStage INSTANCE = new DescriptionStage();

    /**
     * Texte descriptif du jeu.
     * Il est affiché dans la scène de description.
     */
    private static final String text = """
            Rise and Fall est un jeu développé par une équipe de choc.
            Le but est de créer un jeu tour par tour dans un monde fantasy.
            On a présenté le projet, maintenant passons aux règles du jeu :
            Rise & Fall est un jeu de stratégie au tour par tour où le joueur dirige une civilisation.
            L'objectif est de gérer son économie et son expansion en construisant des bâtiments
            et en recrutant des unités tout en optimisant ses ressources.""";

    /**
     * Constructeur de la scène de description.
     * Il initialise la taille minimale de la fenêtre, défini le titre et la scène de contenu.
     */
    private DescriptionStage() {
        this.setMinWidth(256);
        this.setMinHeight(192);
        this.setTitle("Description de Rise & fall");
        this.setScene(View.DESCRIPTION.getScene(1024, 768, this::setupScene));
    }

    /**
     * Méthode pour configurer la scène de description.
     * Elle définit l'image de fond, adapte la taille de l'image à la fenêtre,
     * et centre le texte dans le ScrollPane.
     *
     * @param scene La scène à configurer.
     */
    private void setupScene(Scene scene) {
        DescriptionController controller = View.DESCRIPTION.getController();
        scene.getStylesheets().add(Objects.requireNonNull(RiseAndFallApplication.class.getResource("description.css")).toExternalForm());

        // Définir l'image de fond
        Image image = new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResourceAsStream("images/background.jpg")));
        controller.backgroundImageView.setImage(image);

        // Adapter la taille de l'image de fond à la taille de la fenêtre.
        // On recadre l'image de manière à ce qu'elle recouvre tout l'écran sans être déformée.
        InvalidationListener adaptImageSize = (observable) -> {
            controller.backgroundImageView.setFitWidth(Math.max(scene.getWidth(), scene.getHeight() * image.getWidth() / image.getHeight()));
            controller.backgroundImageView.setFitHeight(Math.max(scene.getHeight(), scene.getWidth() * image.getHeight() / image.getWidth()));
            controller.backgroundImageView.setX((scene.getWidth() - controller.backgroundImageView.getFitWidth()) / 2);
            controller.backgroundImageView.setY((scene.getHeight() - controller.backgroundImageView.getFitHeight()) / 2);
        };
        scene.widthProperty().addListener(adaptImageSize);
        scene.heightProperty().addListener(adaptImageSize);
        adaptImageSize.invalidated(null); // Appel initial pour adapter l'image à la taille de la fenêtre

        controller.textFlow.getChildren().add(new Text(text));

        InvalidationListener adaptTextPosition = (observable) -> {
            double viewportHeight = controller.textScrollPane.getViewportBounds().getHeight();
            if (controller.textFlow.getHeight() < viewportHeight) {
                controller.textFlow.setTranslateY((viewportHeight - controller.textFlow.getHeight()) / 2);
            } else {
                controller.textFlow.setTranslateY(0);
            }
        };

        // Centrer le texte dans le ScrollPane si sa hauteur est inférieure à celle du ScrollPane
        controller.textScrollPane.viewportBoundsProperty().addListener(adaptTextPosition);
        controller.textFlow.heightProperty().addListener(adaptTextPosition);
    }
}
