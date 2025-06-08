package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.game.orders.amountselector.PurchasableItemAmountSelector;
import fr.butinfoalt.riseandfall.gamelogic.data.PurchasableItem;

/**
 * Représente une ligne dans un tableau d'éléments achetables.
 *
 * @param <T> Le type de l'élément achetable, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableTableRow<T extends PurchasableItem> {
    /**
     * L'élément achetable.
     */
    private final T item;
    /**
     * Sélecteur de quantité pour cet élément.
     */
    private final PurchasableItemAmountSelector<T> amountSelector;

    /**
     * Constructeur de la classe PurchasableTableRow.
     *
     * @param item           L'élément achetable.
     * @param amountSelector Le sélecteur de quantité pour cet élément.
     */
    public PurchasableTableRow(T item, PurchasableItemAmountSelector<T> amountSelector) {
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
    public PurchasableItemAmountSelector<T> getAmountSelector() {
        return this.amountSelector;
    }
}
