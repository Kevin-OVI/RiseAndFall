package fr.butinfoalt.riseandfall.front.game;

import fr.butinfoalt.riseandfall.front.components.HeightAdaptedTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tableau simple pour afficher des paires clé-valeur.
 */
public class SimpleTable extends HeightAdaptedTableView<SimpleTable.SimpleTableRow> {
    /**
     * Colonne pour afficher la clé
     */
    private final TableColumn<SimpleTableRow, String> keyColumn;

    /**
     * Colonne pour afficher la valeur
     */
    private final TableColumn<SimpleTableRow, String> valueColumn;

    /**
     * Constructeur de la classe SimpleTable.
     * Initialise les colonnes pour la clé et la valeur.
     */
    public SimpleTable() {
        this.keyColumn = new TableColumn<>();
        this.keyColumn.setCellValueFactory(row -> row.getValue().getKeyProperty());
        this.getColumns().add(this.keyColumn);

        this.valueColumn = new TableColumn<>();
        this.valueColumn.setCellValueFactory(param -> param.getValue().getValueProperty());
        this.getColumns().add(this.valueColumn);
    }

    /**
     * Définit le nom de la colonne pour la clé.
     *
     * @param name Le nom de la colonne pour la clé.
     */
    public void setKeyColumnName(String name) {
        this.keyColumn.setText(name);
    }

    /**
     * Définit le nom de la colonne pour la valeur.
     *
     * @param name Le nom de la colonne pour la valeur.
     */
    public void setValueColumnName(String name) {
        this.valueColumn.setText(name);
    }

    /**
     * Définit les éléments du tableau avec un tableau de SimpleTableRow.
     *
     * @param items Les éléments à ajouter au tableau.
     */
    public void setItems(SimpleTableRow... items) {
        ObservableList<SimpleTableRow> itemList = this.getItems();
        itemList.clear();
        itemList.addAll(Arrays.asList(items));
    }


    public void setItems(Collection<SimpleTableRow> items) {
        ObservableList<SimpleTableRow> itemList = this.getItems();
        itemList.clear();
        itemList.addAll(items);
    }


    /**
     * Définit les éléments du tableau avec une liste d'éléments.
     *
     * @param items Les éléments à ajouter au tableau.
     */
    public static class SimpleTableRow {
        private final SimpleStringProperty keyProperty;
        private final SimpleStringProperty valueProperty;

        public SimpleTableRow(String key, String value) {
            this.keyProperty = new SimpleStringProperty(key);
            this.valueProperty = new SimpleStringProperty(value);
        }

        public SimpleTableRow(String name, int value) {
            this(name, String.valueOf(value));
        }

        public SimpleStringProperty getKeyProperty() {
            return this.keyProperty;
        }

        public SimpleStringProperty getValueProperty() {
            return this.valueProperty;
        }

        public void setValue(String value) {
            this.valueProperty.set(value);
        }
    }
}
