package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.RiseAndFallClient;
import fr.butinfoalt.riseandfall.network.packets.PacketJoinedGame;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale du jeu RiseAndFall coté client.
 * Stocke le joueur actuel et les opérations avec lui.
 */
public class RiseAndFall {
    /**
     * Instance du joueur contrôlé par ce client.
     */
    private static ClientPlayer player;
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
    public static ClientPlayer getPlayer() {
        return player;
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
    }

    /**
     * Initialise le client socket.
     * Appelée lors du démarrage de l'application ({@link RiseAndFallApplication#start(Stage)}).
     */
    public static void initSocketClient() {
        client = new RiseAndFallClient();

        try {
            client.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode pour obtenir le client socket.
     *
     * @return Le client socket.
     */
    public static RiseAndFallClient getClient() {
        return client;
    }

    /**
     * Méthode pour initialiser la partie.
     * Appelée lors de la réception du paquet {@link PacketJoinedGame}.
     *
     * @param packet Le paquet contenant les données de la partie.
     */
    public static void initGame(PacketJoinedGame<ClientGame, ClientPlayer> packet) {
        game = packet.getGame();
        player = packet.getPlayer();
    }
}
