package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.components.HeightAdaptedTableView;
import fr.butinfoalt.riseandfall.front.game.orders.amountselector.ItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

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
        this.nameColumn = new TableColumn<>("Nom");
        this.quantityColumn = new TableColumn<>("Quantité");

        this.nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItem().getName()));
        this.getColumns().add(this.nameColumn);

        this.quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAmountSelector()));
        this.getColumns().add(this.quantityColumn);
    }
}
