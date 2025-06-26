package fr.butinfoalt.riseandfall.front.game.logs;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.*;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackResult;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la vue des journaux d'attaques.
 * Affiche les résultats des attaques et les joueurs éliminés pour chaque tour.
 */
public class AttackLogsController implements ViewController {
    private final HashMap<Integer, AttackLogsListItemController> itemControllers = new HashMap<>();

    /**
     * Le conteneur racine de la vue.
     */
    @FXML
    public VBox root;

    /**
     * L'image de fond de la vue.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Le conteneur pour la liste des journaux d'attaques.
     */
    @FXML
    public VBox listContainer;

    /**
     * Le bouton pour revenir à la vue précédente.
     */
    @FXML
    public Button backButton;

    /**
     * Le label indiquant que le joueur a été éliminé.
     */
    @FXML
    public Label eliminatedLabel;

    /**
     * Appelé par JavaFX lors de l'initialisation de la vue.
     * Initialise l'image de fond de la scène.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);
    }

    /**
     * Méthode appelée lorsque le bouton de retour est cliqué.
     * Permet de revenir à la vue principale du jeu en cours.
     */
    @FXML
    public void switchBack() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        if (player.isEliminated()) {
            try {
                RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.QUIT_GAME));
            } catch (IOException e) {
                LogManager.logError("Failed to send exit game packet", e);
                return;
            }
            RiseAndFallApplication.switchToView(View.LOADING);
        } else {
            RiseAndFallApplication.switchToView(RiseAndFall.getGame().getState() == GameState.ENDED ? View.VICTORY_SCREEN : View.MAIN_RUNNING_GAME);
        }
    }

    /**
     * Méthode appelée lorsque la vue est affichée.
     * Met à jour l'affichage en fonction de l'état actuel du joueur et du jeu.
     *
     * @param errorMessage Message d'erreur à afficher, s'il y en a un.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        CurrentClientPlayer currentPlayer = RiseAndFall.getPlayer();
        ClientGame game = RiseAndFall.getGame();

        if (currentPlayer.isEliminated()) {
            this.backButton.setText("Relever un nouveau défi");
            this.eliminatedLabel.setText("Vous avez été éliminé au tour %d, vous ne pouvez plus jouer.".formatted(currentPlayer.getEliminationTurn()));
            this.eliminatedLabel.setVisible(true);
        } else {
            this.backButton.setText("Retour");
            this.eliminatedLabel.setVisible(false);
        }

        Map<Integer, List<Player>> eliminatedPlayers = game.getAllPlayers().stream()
                .filter(clientPlayer -> clientPlayer.getEliminationTurn() != -1)
                .collect(Collectors.groupingBy(Player::getEliminationTurn, Collectors.toList()));

        int currentTurn = game.getCurrentTurn();
        int maxTurn = game.getState() == GameState.ENDED ? currentTurn + 1 : currentTurn;
        for (int i = 1; i < maxTurn; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(AttackLogsController.class.getResource("/fr/butinfoalt/riseandfall/front/components/attack-log-component.fxml"));
                Node node = loader.load();

                AttackLogsListItemController controller = loader.getController();
                controller.init(i, game, currentPlayer, game.getAttackResults(i), eliminatedPlayers.getOrDefault(i, Collections.emptyList()));

                this.listContainer.getChildren().addFirst(node);
                this.itemControllers.put(i, controller);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }

    /**
     * Appelée lorsque la vue est masquée.
     * Nettoie le conteneur de la liste et les contrôleurs d'éléments pour libérer les ressources.
     */
    @Override
    public void onHidden() {
        ViewController.super.onHidden();

        this.listContainer.getChildren().clear();
        this.itemControllers.clear();
    }

    /**
     * Met à jour l'élément de la liste pour un tour spécifique avec les résultats des attaques et les joueurs éliminés.
     *
     * @param turn              Le numéro du tour à mettre à jour
     * @param attackResults     La liste des résultats des attaques impliquant le joueur durant ce tour
     * @param eliminatedPlayers La liste des joueurs éliminés durant ce tour
     */
    public void updateDisplayedItem(int turn, List<AttackResult> attackResults, List<Player> eliminatedPlayers) {
        AttackLogsListItemController controller = this.itemControllers.get(turn);
        if (controller != null) {
            controller.clear();
            controller.init(turn, RiseAndFall.getGame(), RiseAndFall.getPlayer(), attackResults, eliminatedPlayers);
        }
    }
}
