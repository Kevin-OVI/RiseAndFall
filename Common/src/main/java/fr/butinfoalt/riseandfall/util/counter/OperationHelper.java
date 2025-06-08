package fr.butinfoalt.riseandfall.util.counter;

public abstract class OperationHelper<T> {
    protected final T value;

    public OperationHelper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public abstract OperationHelper<T> add(T other);

    public abstract OperationHelper<T> subtract(T other);

    public abstract T getDefaultModifierValue();
}
