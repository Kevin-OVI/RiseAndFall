package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Contrôleur de la vue de chargement.
 */
public class LoadingController implements ViewController {
    /**
     * Champ pour le composant de l'image de fond.
     */
    public ImageView backgroundImageView;

    /**
     * Méthode d'initialisation de la vue.
     * On ne peut pas utiliser la méthode internalize() car elle est appelée avant que la scène soit instanciée.
     * Cette méthode est donc appelée manuellement par {@link RiseAndFallApplication#start(Stage)}
     */
    public void initializeScene(Scene scene) {
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }
}
