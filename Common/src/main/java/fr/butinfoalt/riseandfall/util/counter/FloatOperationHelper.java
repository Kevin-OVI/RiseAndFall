package fr.butinfoalt.riseandfall.util.counter;

public class FloatOperationHelper extends OperationHelper<Float> {
    public FloatOperationHelper(float value) {
        super(value);
    }

    @Override
    public OperationHelper<Float> add(Float other) {
        return new FloatOperationHelper(value + other);
    }

    @Override
    public OperationHelper<Float> subtract(Float other) {
        return new FloatOperationHelper(value - other);
    }

    @Override
    public Float getDefaultModifierValue() {
        return 0f;
    }
}
