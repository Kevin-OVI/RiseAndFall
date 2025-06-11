package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.game.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.data.PurchasableItem;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.TableColumn;

/**
 * Tableau affichant des éléments achetables.
 *
 * @param <T> Le type des éléments achetables, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableTable<T extends PurchasableItem> extends ItemTable<T, PurchasableItemAmountSelector<T>> {
    /**
     * Colonne pour afficher le prix unitaire de l'élément.
     */
    private final TableColumn<ItemTableRow<T, PurchasableItemAmountSelector<T>>, Number> pricePerUnitColumn;

    /**
     * Colonne pour afficher l'intelligence requise de l'élément.
     */
    private final TableColumn<ItemTableRow<T, PurchasableItemAmountSelector<T>>, Number> requiredIntelligenceColumn;

    /**
     * Constructeur de la classe PurchasableTable.
     * Ajoute des classes CSS pour le style et initialise les colonnes nom, quantité et prix unitaire.
     */
    public PurchasableTable() {
        super();

        this.pricePerUnitColumn = new TableColumn<>("Prix unitaire");
        this.pricePerUnitColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getItem().getPrice()));
        this.getColumns().add(this.pricePerUnitColumn);

        this.requiredIntelligenceColumn = new TableColumn<>("Intelligence requise");
        this.requiredIntelligenceColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getItem().getRequiredIntelligence()));
        this.getColumns().add(this.requiredIntelligenceColumn);
    }
}
