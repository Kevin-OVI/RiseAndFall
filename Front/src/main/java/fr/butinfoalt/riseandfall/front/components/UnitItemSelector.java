package fr.butinfoalt.riseandfall.front.components;

import fr.butinfoalt.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;

import java.util.Map;
import java.util.function.Function;

public class UnitItemSelector extends PurchasableItemAmountSelector<UnitType> {
    public UnitItemSelector(EnumIntMap.Entry<UnitType> entry, Counter goldCounter, Function<Integer, Boolean> amountValidator) {
        super(entry, goldCounter, amountValidator);
    }

    public UnitItemSelector(EnumIntMap.Entry<UnitType> entry, Counter goldCounter) {
        super(entry, goldCounter);
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> details = super.getDetails();
        // Rien à ajouter pour le moment
        return details;
    }
}
