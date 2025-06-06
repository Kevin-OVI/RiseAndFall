package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketRegister;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

/**
 * Contrôleur de la vue d'inscription.
 */
public class RegisterController {
    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant du nom d'utilisateur.
     */
    @FXML
    public TextField username;

    /**
     * Champ pour le composant du mot de passe.
     */
    @FXML
    public PasswordField password;

    /**
     * Champ pour le composant de la confirmation du mot de passe.
     */
    @FXML
    public PasswordField confirmPassword;

    /**
     * Champ pour le composant du message d'erreur.
     */
    @FXML
    public Label errorMessage;


    /**
     * Méthode d'initialisation de la vue appelée par JavaFX
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    /**
     * Méthode pour changer la vue de la fenêtre principale.
     * On peut choisir de remplacer la vue actuelle ou de l'empiler pour y revenir plus tard.
     */
    @FXML
    public void login() {
        RiseAndFallApplication.switchToView(View.LOGIN, true);
    }

    /**
     * Méthode pour afficher un message d'erreur.
     *
     * @param error Le message d'erreur à afficher.
     */
    public void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }

    /**
     * Méthode exécutée lors d'un clic sur le bouton d'inscription.
     */
    @FXML
    public void register() {
        String username = this.username.getText();
        String password = this.password.getText();
        String passwordConfirm = this.confirmPassword.getText();

        if (username.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            showError("Tous les champs doivent être remplis!");
            return;
        }
        if (!passwordConfirm.equals(password)) {
            showError("Vous avez fait un erreur sur la vérification de mot de passe!");
            return;
        }

        try {
            RiseAndFall.getClient().sendPacket(new PacketRegister(username, password));
        } catch (IOException e) {
            showError("Erreur lors de la connexion au serveur");
            LogManager.logError("Impossible d'envoyer le packet d'enregistrement", e);
            return;
        }
        RiseAndFallApplication.switchToView(View.LOADING, true);
    }
}
