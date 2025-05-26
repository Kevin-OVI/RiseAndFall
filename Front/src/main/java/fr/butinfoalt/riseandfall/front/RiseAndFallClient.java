package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.GameList.GameListController;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.orders.OrderController;
import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.client.BaseSocketClient;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

/**
 * Client socket pour le jeu Rise and Fall.
 * Il gère la connexion au serveur et l'envoi/réception de paquets.
 */
public class RiseAndFallClient extends BaseSocketClient {
    /**
     * Gestionnaire d'erreurs pour le client.
     */
    private final ErrorManager errorManager;

    /**
     * Constructeur du client.
     * Il initialise le client avec l'hôte et le port du serveur et enregistre les paquets à envoyer et à recevoir.
     */
    public RiseAndFallClient() {
        super(Environment.SERVER_HOST, Environment.SERVER_PORT);

        this.errorManager = new ErrorManager(this);
        this.registerSendPacket((byte) 0, PacketAuthentification.class);
        this.registerSendAndReceivePacket((byte) 1, PacketToken.class, this::onToken, PacketToken::new);
        this.registerReceivePacket((byte) 2, PacketServerData.class, this::onServerData, PacketServerData::new);
        this.registerSendPacket((byte) 3, PacketCreateOrJoinGame.class);
        this.registerReceivePacket((byte) 4, PacketInitialGameData.class, this::onInitialGameData, this::decodeInitialGameData);
        this.registerSendPacket((byte) 5, PacketUpdateOrders.class);
        this.registerReceivePacket((byte) 6, PacketUpdateGameData.class, this::onNextTurnData);
        this.registerSendPacket((byte) 7, PacketGameAction.class);
        this.registerReceivePacket((byte) 8, PacketError.class, this.errorManager::onError, PacketError::new);
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
        int id = readHelper.readInt();
        String name = readHelper.readString();
        int turnInterval = readHelper.readInt();
        GameState state = GameState.values()[readHelper.readInt()];
        long lastTurnTimestampValue = readHelper.readLong();
        Timestamp lastTurnTimestamp = lastTurnTimestampValue == -1 ? null : new Timestamp(lastTurnTimestampValue);
        int currentTurn = readHelper.readInt();
        ClientGame clientGame = new ClientGame(id, name, turnInterval, state, lastTurnTimestamp, currentTurn);

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
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            RiseAndFallApplication.switchToView(View.WELCOME, true);
        });
    }

    /**
     * Méthode appelée lorsque le paquet PacketServerData est reçu.
     * Elle initialise les données du serveur et change la vue de l'application pour afficher l'écran d'accueil.
     *
     * @param sender Le socket connecté au serveur.
     * @param packet Le paquet reçu.
     */
    private void onServerData(SocketWrapper sender, PacketServerData packet) {
        ServerData.init(List.of(packet.getRaces()), List.of(packet.getBuildingTypes()), List.of(packet.getUnitTypes()), List.of(packet.getGames()));
        Platform.runLater(() -> {
            RiseAndFallApplication.switchToView(View.LOGIN, true);
            try {
                String token = new String(Files.readAllBytes(Paths.get("auth_token.txt")));
                System.out.println("Token récupéré : " + token);
                sender.sendPacket(new PacketToken(token));
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture du fichier auth_token.txt : ");
                e.printStackTrace();
            }
        });
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
}
