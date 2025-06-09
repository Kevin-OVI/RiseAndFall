package fr.butinfoalt.riseandfall.front;

public interface ViewController {
    /**
     * Méthode appelée à chaque fois que la vue est affichée.
     *
     * @param errorMessage Le message d'erreur à afficher, s'il y en a un.
     */
    default void onDisplayed(String errorMessage) {}

    /**
     * Méthode appelée à chaque fois que la vue est masquée.
     */
    default void onHidden() {}
}
