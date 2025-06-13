package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.components.HeightAdaptedTableView;
import fr.butinfoalt.riseandfall.front.game.orders.amountselector.ItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public abstract class ItemTable<T extends NamedItem, S extends ItemAmountSelector<T>> extends HeightAdaptedTableView<ItemTableRow<T, S>> {
    /**
     * Colonne pour afficher le nom de l'élément.
     */
    protected final TableColumn<ItemTableRow<T, S>, String> nameColumn;
    /**
     * Colonne pour afficher le sélecteur de quantité de l'élément.
     */
    protected final TableColumn<ItemTableRow<T, S>, ItemAmountSelector<T>> quantityColumn;

    public ItemTable() {
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        this.setEditable(false);
        // On empêche la sélection d'une ligne en forçant la sélection à null
        this.selectionModelProperty().bind(new ReadOnlyObjectWrapper<>(null));
        this.getStyleClass().add("purchasable-table");

        this.getColumns().addListener((ListChangeListener<TableColumn<ItemTableRow<T, S>, ?>>) change -> {
            while (change.next()) {
                for (TableColumn<ItemTableRow<T, S>, ?> column : change.getAddedSubList()) {
                    column.getStyleClass().add("purchasable-table-column");
                }
            }
        });

        this.nameColumn = new TableColumn<>("Nom");
        this.quantityColumn = new TableColumn<>("Quantité");

        this.nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItem().getName()));
        this.getColumns().add(this.nameColumn);

        this.quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAmountSelector()));
        this.getColumns().add(this.quantityColumn);
    }
}
