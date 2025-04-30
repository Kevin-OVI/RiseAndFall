package fr.butinfoalt.riseandfall.front.description;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.beans.InvalidationListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Classe représentant la scène de description du jeu.
 * Elle affiche une image de fond et un texte descriptif.
 */
public class DescriptionStage extends Stage {
    public static final DescriptionStage INSTANCE = new DescriptionStage();

    /**
     * Constructeur de la scène de description.
     * Il initialise la taille minimale de la fenêtre, défini le titre et la scène de contenu.
     */
    private DescriptionStage() {
        this.setMinWidth(256);
        this.setMinHeight(192);
        this.setTitle(View.DESCRIPTION.getWindowTitle());
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
        scene.getStylesheets().add(Objects.requireNonNull(RiseAndFallApplication.class.getResource("styles/description.css")).toExternalForm());

        UIUtils.setBackgroundImage("images/map.jpg", scene, controller.backgroundImageView);
        Text mainTitle = new Text("Rise & Fall\n");
        mainTitle.setTextAlignment(TextAlignment.CENTER);
        mainTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Texte formaté avec des titres en gras
        Text intro1 = new Text("Rise & Fall est un jeu développé par une équipe de choc.\n");
        Text intro2 = new Text("Le but est de créer un jeu tour par tour dans un monde fantasy.\n");
        Text intro3 = new Text("On a présenté le projet, maintenant passons aux règles du jeu :\n\n");

        Text objectifTitre = new Text("Objectif de jeu :\n");
        objectifTitre.setStyle("-fx-font-weight: bold");

        Text objectifTexte = new Text(
                """
                        Dans Rise & Fall, chaque joueur incarne une civilisation dans un monde fantasy. \
                        Le but est de faire prospérer sa civilisation en gérant ses ressources, en développant son économie, \
                        et en étendant son territoire tout en survivant jusqu’à la fin de la partie.

                        """);

        Text toursTitre = new Text("Déroulement des tours :\n");
        toursTitre.setStyle("-fx-font-weight: bold");

        Text toursTexte = new Text(
                """
                        • Le jeu se joue au tour par tour.
                        • À chaque tour, un joueur peut :
                          + Collecter des ressources
                          + Construire des bâtiments
                          + Recruter des unités
                          + Déplacer ses unités
                          + Attaquer ou interagir avec d’autres joueurs ou entités du monde

                        """);

        Text finTitre = new Text("Fin de partie\n");
        finTitre.setStyle("-fx-font-weight: bold");

        Text finTexte = new Text(
                """
                        • La partie se termine lorsqu’il ne reste plus qu’un nombre limité de civilisations en jeu (par exemple : 2 ou 3 joueurs survivants, selon le nombre initial).
                        • L’objectif est donc de faire partie des derniers survivants en éliminant ou surpassant ses adversaires.
                        • La stratégie de survie est aussi importante que l’agression ou la croissance.
                        """);

        // Ajouter les morceaux de texte dans le TextFlow
        controller.textFlow.getChildren().addAll(
                mainTitle,
                intro1, intro2, intro3,
                objectifTitre, objectifTexte,
                toursTitre, toursTexte,
                finTitre, finTexte
        );

        // Centrer le texte dans le ScrollPane si sa hauteur est inférieure à celle du ScrollPane
        InvalidationListener adaptTextPosition = (observable) -> {
            double viewportWidth = controller.textScrollPane.getViewportBounds().getWidth();
            if (controller.textFlow.getWidth() < viewportWidth) {
                controller.textFlow.setTranslateX((viewportWidth - controller.textFlow.getWidth()) / 2);
            } else {
                controller.textFlow.setTranslateX(0);
            }

            double viewportHeight = controller.textScrollPane.getViewportBounds().getHeight();
            if (controller.textFlow.getHeight() < viewportHeight) {
                controller.textFlow.setTranslateY((viewportHeight - controller.textFlow.getHeight()) / 2);
            } else {
                controller.textFlow.setTranslateY(0);
            }
        };

        controller.textScrollPane.viewportBoundsProperty().addListener(adaptTextPosition);
        controller.textFlow.widthProperty().addListener(adaptTextPosition);
        controller.textFlow.heightProperty().addListener(adaptTextPosition);
    }
}