package fr.butinfoalt.riseandfall.front.game.orders.table;

import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.beans.property.SimpleStringProperty;

public class FloatOptimisedDisplayProperty extends SimpleStringProperty {
    public FloatOptimisedDisplayProperty(float initialValue) {
        super(UIUtils.displayOptimisedFloat(initialValue));
    }
}
