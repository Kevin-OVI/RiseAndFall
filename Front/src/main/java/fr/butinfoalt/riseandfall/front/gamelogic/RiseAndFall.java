package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.front.chat.ChatStage;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Timer;

/**
 * Classe principale du jeu RiseAndFall coté client.
 * Stocke le joueur actuel et les opérations avec lui.
 */
public class RiseAndFall {
    public static final Timer TIMER = new Timer();

    /**
     * Instance du joueur contrôlé par ce client.
     */
    private static CurrentClientPlayer player;
    /**
     * Instance de la partie actuelle.
     */
    private static ClientGame game;

    /**
     * Instance du client socket.
     */
    private static RiseAndFallClient client;

    /**
     * Méthode pour obtenir le joueur actuel.
     *
     * @return Le joueur actuel.
     */
    public static CurrentClientPlayer getPlayer() {
        return player;
    }

    public static ClientPlayer getPlayer(int playerId) {
        ClientPlayer player = RiseAndFall.getPlayer();
        if (player != null && player.getId() == playerId) {
            return player;
        }
        return RiseAndFall.getGame().getOtherPlayer(playerId);
    }

    /**
     * Méthode pour obtenir la partie actuelle.
     *
     * @return La partie actuelle.
     */
    public static ClientGame getGame() {
        return game;
    }

    /**
     * Méthode pour réinitialiser le joueur.
     * Utilisée pour réinitialiser le joueur après une partie.
     */
    public static void resetGame() {
        player = null;
        game = null;
        Platform.runLater(ChatStage::closeWindow);
    }

    /**
     * Initialise le client socket.
     * Appelée lors du démarrage de l'application ({@link RiseAndFallApplication#start(Stage)}).
     */
    public static void initSocketClient() {
        client = new RiseAndFallClient();
        client.scheduledConnect();
    }

    /**
     * Méthode pour obtenir le client socket.
     *
     * @return Le client socket.
     */
    public static RiseAndFallClient getClient() {
        return client;
    }

    public static void setPlayer(CurrentClientPlayer player) {
        RiseAndFall.player = player;
    }

    public static void setGame(ClientGame game) {
        RiseAndFall.game = game;
    }
}
