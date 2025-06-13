package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.game.orders.amountselector.ItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;

/**
 * Représente une ligne dans un tableau d'éléments només
 *
 * @param <T> Le type de l'élément, qui doit implémenter NamedItem.
 * @param <S> Le type du sélecteur de quantité pour cet élément, qui doit étendre ItemAmountSelector<T>.
 */
public class ItemTableRow<T extends NamedItem, S extends ItemAmountSelector<T>> {
    /**
     * L'élément.
     */
    protected final T item;

    /**
     * Sélecteur de quantité pour cet élément.
     */
    protected final S amountSelector;

    /**
     * Constructeur de la classe ItemTableRow.
     *
     * @param item           L'élément.
     * @param amountSelector Le sélecteur de quantité pour cet élément.
     */
    public ItemTableRow(T item, S amountSelector) {
        this.item = item;
        this.amountSelector = amountSelector;
    }

    /**
     * Retourne l'élément achetable.
     *
     * @return L'élément achetable.
     */
    public T getItem() {
        return this.item;
    }

    /**
     * Retourne le sélecteur de quantité pour cet élément.
     *
     * @return Le sélecteur de quantité.
     */
    public S getAmountSelector() {
        return this.amountSelector;
    }
}
