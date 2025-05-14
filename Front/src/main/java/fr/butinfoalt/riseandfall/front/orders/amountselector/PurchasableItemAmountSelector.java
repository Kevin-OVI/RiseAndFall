package fr.butinfoalt.riseandfall.front.orders.amountselector;

import fr.butinfoalt.riseandfall.util.Dispatcher;
import fr.butinfoalt.riseandfall.util.counter.Counter;
import fr.butinfoalt.riseandfall.util.counter.Modifier;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.gamelogic.data.PurchasableItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Composant pour sélectionner la quantité d'un élément achetable.
 *
 * @param <T> Le type de l'élément achetable, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableItemAmountSelector<T extends PurchasableItem> extends HBox {
    /**
     * L'entrée de l'élément achetable
     */
    protected final ObjectIntMap.Entry<T> entry;

    /**
     * La quantité d'intelligence du joueur.
     */
    private int playerIntelligence;

    /**
     * Fonction de validation supplémentaire de la quantité en plus de celle du prix.
     * Si null, aucune validation supplémentaire n'est effectuée.
     */
    private final Function<Integer, Boolean> amountValidator;

    /**
     * Le dispatcher pour la quantité de l'élément.
     * Il est utilisé pour notifier les changements de quantité.
     */
    private final Dispatcher<Integer> changeDispatcher = new Dispatcher<>(true);

    /**
     * Le modificateur d'or associé à cet élément.
     */
    private final Modifier goldModifier;

    /**
     * Le bouton de diminution de la quantité.
     */
    private final Button decreaseButton;

    /**
     * Le label affichant la quantité actuelle de l'élément.
     */
    private final Label countLabel;

    /**
     * Le bouton d'augmentation de la quantité.
     */
    private final Button increaseButton;

    /**
     * Constructeur de la classe PurchasableItemAmountSelector.
     *
     * @param entry              L'entrée de l'élément achetable.
     * @param goldCounter        Le compteur d'or à modifier.
     * @param playerIntelligence La quantité d'intelligence du joueur.
     * @param amountValidator    Fonction de validation de la quantité.
     */
    public PurchasableItemAmountSelector(ObjectIntMap.Entry<T> entry, Counter goldCounter, int playerIntelligence, Function<Integer, Boolean> amountValidator) {
        this.entry = entry;
        this.playerIntelligence = playerIntelligence;
        this.amountValidator = amountValidator;
        this.goldModifier = goldCounter.addModifier(-entry.getKey().getPrice() * entry.getValue());

        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        ObservableList<Node> children = this.getChildren();

        this.decreaseButton = new Button("-");
        this.countLabel = new Label(String.valueOf(entry.getValue()));
        this.increaseButton = new Button("+");

        this.decreaseButton.setOnAction(this::onDecreaseButtonClicked);
        this.increaseButton.setOnAction(this::onIncreaseButtonClicked);

        goldCounter.addListener(goldAmount -> this.updateButtonsState());

        children.add(this.decreaseButton);
        children.add(this.countLabel);
        children.add(this.increaseButton);
    }

    /**
     * Constructeur de la classe PurchasableItemAmountSelector.
     *
     * @param entry              L'entrée de l'élément achetable.
     * @param playerIntelligence La quantité d'intelligence du joueur.
     * @param goldCounter        Le compteur d'or à modifier.
     */
    public PurchasableItemAmountSelector(ObjectIntMap.Entry<T> entry, Counter goldCounter, int playerIntelligence) {
        this(entry, goldCounter, playerIntelligence, null);
    }

    /**
     * Méthode appelée lorsque le bouton de diminution est cliqué.
     *
     * @param actionEvent L'événement d'action.
     */
    private void onDecreaseButtonClicked(ActionEvent actionEvent) {
        int newCount = entry.getValue() - 1;
        if (this.isAmountValid(newCount)) {
            this.setValue(newCount);
        }
        this.updateButtonsState();
    }

    /**
     * Méthode appelée lorsque le bouton d'augmentation est cliqué.
     *
     * @param actionEvent L'événement d'action.
     */
    private void onIncreaseButtonClicked(ActionEvent actionEvent) {
        int newCount = entry.getValue() + 1;
        if (this.isAmountValid(newCount)) {
            this.setValue(newCount);
        }
        this.updateButtonsState();
    }

    private void setValue(int newValue) {
        this.entry.setValue(newValue);
        this.countLabel.setText(String.valueOf(newValue));
        this.goldModifier.setDelta(-entry.getKey().getPrice() * newValue);
        this.changeDispatcher.dispatch(newValue);
    }

    /**
     * Vérifie si la quantité est invalide en fonction de la fonction de validation fournie.
     *
     * @param amount La quantité à valider.
     * @return true si la quantité est invalide, false sinon.
     */
    private boolean isAmountValid(int amount) {
        return amount >= 0 &&
                this.goldModifier.computeWithAlternativeDelta(-entry.getKey().getPrice() * amount) >= 0 &&
                this.playerIntelligence >= entry.getKey().getRequiredIntelligence() &&
                (this.amountValidator == null || this.amountValidator.apply(amount));
    }

    /**
     * Ajoute un écouteur pour les changements de quantité.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(Consumer<Integer> listener) {
        this.changeDispatcher.addListener(listener);
    }

    /**
     * Supprime un écouteur pour les changements de quantité.
     *
     * @param listener L'écouteur à supprimer.
     */
    public void removeListener(Consumer<Integer> listener) {
        this.changeDispatcher.removeListener(listener);
    }

    /**
     * Vérifie si les changements doivent être dispatchés.
     *
     * @return true si les changements doivent être dispatchés, false sinon.
     */
    public boolean isDispatchChanges() {
        return this.changeDispatcher.isDispatchChanges();
    }

    /**
     * Définit si les changements doivent être dispatchés.
     * Si true, la valeur actuelle sera distribuée immédiatement.
     *
     * @param dispatchChanges true pour activer le dispatching des changements, false sinon.
     */
    public void setDispatchChanges(boolean dispatchChanges) {
        this.changeDispatcher.setDispatchChanges(dispatchChanges);
        if (dispatchChanges) {
            this.changeDispatcher.dispatch(this.entry.getValue());
        }
    }

    /**
     * Met à jour l'état des boutons de sélection de quantité.
     */
    public void updateButtonsState() {
        int count = this.entry.getValue();
        this.decreaseButton.setDisable(!this.isAmountValid(count - 1));
        this.increaseButton.setDisable(!this.isAmountValid(count + 1));
    }
}
