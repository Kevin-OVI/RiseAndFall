package fr.butinfoalt.riseandfall.front.components;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
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
        InvalidationListener listener = (observable) -> {
            int height = (this.getItems().size() + 1) * 40;
            this.setMaxHeight(height);
            this.setMaxHeight(height);
        };
        this.getItems().addListener(listener);
        listener.invalidated(this.itemsProperty());
    }
}
