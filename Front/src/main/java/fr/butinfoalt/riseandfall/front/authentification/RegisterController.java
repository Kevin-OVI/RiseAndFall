package fr.butinfoalt.riseandfall.front.authentification;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.network.packets.PacketRegister;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    PasswordField confirmPassword;

    @FXML
    Label errorMessage;

    @FXML
    public void login() {
        RiseAndFallApplication.switchToView(View.LOGIN, true);
    }

    public void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }

    @FXML
    public void register() {
        String username = this.username.getText();
        String password = this.password.getText();
        String passwordConfirm = this.confirmPassword.getText();

        if (Objects.equals(username, "")) {
            showError("Vous devez choisir un nom d'utilisateur!");
            return;
        }
        else if (Objects.equals(password, "") || Objects.equals(passwordConfirm, "")) {
            showError("Vous devez choisir un mot de passe!");
            return;
        }
        else if (!passwordConfirm.equals(password)) {
            showError("Vous avez fait un erreur sur la vérification de mot de passe!");
            return;
        }

        try {
            RiseAndFall.getClient().sendPacket(new PacketRegister(username, password));
            RiseAndFallApplication.switchToView(View.LOADING, true);
        } catch (IOException e) {
            showError("Erreur lors de la connexion au serveur");
        }
    }
}
