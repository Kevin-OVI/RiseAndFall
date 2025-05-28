package fr.butinfoalt.riseandfall.front.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.front.orders.table.BuildingsTable;
import fr.butinfoalt.riseandfall.front.orders.table.PurchasableTable;
import fr.butinfoalt.riseandfall.front.orders.table.PurchasableTableRow;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
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
import java.util.ArrayList;


/**
 * Contrôleur pour la vue de gestion des ordres.
 */
public class OrderController {
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
    private PurchasableTable<UnitType> unitTable;

    /**
     * Tableau contenant les bâtiments.
     */
    @FXML
    private BuildingsTable buildingTable;

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
    public void loadPendingOrders() {
        ClientPlayer player = RiseAndFall.getPlayer();
        float playerIntelligence = player.getIntelligence();
        this.pendingUnits = player.getUnitMap().createEmptyClone();
        this.pendingBuildings = player.getBuildingMap().createEmptyClone();

        for (BaseOrder order : player.getPendingOrders()) {
            if (order instanceof OrderCreateUnit orderCreateUnit) {
                pendingUnits.increment(orderCreateUnit.getUnitType(), orderCreateUnit.getCount());
            } else if (order instanceof OrderCreateBuilding orderCreateBuilding) {
                pendingBuildings.increment(orderCreateBuilding.getBuildingType(), orderCreateBuilding.getCount());
            }
        }

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
            unitTable.getItems().add(new PurchasableTableRow<>(entry.getKey(), selector));
        }

        this.buildingTable.getItems().clear();
        for (ObjectIntMap.Entry<BuildingType> entry : pendingBuildings) {
            Modifier<Integer> buildingModifier = allowedBuildingsCounter.addModifier(-entry.getValue());
            PurchasableItemAmountSelector<BuildingType> selector = new PurchasableItemAmountSelector<>(entry, goldCounter, playerIntelligence,
                    (amount) -> buildingModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> buildingModifier.setDelta(-amount));
            allowedBuildingsCounter.addListener(value -> selector.updateButtonsState());
            this.buildingTable.getItems().add(new PurchasableTableRow<>(entry.getKey(), selector));
        }

        goldCounter.setDispatchChanges(true);
        allowedBuildingsCounter.setDispatchChanges(true);
        allowedUnitsCounter.setDispatchChanges(true);
    }

    /**
     * Méthode appelée par JavaFX pour revenir à la vue précédente.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToPreviousView();
    }

    /**
     * Méthode appelée par JavaFX pour gérer l'action de sauvegarde.
     * Elle enregistre les ordres en attente du joueur et revient à la vue précédente.
     */
    @FXML
    private void handleSave() {
        ArrayList<BaseOrder> newOrders = new ArrayList<>();

        for (ObjectIntMap.Entry<UnitType> entry : this.pendingUnits) {
            int nbTroops = entry.getValue();
            if (nbTroops > 0) {
                newOrders.add(new OrderCreateUnit(entry.getKey(), nbTroops));
            }
        }
        for (ObjectIntMap.Entry<BuildingType> entry : this.pendingBuildings) {
            int nbHuts = entry.getValue();
            if (nbHuts > 0) {
                newOrders.add(new OrderCreateBuilding(entry.getKey(), nbHuts));
            }
        }
        try {
            RiseAndFall.getClient().sendPacket(new PacketUpdateOrders(newOrders));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des ordres", e);
            return;
        }
        RiseAndFall.getPlayer().updatePendingOrders(newOrders);

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
