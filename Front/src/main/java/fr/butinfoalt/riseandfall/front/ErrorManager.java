package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.authentification.LoginController;
import fr.butinfoalt.riseandfall.front.authentification.RegisterController;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.PacketError;
import fr.butinfoalt.riseandfall.network.packets.PacketError.ErrorType;
import javafx.application.Platform;

public class ErrorManager {
    private final RiseAndFallClient client;

    public ErrorManager(RiseAndFallClient client) {
        this.client = client;
    }

    public void onError(SocketWrapper sender, PacketError packetError) {
        ErrorType errorType = packetError.getErrorType();
        System.err.println("Erreur reçue du serveur : " + errorType);
        Platform.runLater(() -> {
            switch (errorType) {
                case LOGIN_GENERIC_ERROR, LOGIN_INVALID_CREDENTIALS, LOGIN_INVALID_SESSION -> {
                    RiseAndFallApplication.switchToView(View.LOGIN, true);
                        ((LoginController) View.LOGIN.getController()).showError(errorType.getMessage());
                }
                case REGISTER_GENERIC_ERROR, REGISTER_USERNAME_TAKEN -> {
                    RiseAndFallApplication.switchToView(View.REGISTER, true);
                    ((RegisterController) View.REGISTER.getController()).showError(errorType.getMessage());
                }
                default -> System.out.println("Erreur inconnue : " + errorType.getMessage());
            }
        });
    }
}
