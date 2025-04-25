package fr.butinfoalt.riseandfall.front.components;

import fr.butinfoalt.riseandfall.gamelogic.counter.Counter;
import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap;

import java.util.Map;
import java.util.function.Function;

public class BuildingAmountSelector extends PurchasableItemAmountSelector<BuildingType> {
    public BuildingAmountSelector(EnumIntMap.Entry<BuildingType> entry, Counter goldCounter, Function<Integer, Boolean> amountValidator) {
        super(entry, goldCounter, amountValidator);
    }

    public BuildingAmountSelector(EnumIntMap.Entry<BuildingType> entry, Counter goldCounter) {
        super(entry, goldCounter);
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> details = super.getDetails();
        details.put("Production d'or", String.valueOf(this.entry.getKey().getGoldProduction()));
        details.put("Production d'intelligence", String.valueOf(this.entry.getKey().getIntelligenceProduction()));
        details.put("Capacité d'hébergement", String.valueOf(this.entry.getKey().getMaxUnits()));
        return details;
    }
}
