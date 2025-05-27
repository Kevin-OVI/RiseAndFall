package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.GameList.GameListController;
import fr.butinfoalt.riseandfall.front.authentification.LoginController;
import fr.butinfoalt.riseandfall.front.authentification.RegisterController;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.orders.OrderController;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.client.BaseSocketClient;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Client socket pour le jeu Rise and Fall.
 * Il gère la connexion au serveur et l'envoi/réception de paquets.
 */
public class RiseAndFallClient extends BaseSocketClient {
    /**
     * Constructeur du client.
     * Il initialise le client avec l'hôte et le port du serveur et enregistre les paquets à envoyer et à recevoir.
     */
    public RiseAndFallClient() {
        super(Environment.SERVER_HOST, Environment.SERVER_PORT);

        this.registerSendPacket((byte) 0, PacketAuthentification.class);
        this.registerSendAndReceivePacket((byte) 1, PacketToken.class, this::onToken, PacketToken::new);
        this.registerReceivePacket((byte) 2, PacketServerData.class, this::onServerData, readHelper -> new PacketServerData<>(readHelper, ClientGame::new));
        this.registerSendPacket((byte) 3, PacketCreateOrJoinGame.class);
        this.registerReceivePacket((byte) 4, PacketInitialGameData.class, this::onInitialGameData, this::decodeInitialGameData);
        this.registerSendPacket((byte) 5, PacketUpdateOrders.class);
        this.registerReceivePacket((byte) 6, PacketUpdateGameData.class, this::onNextTurnData);
        this.registerSendPacket((byte) 7, PacketGameAction.class);
        this.registerReceivePacket((byte) 8, PacketError.class, this::onError, PacketError::new);
        this.registerSendPacket((byte) 9, PacketRegister.class);
    }

    /**
     * Décode les données du paquet PacketInitialGameData.
     *
     * @param readHelper L'outil de lecture pour désérialiser le paquet.
     * @return Un objet PacketInitialGameData contenant les données du jeu et du joueur.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    private PacketInitialGameData<ClientGame, ClientPlayer> decodeInitialGameData(ReadHelper readHelper) throws IOException {
        ClientGame clientGame = new ClientGame(readHelper);
        ClientPlayer player = new ClientPlayer(readHelper);
        return new PacketInitialGameData<>(clientGame, player);
    }

    /**
     * Méthode appelée lorsqu'un paquet de type PacketToken est reçu.
     * Elle sauvegarde le token d'authentification pour permettre à l'utilisateur de se reconnecter au serveur sans avoir à se réauthentifier.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onToken(SocketWrapper sender, PacketToken packet) {
        try (FileWriter writer = new FileWriter("auth_token.txt")) {
            writer.write(packet.getToken());
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'écriture du token d'authentification dans le fichier", e);
        }
        Platform.runLater(() -> RiseAndFallApplication.switchToView(View.WELCOME, true));
    }

    /**
     * Méthode appelée lorsque le paquet PacketServerData est reçu.
     * Elle initialise les données du serveur et change la vue de l'application pour afficher l'écran d'accueil.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onServerData(SocketWrapper sender, PacketServerData<ClientGame> packet) {
        RiseAndFall.initServerData(new ServerData<>(packet.getRaces(), packet.getBuildingTypes(), packet.getUnitTypes(), packet.getGames()));
        try {
            String token = new String(Files.readAllBytes(Paths.get("auth_token.txt")));
            LogManager.logMessage("Envoi du token d'authentification...");
            sender.sendPacket(new PacketToken(token));
            return;
        } catch (IOException e) {
            LogManager.logMessage("Impossible de lire le fichier d'authentification, affichage de la vue de connexion.");
        } catch (Throwable e) {
            LogManager.logError("Erreur lors de la lecture du fichier d'authentification, affichage de la vue de connexion.", e);
        }

        Platform.runLater(() -> RiseAndFallApplication.switchToView(View.LOGIN, true));
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketInitialGameData} est reçu.
     * Elle initialise les données du jeu et du joueur, puis change la vue de l'application pour afficher l'écran principal.
     *
     * @param client Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onInitialGameData(SocketWrapper client, PacketInitialGameData<ClientGame, ClientPlayer> packet) {
        RiseAndFall.initGame(packet);
        Platform.runLater(() -> {
            RiseAndFallApplication.switchToView(View.MAIN, true);
            MainController mainController = View.MAIN.getController();
            mainController.updateFields();
        });
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketUpdateGameData} est reçu.
     * Elle met à jour les données modifiables du jeu et du joueur, puis met à jour l'interface utilisateur.
     *
     * @param sender     Le socket connecté au serveur.
     * @param readHelper L'outil de lecture pour désérialiser le paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    private void onNextTurnData(SocketWrapper sender, ReadHelper readHelper) throws IOException {
        RiseAndFall.getGame().updateModifiableData(readHelper);
        RiseAndFall.getPlayer().updateModifiableData(readHelper);
        Platform.runLater(() -> {
            MainController mainController = View.MAIN.getController();
            mainController.updateFields();
            OrderController orderController = View.ORDERS.getController();
            orderController.loadPendingOrders();
        });
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketError} est reçu.
     * Elle gère les erreurs en fonction du type d'erreur et change la vue de l'application si nécessaire.
     *
     * @param sender      Le socket connecté au serveur.
     * @param packetError Le paquet d'erreur reçu.
     */
    private void onError(SocketWrapper sender, PacketError packetError) {
        PacketError.ErrorType errorType = packetError.getErrorType();
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
                case JOINING_GAME_FAILED, JOINING_GAME_GAME_NOT_FOUND -> {
                    RiseAndFallApplication.switchToView(View.GAME_LIST, true);
                    ((GameListController) View.GAME_LIST.getController()).showError(errorType.getMessage());
                }
                case QUIT_GAME_FAILED -> {
                    RiseAndFallApplication.switchToView(View.MAIN, true);
                    ((MainController)View.MAIN.getController()).showError(errorType.getMessage());
                }
                default -> LogManager.logError("Erreur inconnue : " + errorType.getMessage());
            }
        });
    }
}
