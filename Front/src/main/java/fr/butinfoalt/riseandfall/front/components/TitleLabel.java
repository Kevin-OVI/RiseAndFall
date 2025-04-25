package fr.butinfoalt.riseandfall.front.components;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Composant pour afficher un label de titre.
 * Ce composant est une extension de la classe Label de JavaFX.
 * Il modifie la taille de la police du texte affiché pour le rendre plus grand.
 */
public class TitleLabel extends Label {
    /**
     * Constructeur par défaut de la classe TitleLabel.
     * Initialise le label avec une taille de police augmentée.
     */
    public TitleLabel() {
        this.init();
    }

    /**
     * Constructeur de la classe TitleLabel.
     * Initialise le label avec une chaîne de caractères et une taille de police augmentée.
     *
     * @param s La chaîne de caractères à afficher dans le label.
     */
    public TitleLabel(String s) {
        super(s);
        this.init();
    }

    /**
     * Constructeur de la classe TitleLabel.
     * Initialise le label avec une chaîne de caractères, un nœud parent et une taille de police augmentée.
     *
     * @param s    La chaîne de caractères à afficher dans le label.
     * @param node Le nœud parent du label.
     */
    public TitleLabel(String s, Node node) {
        super(s, node);
        this.init();
    }

    /**
     * Fonction d'initialisation appelée par chaque constructeur pour le label.
     * Elle modifie la taille de la police du texte affiché.
     */
    private void init() {
        this.getStyleClass().add("title-label");
    }
}
