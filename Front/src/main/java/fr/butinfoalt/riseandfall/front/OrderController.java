package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.components.BuildingAmountSelector;
import fr.butinfoalt.riseandfall.front.components.UnitItemSelector;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt.riseandfall.gamelogic.counter.Modifier;
import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
     * Champ pour le composant contenant la quantité d'unités pouvant être produites.
     */
    @FXML
    private Label unitsField;
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
     * Champ pour le composant contenant le prix total.
     */
    @FXML
    public Label totalPrice;

    /**
     * Méthode pour charger les ordres en attente du joueur dans l'interface.
     * Elle met à jour les composants de l'interface utilisateur
     * pour afficher les unités et bâtiments en attente de création,
     * ainsi que la quantité d'or disponible.
     */
    public void loadPendingOrders() {
        ClientPlayer player = RiseAndFall.getPlayer();
        this.pendingUnits = new EnumIntMap<>(UnitType.class);
        this.pendingBuildings = new EnumIntMap<>(BuildingType.class);

        for (BaseOrder order : player.getPendingOrders()) {
            if (order instanceof OrderCreateUnit orderCreateUnit) {
                pendingUnits.increment(orderCreateUnit.getUnitType(), orderCreateUnit.getCount());
            } else if (order instanceof OrderCreateBuilding orderCreateBuilding) {
                pendingBuildings.increment(orderCreateBuilding.getBuildingType(), orderCreateBuilding.getCount());
            }
        }

        Counter goldCounter = new Counter(player.getGoldAmount());
        goldCounter.addListener(goldAmount -> {
            this.goldField.setText("Or restant : " + goldAmount);
            this.totalPrice.setText("Prix total : " + (goldCounter.getInitialValue() - goldAmount));
        });
        Counter allowedUnitsCounter = new Counter(player.getAllowedUnitCount());
        allowedUnitsCounter.addListener(allowedCount -> this.unitsField.setText("Entrainements d'unités restants : " + allowedCount));
        Counter allowedBuildingsCounter = new Counter(5);

        this.unitVBox.getChildren().clear();
        TableView<UnitTypeRow> unitTable = new javafx.scene.control.TableView<>();
        unitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<UnitTypeRow, String> nameCol = new TableColumn<>("Unité");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<UnitTypeRow, UnitItemSelector> controlCol = new TableColumn<>("Quantité");
        controlCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSelector()));
        controlCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(UnitItemSelector item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        TableColumn<UnitTypeRow, String> priceCol = new TableColumn<>("Prix");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.valueOf(data.getValue().getName())
        ));

        unitTable.getColumns().addAll(nameCol, controlCol, priceCol);

        for (EnumIntMap.Entry<UnitType> entry : pendingUnits) {
            Modifier unitsModifier = allowedUnitsCounter.addModifier(-entry.getValue());
            UnitItemSelector selector = new UnitItemSelector(entry, goldCounter,
                    (amount) -> unitsModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> unitsModifier.setDelta(-amount));
            unitTable.getItems().add(new UnitTypeRow(entry.getKey().toString(), selector));
        }

        this.unitVBox.getChildren().add(unitTable);

        this.buildingsVBox.getChildren().clear();
        javafx.scene.control.TableView<BuildingTypeRow> buildingTable;
        buildingTable = new TableView<>();
        buildingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BuildingTypeRow, String> nameColB = new TableColumn<>("Bâtiment");
        nameColB.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<BuildingTypeRow, BuildingAmountSelector> controlColB = new TableColumn<>("Quantité");
        controlColB.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSelector()));
        controlColB.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BuildingAmountSelector item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        TableColumn<BuildingTypeRow, String> priceColB = new TableColumn<>("Prix");
        priceColB.setCellValueFactory(data -> new SimpleStringProperty(
            String.valueOf(data.getValue().getName())
        ));

        buildingTable.getColumns().addAll(nameColB, controlColB, priceColB);

        for (EnumIntMap.Entry<BuildingType> entry : pendingBuildings) {
            Modifier buildingModifier = allowedBuildingsCounter.addModifier(-entry.getValue());
            BuildingAmountSelector selector = new BuildingAmountSelector(entry, goldCounter,
                    (amount) -> buildingModifier.computeWithAlternativeDelta(-amount) >= 0);
            selector.addListener(amount -> buildingModifier.setDelta(-amount));
            allowedBuildingsCounter.addListener(value -> selector.updateButtonsState());
            buildingTable.getItems().add(new BuildingTypeRow(entry.getKey().toString(), selector));
        }

        this.buildingsVBox.getChildren().add(buildingTable);

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
        ClientPlayer player = RiseAndFall.getPlayer();
        player.clearPendingOrders();
        for (EnumIntMap.Entry<UnitType> entry : this.pendingUnits) {
            int nbTroops = entry.getValue();
            if (nbTroops > 0) {
                player.addPendingOrder(new OrderCreateUnit(entry.getKey(), nbTroops));
            }
        }
        for (EnumIntMap.Entry<BuildingType> entry : this.pendingBuildings) {
            int nbHuts = entry.getValue();
            if (nbHuts > 0) {
                player.addPendingOrder(new OrderCreateBuilding(entry.getKey(), nbHuts));
            }
        }

        this.switchBack();
    }
    public static class UnitTypeRow {
        private final String name;
        private final UnitItemSelector selector;

        public UnitTypeRow(String name, UnitItemSelector selector) {
            this.name = name;
            this.selector = selector;
        }

        public String getName() { return name; }

        public UnitItemSelector getSelector() { return selector; }
    }

    public static class BuildingTypeRow {
        private final String name;
        private final BuildingAmountSelector selector;

        public BuildingTypeRow(String name, BuildingAmountSelector selector) {
            this.name = name;
            this.selector = selector;
        }

        public String getName() { return name; }

        public BuildingAmountSelector getSelector() { return selector; }
    }
}
