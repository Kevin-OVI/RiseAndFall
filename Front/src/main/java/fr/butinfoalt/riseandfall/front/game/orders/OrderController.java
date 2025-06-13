package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.game.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.front.game.orders.table.BuildingsPurchaseTable;
import fr.butinfoalt.riseandfall.front.game.orders.table.ItemTableRow;
import fr.butinfoalt.riseandfall.front.game.orders.table.UnitsPurchaseTable;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateOrders;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.counter.Counter;
import fr.butinfoalt.riseandfall.util.counter.Modifier;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;


/**
 * Contrôleur pour la vue de gestion des ordres.
 */
public class OrderController implements ViewController {
    /**
     * Liste des unités en attente de création.
     */
    private ObjectIntMap<UnitType> pendingUnits;

    /**
     * Liste des bâtiments en attente de création.
     */
    private ObjectIntMap<BuildingType> pendingBuildings;

    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    private Label goldField;

    /**
     * Champ pour le composant de la quantité d'intelligence.
     */
    @FXML
    public Label intelligenceField;

    /**
     * Champ pour le composant contenant la quantité d'unités pouvant être produites.
     */
    @FXML
    private Label unitsField;

    /**
     * Tableau contenant les unités.
     */
    @FXML
    private UnitsPurchaseTable unitTable;

    /**
     * Tableau contenant les bâtiments.
     */
    @FXML
    private BuildingsPurchaseTable buildingTable;

    /**
     * Champ pour le composant contenant le prix total.
     */
    @FXML
    public Label totalPrice;

    /**
     * Champ pour le composant contenant l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Méthode pour charger les ordres en attente du joueur dans l'interface.
     * Elle met à jour les composants de l'interface utilisateur
     * pour afficher les unités et bâtiments en attente de création,
     * ainsi que la quantité d'or disponible.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        ClientPlayer player = RiseAndFall.getPlayer();
        float playerIntelligence = player.getIntelligence();
        this.pendingUnits = player.getPendingUnitsCreation().clone();
        this.pendingBuildings = player.getPendingBuildingsCreation().clone();

        this.intelligenceField.setText("Intelligence : " + playerIntelligence);

        Counter<Float> goldCounter = Counter.of(player.getGoldAmount());
        goldCounter.addListener(goldAmount -> {
            this.goldField.setText("Or restant : " + goldAmount);
            this.totalPrice.setText("Prix total : " + (goldCounter.getInitialValue() - goldAmount));
        });
        Counter<Integer> allowedUnitsCounter = Counter.of(player.getAllowedUnitCount());
        allowedUnitsCounter.addListener(allowedCount -> this.unitsField.setText("Entrainements d'unités restants : " + allowedCount));
        Counter<Integer> allowedBuildingsCounter = Counter.of(5);

        this.unitTable.getItems().clear();
        for (ObjectIntMap.Entry<UnitType> entry : this.pendingUnits) {
            Modifier<Integer> unitsModifier = allowedUnitsCounter.addModifier(-entry.getValue());
            PurchasableItemAmountSelector<UnitType> selector = new PurchasableItemAmountSelector<>(entry, goldCounter, playerIntelligence,
                    (amount) -> unitsModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> unitsModifier.setDelta(-amount));
            this.unitTable.getItems().add(new ItemTableRow<>(entry.getKey(), selector));
        }

        this.buildingTable.getItems().clear();
        for (ObjectIntMap.Entry<BuildingType> entry : pendingBuildings) {
            Modifier<Integer> buildingModifier = allowedBuildingsCounter.addModifier(-entry.getValue());
            PurchasableItemAmountSelector<BuildingType> selector = new PurchasableItemAmountSelector<>(entry, goldCounter, playerIntelligence,
                    (amount) -> buildingModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> buildingModifier.setDelta(-amount));
            allowedBuildingsCounter.addListener(value -> selector.updateButtonsState());
            this.buildingTable.getItems().add(new ItemTableRow<>(entry.getKey(), selector));
        }

        goldCounter.setDispatchChanges(true);
        allowedBuildingsCounter.setDispatchChanges(true);
        allowedUnitsCounter.setDispatchChanges(true);

        // TODO : Afficher les messages d'erreur à l'utilisateur
    }

    /**
     * Méthode appelée par JavaFX pour revenir à la vue précédente.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    /**
     * Méthode appelée par JavaFX pour gérer l'action de sauvegarde.
     * Elle enregistre les ordres en attente du joueur et revient à la vue précédente.
     */
    @FXML
    private void handleSave() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        player.setPendingBuildingsCreation(this.pendingBuildings);
        player.setPendingUnitsCreation(this.pendingUnits);
        try {
            RiseAndFall.getClient().sendPacket(new PacketUpdateOrders(this.pendingUnits, this.pendingBuildings, null));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des ordres", e);
            return;
        }
        this.switchBack();
    }

    /**
     * Méthode appelée par JavaFX à l'initialisation du contrôleur.
     * Elle initialise l'image de fond et définit des largeurs maximales pour les tableaux.
     */
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, backgroundImageView);
        this.unitTable.setMaxWidth(1000);
        this.buildingTable.setMaxWidth(1000);
    }
}
