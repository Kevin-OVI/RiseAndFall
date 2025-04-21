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

public class PurchasableItemAmountSelector<T extends Enum<T> & PurchasableItem> extends HBox {
    private final EnumIntMap.Entry<T> entry;
    private final Function<Integer, Boolean> amountValidator;
    private final Modifier goldModifier;
    private final Button decreaseButton;
    private final Label countLabel;
    private final Button increaseButton;

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
        goldCounter.addChangeListener(goldAmount -> increaseButton.setDisable(goldAmount < entry.getKey().getPrice() || !this.checkAmountValid(entry.getValue() + 1)));

        children.add(nameLabel);
        children.add(this.decreaseButton);
        children.add(this.countLabel);
        children.add(this.increaseButton);
    }

    public PurchasableItemAmountSelector(EnumIntMap.Entry<T> entry, Counter goldCounter) {
        this(entry, goldCounter, null);
    }

    private void onDecreaseButtonClicked(ActionEvent actionEvent) {
        int count = this.entry.getValue();
        if (count > 0) {
            entry.setValue(--count);
            countLabel.setText(String.valueOf(count));
            this.goldModifier.setDelta(-entry.getKey().getPrice() * count);
        }
        this.updateDecreaseButtonState();
    }

    private void onIncreaseButtonClicked(ActionEvent actionEvent) {
        int count = entry.getValue();
        if (this.goldModifier.getCounter().getCurrentValue() >= entry.getKey().getPrice()) {
            entry.setValue(++count);
            countLabel.setText(String.valueOf(count));
            this.goldModifier.setDelta(-entry.getKey().getPrice() * count);
        }
        this.updateDecreaseButtonState();
    }

    private void updateDecreaseButtonState() {
        int count = entry.getValue();
        decreaseButton.setDisable(count <= 0 || !this.checkAmountValid(count - 1));
    }

    private boolean checkAmountValid(int amount) {
        if (this.amountValidator != null) {
            return this.amountValidator.apply(amount);
        }
        return true;
    }
}
