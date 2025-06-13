package fr.butinfoalt.riseandfall.front.game.orders.amountselector;

import fr.butinfoalt.riseandfall.gamelogic.data.PurchasableItem;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.counter.Counter;
import fr.butinfoalt.riseandfall.util.counter.Modifier;

import java.util.function.IntPredicate;

/**
 * Composant pour sélectionner la quantité d'un élément achetable.
 *
 * @param <T> Le type de l'élément achetable, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableItemAmountSelector<T extends PurchasableItem> extends ItemAmountSelector<T> {

    /**
     * La quantité d'intelligence du joueur.
     */
    private final float playerIntelligence;

    /**
     * Le modificateur d'or associé à cet élément.
     */
    private final Modifier<Float> goldModifier;

    /**
     * Constructeur de la classe PurchasableItemAmountSelector.
     *
     * @param entry              L'entrée de l'élément achetable.
     * @param goldCounter        Le compteur d'or à modifier.
     * @param playerIntelligence La quantité d'intelligence du joueur.
     * @param amountValidator    Fonction de validation de la quantité.
     */
    public PurchasableItemAmountSelector(ObjectIntMap.Entry<T> entry, Counter<Float> goldCounter, float playerIntelligence, IntPredicate amountValidator) {
        super(entry, amountValidator);
        this.playerIntelligence = playerIntelligence;
        this.goldModifier = goldCounter.addModifier(-entry.getKey().getPrice() * entry.getValue());
        goldCounter.addListener(goldAmount -> this.updateButtonsState());
    }

    /**
     * Constructeur de la classe PurchasableItemAmountSelector.
     *
     * @param entry              L'entrée de l'élément achetable.
     * @param playerIntelligence La quantité d'intelligence du joueur.
     * @param goldCounter        Le compteur d'or à modifier.
     */
    public PurchasableItemAmountSelector(ObjectIntMap.Entry<T> entry, Counter<Float> goldCounter, float playerIntelligence) {
        this(entry, goldCounter, playerIntelligence, null);
    }

    @Override
    protected void setValue(int newValue) {
        super.setValue(newValue);
        this.goldModifier.setDelta(-entry.getKey().getPrice() * newValue);
    }

    @Override
    protected boolean isAmountValid(int amount) {
        return super.isAmountValid(amount) &&
                this.goldModifier.computeWithAlternativeDelta(-entry.getKey().getPrice() * amount) >= 0 &&
                this.playerIntelligence >= entry.getKey().getRequiredIntelligence();
    }
}
