package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.authentification.LoginController;
import fr.butinfoalt.riseandfall.front.authentification.RegisterController;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketError;
import javafx.application.Platform;

import java.util.Objects;

public class ErrorManager {
    private final RiseAndFallClient client;

    public ErrorManager(RiseAndFallClient client) {
        this.client = client;
    }

    public void onError(SocketWrapper socketWrapper, PacketError packetError) {
        System.err.println("Erreur reçue du serveur : " + packetError.getError());
        Platform.runLater(() -> {
            if (Objects.equals(packetError.getCategory(), "Authentification")) {
                RiseAndFallApplication.switchToView(View.LOGIN, true);
                ((LoginController) View.LOGIN.getController()).showError(packetError.getError());
            }
            else if (Objects.equals(packetError.getCategory(), "Register")) {
                RiseAndFallApplication.switchToView(View.REGISTER, true);
                ((RegisterController) View.REGISTER.getController()).showError(packetError.getError());
            }
            else {
                System.out.println("Erreur inconnue : " + packetError.getError());
                System.out.println("Category: " + packetError.getCategory());
            }
        });
    }
}
