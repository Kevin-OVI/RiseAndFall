package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.game.gamelist.GameListController;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
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
        this.registerReceivePacket((byte) 2, PacketServerData.class, this::onServerData, PacketServerData::new);
        this.registerSendPacket((byte) 3, PacketCreateOrJoinGame.class);
        this.registerReceivePacket((byte) 4, PacketJoinedGame.class, this::onJoinedGame, this::decodePacketJoinedGame);
        this.registerSendPacket((byte) 5, PacketUpdateOrders.class);
        this.registerReceivePacket((byte) 6, PacketUpdateGameData.class, this::onUpdateGameData);
        this.registerSendAndReceivePacket((byte) 7, PacketGameAction.class, this::onGameAction, PacketGameAction::new);
        this.registerReceivePacket((byte) 8, PacketError.class, this::onError, PacketError::new);
        this.registerSendPacket((byte) 9, PacketRegister.class);
        this.registerReceivePacket((byte) 10, PacketWaitingGames.class, this::onWaitingGames, readHelper -> new PacketWaitingGames<>(readHelper, ClientGame::new));
    }

    /**
     * Décode les données du paquet PacketInitialGameData.
     *
     * @param readHelper L'outil de lecture pour désérialiser le paquet.
     * @return Un objet PacketInitialGameData contenant les données du jeu et du joueur.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    private PacketJoinedGame<ClientGame, ClientPlayer> decodePacketJoinedGame(ReadHelper readHelper) throws IOException {
        ClientGame clientGame = new ClientGame(readHelper);
        ClientPlayer player = new ClientPlayer(readHelper);
        return new PacketJoinedGame<>(clientGame, player);
    }

    /**
     * Méthode appelée lorsqu'un paquet de type PacketToken est reçu.
     * Elle sauvegarde le token d'authentification pour permettre à l'utilisateur de se reconnecter au serveur sans avoir à se réauthentifier.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onToken(SocketWrapper sender, PacketToken packet) {
        try (FileWriter writer = new FileWriter(Environment.authTokenFile)) {
            writer.write(packet.getToken());
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'écriture du token d'authentification dans le fichier", e);
        }
        // Le basculement vers l'écran principal ou la liste des parties se fera en fonction du paquet reçu du serveur : PacketJoinedGame ou PacketWaitingGames.
    }

    /**
     * Méthode appelée lorsque le paquet PacketServerData est reçu.
     * Elle initialise les données du serveur et change la vue de l'application pour afficher l'écran d'accueil.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onServerData(SocketWrapper sender, PacketServerData packet) {
        ServerData.init(packet.getRaces(), packet.getBuildingTypes(), packet.getUnitTypes());
        try {
            String token = new String(Files.readAllBytes(Paths.get(Environment.authTokenFile)));
            LogManager.logMessage("Envoi du token d'authentification...");
            sender.sendPacket(new PacketToken(token));
            return;
        } catch (IOException e) {
            LogManager.logMessage("Impossible de lire le fichier d'authentification, affichage de la vue de connexion.");
        } catch (Throwable e) {
            LogManager.logError("Erreur lors de la lecture du fichier d'authentification, affichage de la vue de connexion.", e);
        }

        Platform.runLater(() -> RiseAndFallApplication.switchToView(View.LOGIN));
    }

    /**
     * Change la vue de l'application en fonction de l'état du jeu.
     * Si un message d'erreur est fourni, il sera affiché sur la vue associée.
     *
     * @param gameState    L'état du jeu (WAITING, RUNNING, ENDED).
     * @param errorMessage Message d'erreur à afficher, ou null si aucune erreur n'est à afficher.
     */
    private void switchToGameView(GameState gameState, String errorMessage) {
        switch (gameState) {
            case WAITING -> {
                RiseAndFallApplication.switchToView(View.WAITING_GAME, errorMessage);
            }
            case RUNNING -> {
                View displayView = RiseAndFallApplication.getCurrentView();
                if (displayView != View.MAIN_RUNNING_GAME && displayView != View.ORDERS) {
                    displayView = View.MAIN_RUNNING_GAME;
                }

                RiseAndFallApplication.switchToView(displayView, errorMessage);
            }
            case ENDED -> {
                // TODO : Afficher un message de fin de partie (victoire, défaite)
            }
        }
    }

    /**
     * Change la vue de l'application pour afficher l'écran principal du jeu.
     * Cette méthode est une surcharge de {@link #switchToGameView(GameState, String)} sans message d'erreur.
     *
     * @param gameState L'état du jeu (WAITING, RUNNING, ENDED).
     */
    private void switchToGameView(GameState gameState) {
        this.switchToGameView(gameState, null);
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketJoinedGame} est reçu.
     * Elle initialise les données du jeu et du joueur, puis change la vue de l'application pour afficher l'écran principal.
     *
     * @param client Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onJoinedGame(SocketWrapper client, PacketJoinedGame<ClientGame, ClientPlayer> packet) {
        RiseAndFall.initGame(packet);
        Platform.runLater(() -> this.switchToGameView(packet.getGame().getState()));
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketUpdateGameData} est reçu.
     * Elle met à jour les données modifiables du jeu et du joueur, puis met à jour l'interface utilisateur.
     *
     * @param sender     Le socket connecté au serveur.
     * @param readHelper L'outil de lecture pour désérialiser le paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    private void onUpdateGameData(SocketWrapper sender, ReadHelper readHelper) throws IOException {
        RiseAndFall.getGame().updateModifiableData(readHelper);
        RiseAndFall.getPlayer().updateModifiableData(readHelper);
        Platform.runLater(() -> this.switchToGameView(RiseAndFall.getGame().getState()));
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketGameAction} est reçu.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet d'action de jeu reçu.
     */
    private void onGameAction(SocketWrapper sender, PacketGameAction packet) {
        switch (packet.getAction()) {
            case QUIT_GAME -> {
                RiseAndFall.resetGame();
                Platform.runLater(() -> RiseAndFallApplication.switchToView(View.LOADING));
                // Le basculement vers la liste des parties aura lieu après la réception du paquet PacketWaitingGames
            }
            default -> LogManager.logError("Action de jeu non gérée : " + packet.getAction());
        }
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
                    RiseAndFallApplication.switchToView(View.LOGIN, errorType.getMessage());
                }
                case REGISTER_GENERIC_ERROR, REGISTER_USERNAME_TAKEN -> {
                    RiseAndFallApplication.switchToView(View.REGISTER, errorType.getMessage());
                }
                case JOINING_GAME_FAILED, JOINING_GAME_NOT_FOUND, JOINING_NON_WAITING, JOINING_GAME_FULL -> {
                    GameListController controller = View.GAME_LIST.getController();
                    controller.showError(errorType.getMessage());
                    // Le basculement vers la vue de la liste des parties aura lieu après la réception du paquet PacketWaitingGames
                }
                case QUIT_GAME_FAILED, QUIT_NON_WAITING -> {
                    LogManager.logMessage("Erreur reçue lors de la tentative de quitter la partie : " + errorType.getMessage());
                    this.switchToGameView(RiseAndFall.getGame().getState(), errorType.getMessage());
                }
                default -> LogManager.logError("Erreur inconnue : " + errorType.getMessage());
            }
        });
    }

    /**
     * Méthode appelée lorsque le paquet {@link PacketWaitingGames} est reçu.
     * Elle met à jour la liste des parties en attente et change la vue de l'application pour afficher la liste des parties.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet contenant les parties en attente.
     */
    private void onWaitingGames(SocketWrapper sender, PacketWaitingGames<ClientGame> packet) {
        Platform.runLater(() -> {
            GameListController controller = View.GAME_LIST.getController();
            controller.refreshGameList(packet.getWaitingGames());
            RiseAndFallApplication.switchToView(View.GAME_LIST);
        });
    }
}
