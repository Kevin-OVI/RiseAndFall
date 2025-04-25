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
        int goldProduction = this.entry.getKey().getGoldProduction();
        if (goldProduction > 0) {
            details.put("Production d'or", String.valueOf(goldProduction));
        }
        int intelligenceProduction = this.entry.getKey().getIntelligenceProduction();
        if (intelligenceProduction > 0) {
            details.put("Production d'intelligence", String.valueOf(intelligenceProduction));
        }
        int maxUnits = this.entry.getKey().getMaxUnits();
        if (maxUnits > 0) {
            details.put("Capacité d'hébergement", String.valueOf(maxUnits));
        }
        return details;
    }
}
