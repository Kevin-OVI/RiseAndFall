package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;

import java.io.IOException;

/**
 * Représente un ordre de création d'un bâtiment.
 * Cet ordre est exécuté sur le joueur pour créer un certain nombre de bâtiments d'un type donné.
 */
public class OrderCreateBuilding implements BaseOrder {
    /**
     * Type de bâtiment à créer.
     */
    private final BuildingType buildingType;
    /**
     * Nombre de bâtiments de ce type à créer.
     */
    private final int count;

    /**
     * Constructeur de l'ordre de création de bâtiment.
     *
     * @param buildingType Le type de bâtiment à créer.
     * @param count        Le nombre de bâtiments de ce type à créer.
     */
    public OrderCreateBuilding(BuildingType buildingType, int count) {
        this.buildingType = buildingType;
        this.count = count;
    }

    /**
     * Constructeur de l'ordre de création de bâtiment à partir d'un flux de données.
     * On lit d'abord l'identifiant du type de bâtiment, puis le nombre de bâtiments à créer.
     *
     * @param readHelper     L'outil de lecture pour désérialiser les données.
     * @param ignoredContext Le contexte de désérialisation pour cet ordre, ignoré ici.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public OrderCreateBuilding(ReadHelper readHelper, OrderDeserializationContext ignoredContext) throws IOException {
        this(Identifiable.getById(ServerData.getBuildingTypes(), readHelper.readInt()), readHelper.readInt());
    }

    /**
     * Exécute l'ordre de création de bâtiment sur le joueur donné.
     *
     * @param player Le joueur sur lequel exécuter l'ordre.
     */
    @Override
    public void execute(Player player) {
        player.getBuildingMap().increment(this.buildingType, this.count);
    }

    /**
     * Obtient le prix de l'ordre en or.
     * On calcule le prix total en multipliant le prix du bâtiment à l'unité par le nombre de bâtiments à créer.
     *
     * @return Le prix de l'ordre en or.
     */
    @Override
    public float getPrice() {
        return this.buildingType.getPrice() * this.count;
    }

    /**
     * Obtient le type de bâtiment à créer.
     *
     * @return Le type de bâtiment à créer.
     */
    public BuildingType getBuildingType() {
        return this.buildingType;
    }

    /**
     * Obtient le nombre de bâtiments à créer.
     *
     * @return Le nombre de bâtiments à créer.
     */
    public int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return "OrderCreateBuilding{buildingType=%s, count=%d}".formatted(this.buildingType, this.count);
    }

    /**
     * Sérialise l'ordre de création de bâtiment dans un flux de données.
     * On écrit d'abord l'identifiant du type de bâtiment, puis le nombre de bâtiments à créer.
     *
     * @param writeHelper L'outil d'écriture pour sérialiser les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.buildingType.getId());
        writeHelper.writeInt(this.count);
    }
}
