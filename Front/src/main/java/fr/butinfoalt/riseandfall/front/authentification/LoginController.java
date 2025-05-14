package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
     * Champ pour le composant du nom d'utilisateur.
     */
    public TextField username;

    /**
     * Champ pour le composant de password.
     */
    public PasswordField password;

    /**
     * Champ pour le message d'erreur.
     */
    public Label errorMessage;

    /**
     * Instance du client socket.
     */
    private static RiseAndFallClient client;


    /**
     * Méthode d'initialisation de la vue.
     * On ne peut pas utiliser la méthode internalize() car elle est appelée avant que la scène soit instanciée.
     * Cette méthode est donc appelée manuellement par {@link RiseAndFallApplication#start(Stage)}
     */
    public void initialize(Scene scene) {
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

    @FXML
    public void register() {
        RiseAndFallApplication.switchToView(View.REGISTER, false);
    }

    /**
     * Methode pour afficher une erreur de login
     */
    public void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }
}
