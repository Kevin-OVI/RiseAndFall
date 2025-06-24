package fr.butinfoalt.riseandfall.front.game.logs;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.Player;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la vue des journaux d'attaques.
 * Affiche les résultats des attaques et les joueurs éliminés pour chaque tour.
 */
public class AttackLogsController implements ViewController {
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
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
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

        boolean eliminated = currentPlayer.isEliminated();
        this.backButton.setVisible(!eliminated);
        this.eliminatedLabel.setVisible(eliminated);
        if (eliminated) {
            this.eliminatedLabel.setText("Vous avez été éliminé au tour %d, vous ne pouvez plus jouer.".formatted(currentPlayer.getEliminationTurn()));
        }

        this.listContainer.getChildren().clear();
        Map<Integer, List<Player>> eliminatedPlayers = game.getAllPlayers().stream()
                .filter(clientPlayer -> clientPlayer.getEliminationTurn() != -1)
                .collect(Collectors.groupingBy(Player::getEliminationTurn, Collectors.toList()));

        int currentTurn = game.getCurrentTurn();
        for (int i = 1; i < currentTurn; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(AttackLogsController.class.getResource("/fr/butinfoalt/riseandfall/front/components/attack-log-component.fxml"));
                Node node = loader.load();

                AttackLogsListItemController controller = loader.getController();
                controller.init(i, currentPlayer, game.getAttackResults(i), eliminatedPlayers.getOrDefault(i, Collections.emptyList()));

                this.listContainer.getChildren().addFirst(node);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }
}
