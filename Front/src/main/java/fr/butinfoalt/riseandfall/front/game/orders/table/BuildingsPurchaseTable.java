package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.game.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableColumn;


/**
 * Tableau affichant des bâtiments achetables.
 * Il hérite de PurchasableTable et ajoute des colonnes spécifiques pour les bâtiments.
 */
public class BuildingsPurchaseTable extends PurchasableTable<BuildingType> {
    /**
     * Colonne pour afficher la production d'or du bâtiment.
     */
    private final TableColumn<ItemTableRow<BuildingType, PurchasableItemAmountSelector<BuildingType>>, String> goldProductionColumn;
    /**
     * Colonne pour afficher la production d'intelligence du bâtiment.
     */
    private final TableColumn<ItemTableRow<BuildingType, PurchasableItemAmountSelector<BuildingType>>, String> intelligenceProductionColumn;
    /**
     * Colonne pour afficher la résistance du bâtiment.
     */
    private final TableColumn<ItemTableRow<BuildingType, PurchasableItemAmountSelector<BuildingType>>, String> resistanceColumn;
    /**
     * Colonne pour afficher la capacité d'hébergement du bâtiment.
     */
    private final TableColumn<ItemTableRow<BuildingType, PurchasableItemAmountSelector<BuildingType>>, Number> hostingCapacityColumn;

    /**
     * Constructeur de la classe BuildingsTable.
     * Ajoute au tableau les colonnes spécifiques aux bâtiments (production d'or, production d'intelligence et capacité d'hébergement).
     */
    public BuildingsPurchaseTable() {
        super();

        Race race = RiseAndFall.getPlayer().getRace();

        this.goldProductionColumn = new TableColumn<>("Production d'or");
        this.goldProductionColumn.setCellValueFactory(data -> new FloatOptimisedDisplayProperty(data.getValue().getItem().getGoldProduction() * race.getGoldMultiplier()));
        this.getColumns().add(this.goldProductionColumn);

        this.intelligenceProductionColumn = new TableColumn<>("Production d'intelligence");
        this.intelligenceProductionColumn.setCellValueFactory(data -> new FloatOptimisedDisplayProperty(data.getValue().getItem().getIntelligenceProduction() * race.getIntelligenceMultiplier()));
        this.getColumns().add(this.intelligenceProductionColumn);

        this.resistanceColumn = new TableColumn<>("Résistance");
        this.resistanceColumn.setCellValueFactory(data -> new FloatOptimisedDisplayProperty(data.getValue().getItem().getResistance()));
        this.getColumns().add(this.resistanceColumn);

        this.hostingCapacityColumn = new TableColumn<>("Capacité d'hébergement");
        this.hostingCapacityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getItem().getMaxUnits()));
        this.getColumns().add(this.hostingCapacityColumn);
    }
}
