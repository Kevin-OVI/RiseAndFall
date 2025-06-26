package fr.butinfoalt.riseandfall.front.description;

import fr.butinfoalt.riseandfall.front.View;
import javafx.stage.Stage;

/**
 * Classe représentant la scène de description du jeu.
 * Elle affiche une image de fond et un texte descriptif.
 */
public class DescriptionStage extends Stage {
    /**
     * Instance unique de la scène de description.
     * Utilisée pour garantir qu'il n'y a qu'une seule scène de ce type ouverte à la fois.
     */
    public static final DescriptionStage INSTANCE = new DescriptionStage();

    /**
     * Constructeur de la scène de description.
     * Il initialise la taille minimale de la fenêtre, défini le titre et la scène de contenu.
     */
    private DescriptionStage() {
        this.setMinWidth(256);
        this.setMinHeight(192);
        this.setTitle(View.DESCRIPTION.getWindowTitle());
        this.setScene(View.DESCRIPTION.getScene(1024, 768, scene -> ((DescriptionController) View.DESCRIPTION.getController()).setupScene(scene)));
    }
}
