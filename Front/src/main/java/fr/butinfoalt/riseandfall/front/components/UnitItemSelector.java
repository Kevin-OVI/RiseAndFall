package fr.butinfoalt.riseandfall.front.components;

import fr.butinfoalt.riseandfall.util.counter.Counter;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;

import java.util.Map;
import java.util.function.Function;

public class UnitItemSelector extends PurchasableItemAmountSelector<UnitType> {
    public UnitItemSelector(ObjectIntMap.Entry<UnitType> entry, Counter goldCounter, Function<Integer, Boolean> amountValidator) {
        super(entry, goldCounter, amountValidator);
    }

    public UnitItemSelector(ObjectIntMap.Entry<UnitType> entry, Counter goldCounter) {
        super(entry, goldCounter);
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> details = super.getDetails();
        // Rien à ajouter pour le moment
        return details;
    }
}
