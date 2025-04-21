package fr.butinfoalt1.riseandfall.gamelogic.counter;

public class Modifier {
    private final Counter counter;
    private int delta;

    Modifier(Counter counter, int delta) {
        this.counter = counter;
        this.delta = delta;
    }

    public Counter getCounter() {
        return this.counter;
    }

    public int getDelta() {
        return this.delta;
    }

    public void setDelta(int delta) {
        if (!this.counter.hasModifier(this)) {
            throw new IllegalStateException("This modifier got removed from the counter.");
        }
        this.counter.updateCurrentValue(this.delta, delta);
        this.delta = delta;
    }

    public void remove() {
        this.counter.removeModifier(this);
    }

    public int computeWithAlternativeDelta(int alternativeDelta) {
        return this.counter.getCurrentValue() - this.delta + alternativeDelta;
    }
}
