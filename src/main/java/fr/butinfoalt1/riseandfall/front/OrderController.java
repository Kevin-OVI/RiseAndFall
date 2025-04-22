package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.components.PurchasableItemAmountSelector;
import fr.butinfoalt1.riseandfall.gamelogic.Player;
import fr.butinfoalt1.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt1.riseandfall.gamelogic.counter.Modifier;
import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt1.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt1.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Contrôleur pour la vue de gestion des ordres.
 */
public class OrderController {
    /**
     * Liste des unités en attente de création.
     */
    private EnumIntMap<UnitType> pendingUnits;
    /**
     * Liste des bâtiments en attente de création.
     */
    private EnumIntMap<BuildingType> pendingBuildings;

    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    private Label goldField;

    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    private Label IntelligenceField;

    /**
     * Champ pour le composant contenant les unités.
     */
    @FXML
    private VBox unitVBox;

    /**
     * Champ pour le composant contenant les bâtiments.
     */
    @FXML
    private VBox buildingsVBox;

    /**
     * Champ pour le composant de la quantité totale d'unités.
     */
    @FXML
    private Label totalUnitsField;

    /**
     * Champ pour le composant de la quantité totale de bâtiments.
     */
    @FXML
    private Label totalBuildingsField;

    /**
     * Méthode pour charger les ordres en attente du joueur dans l'interface.
     * Elle met à jour les composants de l'interface utilisateur
     * pour afficher les unités et bâtiments en attente de création,
     * ainsi que la quantité d'or disponible.
     */
    public void loadPendingOrders() {
        this.pendingUnits = new EnumIntMap<>(UnitType.class);
        this.pendingBuildings = new EnumIntMap<>(BuildingType.class);

        for (BaseOrder order : Player.SINGLE_PLAYER.getPendingOrders()) {
            if (order instanceof OrderCreateUnit orderCreateUnit) {
                pendingUnits.increment(orderCreateUnit.getUnitType(), orderCreateUnit.getCount());
            } else if (order instanceof OrderCreateBuilding orderCreateBuilding) {
                pendingBuildings.increment(orderCreateBuilding.getBuildingType(), orderCreateBuilding.getCount());
            }
        }

        Counter goldCounter = new Counter(Player.SINGLE_PLAYER.getGoldAmount());
        goldCounter.addListener(goldAmount -> this.goldField.setText(String.valueOf(goldAmount)));
        Counter allowedUnitsCounter = new Counter(Player.SINGLE_PLAYER.getAllowedUnitCount());
        Counter allowedBuildingsCounter = new Counter(5);

        this.unitVBox.getChildren().clear();
        for (EnumIntMap.Entry<UnitType> entry : pendingUnits) {
            Modifier unitsModifier = allowedUnitsCounter.addModifier(-entry.getValue());
            this.unitVBox.getChildren().add(new PurchasableItemAmountSelector<>(entry, goldCounter,
                    (amount) -> unitsModifier.computeWithAlternativeDelta(-amount) >= 0));
        }

        this.buildingsVBox.getChildren().clear();
        for (EnumIntMap.Entry<BuildingType> entry : pendingBuildings) {
            Modifier buildingModifier = allowedBuildingsCounter.addModifier(-entry.getValue());
            PurchasableItemAmountSelector<BuildingType> selector = new PurchasableItemAmountSelector<>(entry, goldCounter,
                    (amount) -> buildingModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> buildingModifier.setDelta(-amount));
            allowedBuildingsCounter.addListener(value -> selector.updateButtonsState());
            this.buildingsVBox.getChildren().add(selector);
        }

        goldCounter.setDispatchChanges(true);
        allowedBuildingsCounter.setDispatchChanges(true);

        int totalUnits = Player.SINGLE_PLAYER.getUnitsCount();
        int totalBuildings = Player.SINGLE_PLAYER.getBuildingsCount();

        totalUnitsField.setText("Unités totales : " + totalUnits);
        totalBuildingsField.setText("Bâtiments totaux : " + totalBuildings);
        IntelligenceField.setText(String.valueOf(Player.SINGLE_PLAYER.getIntelligence()));
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
        Player.SINGLE_PLAYER.clearPendingOrders();
        for (EnumIntMap.Entry<UnitType> entry : this.pendingUnits) {
            int nbTroops = entry.getValue();
            if (nbTroops > 0) {
                Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateUnit(entry.getKey(), nbTroops));
            }
        }
        for (EnumIntMap.Entry<BuildingType> entry : this.pendingBuildings) {
            int nbHuts = entry.getValue();
            if (nbHuts > 0) {
                Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateBuilding(entry.getKey(), nbHuts));
            }
        }

        this.switchBack();
    }
}
