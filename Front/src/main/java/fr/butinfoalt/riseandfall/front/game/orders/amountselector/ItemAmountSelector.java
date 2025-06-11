package fr.butinfoalt.riseandfall.front.game.orders.amountselector;

import fr.butinfoalt.riseandfall.util.Dispatcher;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ItemAmountSelector<T> extends HBox {
    /**
     * L'entrée de l'élément achetable
     */
    protected final ObjectIntMap.Entry<T> entry;
    /**
     * Fonction de validation supplémentaire de la quantité en plus de celle du prix.
     * Si null, aucune validation supplémentaire n'est effectuée.
     */
    protected final IntPredicate amountValidator;
    /**
     * Le bouton de diminution de la quantité.
     */
    protected final Button decreaseButton;
    /**
     * Le label affichant la quantité actuelle de l'élément.
     */
    protected final Label countLabel;
    /**
     * Le bouton d'augmentation de la quantité.
     */
    protected final Button increaseButton;
    /**
     * Le dispatcher pour la quantité de l'élément.
     * Il est utilisé pour notifier les changements de quantité.
     */
    private final Dispatcher<Integer> changeDispatcher = new Dispatcher<>(true);

    public ItemAmountSelector(ObjectIntMap.Entry<T> entry, IntPredicate amountValidator) {
        this.entry = entry;
        this.amountValidator = amountValidator;
        this.decreaseButton = new Button("-");
        this.countLabel = new Label(String.valueOf(entry.getValue()));
        this.increaseButton = new Button("+");

        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        ObservableList<Node> children = this.getChildren();

        this.decreaseButton.setOnAction(this::onDecreaseButtonClicked);
        this.increaseButton.setOnAction(this::onIncreaseButtonClicked);

        children.add(this.decreaseButton);
        children.add(this.countLabel);
        children.add(this.increaseButton);
    }

    /**
     * Constructeur de la classe ItemAmountSelector.
     * Utilisé pour les cas où aucune validation supplémentaire n'est nécessaire.
     *
     * @param entry L'entrée de l'élément achetable.
     */
    public ItemAmountSelector(ObjectIntMap.Entry<T> entry) {
        this(entry, null);
    }

    /**
     * Méthode appelée lorsque le bouton de diminution est cliqué.
     *
     * @param actionEvent L'événement d'action.
     */
    protected void onDecreaseButtonClicked(ActionEvent actionEvent) {
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
    protected void onIncreaseButtonClicked(ActionEvent actionEvent) {
        int newCount = entry.getValue() + 1;
        if (this.isAmountValid(newCount)) {
            this.setValue(newCount);
        }
        this.updateButtonsState();
    }

    /**
     * Définit la nouvelle valeur de l'élément et met à jour le label de quantité.
     * Notifie également les écouteurs du changement de quantité.
     *
     * @param newValue La nouvelle valeur à définir.
     */
    protected void setValue(int newValue) {
        this.entry.setValue(newValue);
        this.countLabel.setText(String.valueOf(newValue));
        this.changeDispatcher.dispatch(newValue);
    }

    /**
     * Vérifie si la quantité est invalide en fonction de la fonction de validation fournie.
     *
     * @param amount La quantité à valider.
     * @return true si la quantité est invalide, false sinon.
     */
    protected boolean isAmountValid(int amount) {
        return amount >= 0 && (this.amountValidator == null || this.amountValidator.test(amount));
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
