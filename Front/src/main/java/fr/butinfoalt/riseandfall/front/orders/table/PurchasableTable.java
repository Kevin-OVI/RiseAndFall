package fr.butinfoalt.riseandfall.front.orders.table;

import fr.butinfoalt.riseandfall.front.components.HeightAdaptedTableView;
import fr.butinfoalt.riseandfall.front.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.map.PurchasableItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Tableau affichant des éléments achetables.
 *
 * @param <T> Le type des éléments achetables, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableTable<T extends Enum<T> & PurchasableItem> extends HeightAdaptedTableView<PurchasableTableRow<T>> {
    /**
     * Colonne pour afficher le nom de l'élément.
     */
    private final TableColumn<PurchasableTableRow<T>, String> nameColumn;
    /**
     * Colonne pour afficher le sélecteur de quantité de l'élément.
     */
    private final TableColumn<PurchasableTableRow<T>, PurchasableItemAmountSelector<T>> quantityColumn;
    /**
     * Colonne pour afficher le prix unitaire de l'élément.
     */
    private final TableColumn<PurchasableTableRow<T>, Number> pricePerUnitColumn;

    /**
     * Constructeur de la classe PurchasableTable.
     * Ajoute des classes CSS pour le style et initialise les colonnes nom, quantité et prix unitaire.
     */
    public PurchasableTable() {
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        this.setEditable(false);
        this.getStyleClass().add("purchasable-table");

        this.getColumns().addListener((ListChangeListener<TableColumn<PurchasableTableRow<T>, ?>>) change -> {
            while (change.next()) {
                for (TableColumn<PurchasableTableRow<T>, ?> column : change.getAddedSubList()) {
                    column.getStyleClass().add("purchasable-table-column");
                }
            }
        });

        this.nameColumn = new TableColumn<>("Nom");
        this.nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItem().getDisplayName()));
        this.getColumns().add(this.nameColumn);

        this.quantityColumn = new TableColumn<>("Quantité");
        this.quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAmountSelector()));
        this.getColumns().add(this.quantityColumn);

        this.pricePerUnitColumn = new TableColumn<>("Prix unitaire");
        this.pricePerUnitColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getItem().getPrice()));
        this.getColumns().add(this.pricePerUnitColumn);
    }
}
