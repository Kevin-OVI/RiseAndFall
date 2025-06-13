package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

/**
 * Contrôleur de la vue de chargement.
 */
public class LoginController implements ViewController {
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
     * Méthode d'initialisation de la vue appelée par JavaFX
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    /**
     * Méthode exécutée lorsque l'utilisateur clique sur le bouton de connexion.
     */
    @FXML
    public void login() {
        String username = this.username.getText();
        String password = this.password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            RiseAndFall.getClient().sendPacket(new PacketAuthentification(username, password));
        } catch (IOException e) {
            showError("Erreur de connexion au serveur.");
            LogManager.logError("Impossible d'envoyer le packet d'authentification", e);
            return;
        }
        this.showError(null); // On cache le message d'erreur
        RiseAndFallApplication.switchToView(View.LOADING);
    }

    @FXML
    public void register() {
        RiseAndFallApplication.switchToView(View.REGISTER);
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);
        this.showError(errorMessage);
    }

    /**
     * Methode pour afficher une erreur de login
     */
    private void showError(String error) {
        if (error == null) {
            this.errorMessage.setVisible(false);
        } else {
            this.errorMessage.setText(error);
            this.errorMessage.setVisible(true);
        }
    }

    public void handleEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            keyEvent.consume();
            this.login();
        }
    }
}
