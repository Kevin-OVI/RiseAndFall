package fr.butinfoalt1.riseandfall.front.components;

import fr.butinfoalt1.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt1.riseandfall.gamelogic.counter.Modifier;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt1.riseandfall.gamelogic.map.PurchasableItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Function;

/**
 * Composant pour sélectionner la quantité d'un élément achetable.
 *
 * @param <T> Le type de l'élément achetable, qui doit être une énumération implémentant PurchasableItem.
 */
public class PurchasableItemAmountSelector<T extends Enum<T> & PurchasableItem> extends HBox {
    /**
     * L'entrée de l'élément achetable
     */
    private final EnumIntMap.Entry<T> entry;

    /**
     * Fonction de validation supplémentaire de la quantité en plus de celle du prix.
     * Si null, aucune validation supplémentaire n'est effectuée.
     */
    private final Function<Integer, Boolean> amountValidator;

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
     * @param entry           L'entrée de l'élément achetable.
     * @param goldCounter     Le compteur d'or à modifier.
     * @param amountValidator Fonction de validation de la quantité.
     */
    public PurchasableItemAmountSelector(EnumIntMap.Entry<T> entry, Counter goldCounter, Function<Integer, Boolean> amountValidator) {
        this.entry = entry;
        this.amountValidator = amountValidator;
        this.goldModifier = goldCounter.addModifier(-entry.getKey().getPrice() * entry.getValue());

        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        ObservableList<Node> children = this.getChildren();

        Label nameLabel = new Label(entry.getKey().getDisplayName() + " :");
        this.decreaseButton = new Button("-");
        this.countLabel = new Label(String.valueOf(entry.getValue()));
        this.increaseButton = new Button("+");

        this.decreaseButton.setOnAction(this::onDecreaseButtonClicked);
        this.increaseButton.setOnAction(this::onIncreaseButtonClicked);

        this.updateDecreaseButtonState();
        goldCounter.addChangeListener(goldAmount -> increaseButton.setDisable(goldAmount < entry.getKey().getPrice() || this.isAmountInvalid(entry.getValue() + 1)));

        children.add(nameLabel);
        children.add(this.decreaseButton);
        children.add(this.countLabel);
        children.add(this.increaseButton);
    }

    /**
     * Constructeur de la classe PurchasableItemAmountSelector.
     *
     * @param entry       L'entrée de l'élément achetable.
     * @param goldCounter Le compteur d'or à modifier.
     */
    public PurchasableItemAmountSelector(EnumIntMap.Entry<T> entry, Counter goldCounter) {
        this(entry, goldCounter, null);
    }

    /**
     * Méthode appelée lorsque le bouton de diminution est cliqué.
     *
     * @param actionEvent L'événement d'action.
     */
    private void onDecreaseButtonClicked(ActionEvent actionEvent) {
        int count = this.entry.getValue();
        if (count > 0) {
            entry.setValue(--count);
            countLabel.setText(String.valueOf(count));
            this.goldModifier.setDelta(-entry.getKey().getPrice() * count);
        }
        this.updateDecreaseButtonState();
    }

    /**
     * Méthode appelée lorsque le bouton d'augmentation est cliqué.
     *
     * @param actionEvent L'événement d'action.
     */
    private void onIncreaseButtonClicked(ActionEvent actionEvent) {
        int count = entry.getValue();
        if (this.goldModifier.getCounter().getCurrentValue() >= entry.getKey().getPrice()) {
            entry.setValue(++count);
            countLabel.setText(String.valueOf(count));
            this.goldModifier.setDelta(-entry.getKey().getPrice() * count);
        }
        this.updateDecreaseButtonState();
    }

    /**
     * Met à jour l'état du bouton de diminution en fonction de la quantité actuelle.
     */
    private void updateDecreaseButtonState() {
        int count = entry.getValue();
        decreaseButton.setDisable(count <= 0 || this.isAmountInvalid(count - 1));
    }

    /**
     * Vérifie si la quantité est invalide en fonction de la fonction de validation fournie.
     *
     * @param amount La quantité à valider.
     * @return true si la quantité est invalide, false sinon.
     */
    private boolean isAmountInvalid(int amount) {
        if (this.amountValidator != null) {
            return this.amountValidator.apply(amount);
        }
        return false;
    }
}
