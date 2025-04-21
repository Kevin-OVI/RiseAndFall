package fr.butinfoalt1.riseandfall.gamelogic.counter;

import java.util.HashSet;
import java.util.function.Consumer;

public class Counter {
    private final int initialValue;
    private final HashSet<Modifier> modifiers = new HashSet<>();
    private final HashSet<Consumer<Integer>> changeListeners = new HashSet<>();
    private boolean dispatchChanges = false;
    private int currentValue;

    public Counter(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public Modifier addModifier(int delta) {
        Modifier modifier = new Modifier(this, delta);
        this.modifiers.add(modifier);
        this.currentValue += delta;
        this.callChangeListeners();
        return modifier;
    }

    public void removeModifier(Modifier modifier) {
        if (this.modifiers.remove(modifier)) {
            this.currentValue -= modifier.getDelta();
            this.callChangeListeners();
        }
    }

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    public void addChangeListener(Consumer<Integer> listener) {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener(Consumer<Integer> listener) {
        this.changeListeners.remove(listener);
    }

    public boolean isDispatchChanges() {
        return this.dispatchChanges;
    }

    public void setDispatchChanges(boolean dispatchChanges) {
        this.dispatchChanges = dispatchChanges;
        if (dispatchChanges) {
            this.callChangeListeners();
        }
    }

    public int getInitialValue() {
        return this.initialValue;
    }

    public int getCurrentValue() {
        return this.currentValue;
    }

    void updateCurrentValue(int previousDelta, int newDelta) {
        this.currentValue += newDelta - previousDelta;
        this.callChangeListeners();
    }

    private void callChangeListeners() {
        if (this.dispatchChanges) {
            for (Consumer<Integer> listener : this.changeListeners) {
                listener.accept(this.currentValue);
            }
        }
    }
}
