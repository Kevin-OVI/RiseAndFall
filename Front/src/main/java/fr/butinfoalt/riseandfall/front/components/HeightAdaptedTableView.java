package fr.butinfoalt.riseandfall.front.components;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * TableView qui s'adapte à la hauteur de son contenu.
 * Évite d'avoir plein de lignes vides en bas du tableau.
 *
 * @param <S> Le type des éléments contenus dans la TableView.
 */
public class HeightAdaptedTableView<S> extends TableView<S> {
    public HeightAdaptedTableView() {
        this.init();
    }

    public HeightAdaptedTableView(ObservableList<S> items) {
        super(items);
        this.init();
    }

    /**
     * Méthode appelée dans chaque constructeur pour ajouter un listener
     * qui met à jour la hauteur de la TableView lorsque le nombre d'éléments change.
     * On considère qu'une ligne fait 40 pixels de haut.
     */
    private void init() {
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        this.setEditable(false);
        // On empêche la sélection d'une ligne en forçant la sélection à null
        this.selectionModelProperty().bind(new ReadOnlyObjectWrapper<>(null));

        this.getStyleClass().add("table");
        this.getColumns().addListener((ListChangeListener<TableColumn<?, ?>>) change -> {
            while (change.next()) {
                for (TableColumn<?, ?> column : change.getAddedSubList()) {
                    column.getStyleClass().add("table-column");
                }
            }
        });

        this.setFixedCellSize(40);
        InvalidationListener listener = (observable) -> this.updateHeight();
        this.getItems().addListener(listener);
        listener.invalidated(this.itemsProperty());
    }

    public void updateHeight() {
        this.setMaxHeight((this.getItems().size() + 1) * this.getFixedCellSize());
    }
}
