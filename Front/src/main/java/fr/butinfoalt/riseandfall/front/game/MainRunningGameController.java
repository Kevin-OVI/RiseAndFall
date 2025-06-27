package fr.butinfoalt.riseandfall.front.game;

import fr.butinfoalt.riseandfall.front.Environment;
import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.chat.ChatStage;
import fr.butinfoalt.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt.riseandfall.front.game.SimpleTable.SimpleTableRow;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour la vue principale de l'application.
 * Il gère l'affichage des ressources du joueur, la navigation vers les autres vues,
 * ainsi que les actions principales comme terminer un tour ou quitter la partie.
 */
public class MainRunningGameController implements ViewController {
    /**
     * Timer pour mettre à jour le temps restant avant le début de la partie.
     */
    private final AnimationTimer updateTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            updateNextTurnIn();
        }
    };
    private final SimpleTableRow nextTurnInProperty = new SimpleTableRow("Prochain tour dans", "--:--");

    @FXML
    public SimpleTable gameInfoTable;

    @FXML
    public SimpleTable playerInfoTable;

    @FXML
    public SimpleTable unitsTable;

    @FXML
    public SimpleTable buildingsTable;

    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant racine de la vue.
     */
    public ScrollPane root;

    /**
     * Conteneur pour les boutons de la barre d'outils.
     */
    @FXML
    public HBox buttonsContainer;

    /**
     * Bouton pour passer au tour suivant.
     */
    @FXML
    public Button nextTurnButton;

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Description".
     * Elle affiche la fenêtre de description des éléments du jeu.
     */
    @FXML
    public void switchToDescriptionPage() {
        DescriptionStage.showWindow();
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Ordres".
     * Elle bascule vers la vue des ordres et charge les ordres en attente.
     */
    @FXML
    public void switchToOrders() {
        RiseAndFallApplication.switchToView(View.ORDERS);
    }

    @FXML
    public void manageAttacks() {
        RiseAndFallApplication.switchToView(View.ORDERS_ATTACK_LIST);
    }

    public void showAttacksLogs() {
        RiseAndFallApplication.switchToView(View.ATTACKS_LOGS);
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour afficher la page du Chat.
     */
    @FXML
    public void switchToChat() {
        ChatStage.openWindow();
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Fin du tour".
     * Elle exécute les ordres du joueur et applique les changements liés au tour.
     */
    @FXML
    private void handleEndTurn() {
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.NEXT_TURN));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de fin de tour", e);
        }
    }

    /**
     * Met à jour le champ affichant le temps restant avant le prochain tour.
     */
    private void updateNextTurnIn() {
        Timestamp timestamp = RiseAndFall.getGame().getNextActionAt();
        if (timestamp == null) {
            this.updateTimer.stop();
            return;
        }
        long timeRemaining = timestamp.getTime() - System.currentTimeMillis();
        if (timeRemaining < 0) { // En cas d'une latence entre le serveur et le client
            timeRemaining = 0;
            this.updateTimer.stop();
        }
        int minutes = (int) (timeRemaining / 60000);
        int seconds = (int) ((timeRemaining % 60000) / 1000);
        this.nextTurnInProperty.setValue(String.format("%02d:%02d", minutes, seconds));
        this.updateTimer.start(); // Ne fait rien si le timer est déjà en cours
    }

    /**
     * Met à jour l'affichage des ressources et possessions du joueur dans l'interface.
     * Cette méthode met à jour les labels affichant l'or, l'intelligence et la race,
     * ainsi que les listes des unités et bâtiments du joueur.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        ClientGame game = RiseAndFall.getGame();
        ClientPlayer player = RiseAndFall.getPlayer();

        this.gameInfoTable.setItems(
                new SimpleTableRow("Tour", String.valueOf(game.getCurrentTurn())),
                this.nextTurnInProperty
        );

        updateNextTurnIn();

        this.playerInfoTable.setItems(
                new SimpleTableRow("Or", String.valueOf(player.getGoldAmount())),
                new SimpleTableRow("Intelligence", String.valueOf(player.getIntelligence())),
                new SimpleTableRow("Race", player.getRace().getName())
        );

        List<SimpleTableRow> unitData = new ArrayList<>();
        for (ObjectIntMap.Entry<UnitType> entry : player.getUnitMap()) {
            unitData.add(new SimpleTableRow(entry.getKey().getName(), entry.getValue()));
        }
        this.unitsTable.setItems(unitData);

        List<SimpleTableRow> buildingData = new ArrayList<>();
        for (ObjectIntMap.Entry<BuildingType> entry : player.getBuildingMap()) {
            buildingData.add(new SimpleTableRow(entry.getKey().getName(), entry.getValue()));
        }
        this.buildingsTable.setItems(buildingData);

        // TODO : Afficher les messages d'erreur à l'utilisateur
    }

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);

        this.unitsTable.setKeyColumnName("Nom de l'unité");
        this.unitsTable.setValueColumnName("Quantité");

        this.buildingsTable.setKeyColumnName("Nom du bâtiment");
        this.buildingsTable.setValueColumnName("Quantité");

        if (!Environment.DEBUG_MODE) {
            this.buttonsContainer.getChildren().remove(this.nextTurnButton);
        }
    }
}