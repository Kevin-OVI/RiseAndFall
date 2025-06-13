package fr.butinfoalt.riseandfall.front.game.orders.table;

import javafx.beans.property.SimpleStringProperty;

public class FloatOptimisedDisplayProperty extends SimpleStringProperty {

    public FloatOptimisedDisplayProperty(float initialValue) {
        super(convertToString(initialValue));
    }

    private static String convertToString(float value) {
        int intValue = (int) value;
        if (value == intValue) {
            return String.valueOf(intValue);
        } else {
            return String.valueOf(value);
        }
    }
}
