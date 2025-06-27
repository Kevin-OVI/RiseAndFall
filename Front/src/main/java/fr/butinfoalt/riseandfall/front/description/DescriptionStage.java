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
    private static DescriptionStage INSTANCE;

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

    /**
     * Méthode pour obtenir l'instance unique de la scène de description.
     * Si l'instance n'existe pas, elle est créée.
     *
     * @return L'instance unique de la scène de description.
     */
    public static DescriptionStage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DescriptionStage();
        }
        return INSTANCE;
    }

    /**
     * Méthode pour fermer la fenêtre de description si elle est ouverte.
     * Elle vérifie si l'instance existe avant de tenter de la fermer.
     */
    public static void closeWindow() {
        if (INSTANCE != null) {
            INSTANCE.close();
        }
    }

    /**
     * Méthode pour afficher la fenêtre de description.
     * Elle récupère l'instance unique de la scène de description, l'affiche et la met au premier plan.
     */
    public static void showWindow() {
        DescriptionStage window = getInstance();
        window.show();
        window.toFront();
    }
}
