package fr.butinfoalt.riseandfall.util.counter;

public class IntOperationHelper extends OperationHelper<Integer> {
    public IntOperationHelper(int value) {
        super(value);
    }

    @Override
    public OperationHelper<Integer> add(Integer other) {
        return new IntOperationHelper(value + other);
    }

    @Override
    public OperationHelper<Integer> subtract(Integer other) {
        return new IntOperationHelper(value - other);
    }

    @Override
    public Integer getDefaultModifierValue() {
        return 0;
    }
}
