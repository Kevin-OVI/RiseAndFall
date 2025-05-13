package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import java.io.IOException;
/**
 * Contrôleur de la vue de chargement.
 */
public class LoginController {
    /**
     * Champ pour le composant de l'image de fond.
     */
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant de username
     */
    public TextField username;

    /**
     * Champ pour le composant de password.
     */
    public PasswordField password;

    /**
     * Instance du client socket.
     */
    private static RiseAndFallClient client;


    /**
     * Méthode d'initialisation de la vue.
     * On ne peut pas utiliser la méthode internalize() car elle est appelée avant que la scène soit instanciée.
     * Cette méthode est donc appelée manuellement par {@link RiseAndFallApplication#start(Stage)}
     */
    public void initializeScene(Scene scene) {
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    @FXML
    public void login() {
        client = RiseAndFall.getClient();

        try {
            String username = this.username.getText();
            String password = this.password.getText();
            System.out.println("Login : " + username + " / " + password);

            client.sendPacket(new PacketAuthentification(username, password));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du paquet de login : ");
            e.printStackTrace();
            return;
        }
        RiseAndFallApplication.switchToView(View.LOADING, true);
    }
}
