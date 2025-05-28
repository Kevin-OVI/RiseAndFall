package fr.butinfoalt.riseandfall.network.packets.data;

import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderCreateUnit;
import fr.butinfoalt.riseandfall.network.common.IDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumération représentant les différents types d'ordres.
 * Elle est utilisée pour transmettre des ordres sur le réseau.
 * Chaque type d'ordre est associé à une classe d'ordre et à un désérialiseur.
 */
public enum OrderType {
    CREATE_BUILDING(OrderCreateBuilding.class, readHelper -> new OrderCreateBuilding(Identifiable.getById(ServerData.getBuildingTypes(), readHelper.readInt()), readHelper.readInt())),
    CREATE_UNIT(OrderCreateUnit.class, readHelper -> new OrderCreateUnit(Identifiable.getById(ServerData.getUnitTypes(), readHelper.readInt()), readHelper.readInt()));

    /**
     * Map statique pour associer les classes d'ordres à leurs types.
     */
    private static final Map<Class<? extends BaseOrder>, OrderType> byClass = new HashMap<>();

    /**
     * Classe d'ordre associée à ce type d'ordre.
     */
    private final Class<? extends BaseOrder> orderClass;

    /**
     * Désérialiseur associé à ce type d'ordre.
     */
    private final IDeserializer<? extends BaseOrder> deserializer;

    /**
     * Constructeur de l'énumération OrderType.
     *
     * @param orderClass   Classe d'ordre associée à ce type d'ordre.
     * @param deserializer Désérialiseur associé à ce type d'ordre.
     * @param <T>          Type d'ordre.
     */
    <T extends BaseOrder> OrderType(Class<T> orderClass, IDeserializer<T> deserializer) {
        this.orderClass = orderClass;
        this.deserializer = deserializer;
    }

    /**
     * Obtient le désérialiseur associé à ce type d'ordre.
     *
     * @return Le désérialiseur associé à ce type d'ordre.
     */
    public IDeserializer<? extends BaseOrder> getDeserializer() {
        return this.deserializer;
    }

    /**
     * Obtient le type d'ordre associé à une classe d'ordre donnée.
     *
     * @param orderClass Classe d'ordre à rechercher.
     * @return Le type d'ordre associé à la classe d'ordre, ou null si aucun type n'est trouvé.
     */
    public static OrderType getByClass(Class<? extends BaseOrder> orderClass) {
        return byClass.get(orderClass);
    }

    static {
        // Remplissage de la map statique avec les types d'ordres et leurs classes associées
        for (OrderType orderType : OrderType.values()) {
            byClass.put(orderType.orderClass, orderType);
        }
    }
}
