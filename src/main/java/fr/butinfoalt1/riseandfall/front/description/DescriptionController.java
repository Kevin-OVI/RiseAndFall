package fr.butinfoalt1.riseandfall.front.description;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;

/**
 * Contrôleur pour la vue de description.
 */
public class DescriptionController {
    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant de la barre de défilement.
     */
    @FXML
    public ScrollPane textScrollPane;

    /**
     * Champ pour le composant de texte.
     */
    @FXML
    public TextFlow textFlow;
}
