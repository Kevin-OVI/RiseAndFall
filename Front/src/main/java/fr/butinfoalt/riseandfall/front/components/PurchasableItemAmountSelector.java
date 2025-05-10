package fr.butinfoalt.riseandfall.front.components;

import fr.butinfoalt.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt.riseandfall.gamelogic.counter.Modifier;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class PurchasableItemAmountSelector<T extends Enum<T>> extends HBox {
    protected final EnumIntMap.Entry<T> entry;
    protected final Counter goldCounter;
    protected final IntegerProperty countProperty;
    protected final Label countLabel;
    protected final Button minusButton;
    protected final Button plusButton;
    protected final List<Consumer<Integer>> listeners;
    protected Function<Integer, Boolean> amountValidator;
    private final Modifier goldModifier;

    public PurchasableItemAmountSelector(EnumIntMap.Entry<T> entry, Counter goldCounter,
            Function<Integer, Boolean> amountValidator) {
        this.entry = entry;
        this.goldCounter = goldCounter;
        this.amountValidator = amountValidator;

        this.listeners = new ArrayList<>();
        this.countProperty = new SimpleIntegerProperty(entry.getValue());

        // Calcul du prix unitaire
        int price = 0;
        try {
            var method = entry.getKey().getClass().getMethod("getPrice");
            price = (int) method.invoke(entry.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int unitPrice = price;
        this.goldModifier = goldCounter.addModifier(-unitPrice * entry.getValue());

        // Bouton -
        this.minusButton = new Button("-");
        this.minusButton.setOnAction(event -> {
            int newCount = this.countProperty.get() - 1;
            if (newCount >= 0) {
                this.countProperty.set(newCount);
                this.entry.setValue(newCount);
                this.goldModifier.setDelta(-unitPrice * newCount);
                this.notifyListeners(newCount);
                this.updateButtonsState();
            }
        });

        // Label pour le compteur
        this.countLabel = new Label(String.valueOf(this.countProperty.get()));
        this.countProperty.addListener((observable, oldValue, newValue) -> {
            this.countLabel.setText(String.valueOf(newValue.intValue()));
        });

        // Bouton +
        this.plusButton = new Button("+");
        this.plusButton.setOnAction(event -> {
            int newCount = this.countProperty.get() + 1;
            if (this.goldModifier.computeWithAlternativeDelta(-unitPrice * newCount) >= 0
                    && this.amountValidator.apply(newCount)) {
                this.countProperty.set(newCount);
                this.entry.setValue(newCount);
                this.goldModifier.setDelta(-unitPrice * newCount);
                this.notifyListeners(newCount);
                this.updateButtonsState();
            }
        });

        this.setSpacing(5);
        this.setAlignment(Pos.CENTER); // Centrer tous les éléments dans le HBox
        this.setPadding(new Insets(5));

        // MODIFICATION ICI: Ne pas afficher le nom à côté des boutons et centrer les
        // éléments
        // this.getChildren().addAll(new Label(getDisplayName(entry.getKey())),
        // this.minusButton, this.countLabel, this.plusButton);
        this.getChildren().addAll(this.minusButton, this.countLabel, this.plusButton);

        setTooltip();
        this.updateButtonsState();
    }

    public PurchasableItemAmountSelector(EnumIntMap.Entry<T> entry, Counter goldCounter) {
        this(entry, goldCounter, (amount) -> true);
    }

    private String getDisplayName(Enum<?> e) {
        try {
            var method = e.getClass().getMethod("getDisplayName");
            return (String) method.invoke(e);
        } catch (Exception ex) {
            // Fallback : formatte le nom enum
            String name = e.name().replace('_', ' ').toLowerCase();
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }

    public void addListener(Consumer<Integer> listener) {
        this.listeners.add(listener);
    }

    protected void notifyListeners(int newCount) {
        for (Consumer<Integer> listener : this.listeners) {
            listener.accept(newCount);
        }
    }

    public Map<String, String> getDetails() {
        Map<String, String> details = new HashMap<>();
        try {
            var priceMethod = entry.getKey().getClass().getMethod("getPrice");
            int price = (int) priceMethod.invoke(entry.getKey());
            details.put("Prix", price + " or");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    protected void setTooltip() {
        Map<String, String> details = getDetails();
        if (!details.isEmpty()) {
            StringBuilder tooltipText = new StringBuilder();
            for (Map.Entry<String, String> detail : details.entrySet()) {
                tooltipText.append(detail.getKey()).append(" : ").append(detail.getValue()).append("\n");
            }
            Tooltip tooltip = new Tooltip(tooltipText.toString().trim());
            Tooltip.install(this, tooltip);
        }
    }

    public void updateButtonsState() {
        this.minusButton.setDisable(this.countProperty.get() <= 0);

        // Calcul du prix unitaire
        int price = 0;
        try {
            var method = entry.getKey().getClass().getMethod("getPrice");
            price = (int) method.invoke(entry.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int unitPrice = price;
        int newCount = this.countProperty.get() + 1;
        this.plusButton.setDisable(
                this.goldModifier.computeWithAlternativeDelta(-unitPrice * newCount) < 0
                        || !this.amountValidator.apply(newCount));
    }
}