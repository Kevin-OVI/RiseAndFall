package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public class LoadingController {
    public ImageView backgroundImageView;

    public void initializeScene(Scene scene) {
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }
}
