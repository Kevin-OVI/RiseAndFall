package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.game.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.TableColumn;


/**
 * Tableau affichant des unités achetables.
 * Il hérite de PurchasableTable et ajoute des colonnes spécifiques pour les unités.
 */
public class UnitsPurchaseTable extends PurchasableTable<UnitType> {
    private final TableColumn<ItemTableRow<UnitType, PurchasableItemAmountSelector<UnitType>>, Number> healthColumn;
    private final TableColumn<ItemTableRow<UnitType, PurchasableItemAmountSelector<UnitType>>, Number> damageColumn;

    public UnitsPurchaseTable() {
        super();

        Race race = RiseAndFall.getPlayer().getRace();

        this.healthColumn = new TableColumn<>("Vie");
        this.healthColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getItem().getHealth() * race.getHealthMultiplier()));
        this.getColumns().add(this.healthColumn);

        this.damageColumn = new TableColumn<>("Dégâts");
        this.damageColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getItem().getDamage() * race.getDamageMultiplier()));
        this.getColumns().add(this.damageColumn);
    }
}
