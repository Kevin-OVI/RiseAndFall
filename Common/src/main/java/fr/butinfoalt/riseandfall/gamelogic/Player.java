package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderType;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.ObjectIntMap.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Représente un joueur dans le jeu.
 * Chaque joueur a une race, une quantité d'or, une quantité d'intelligence, des bâtiments et des unités.
 * Il peut également donner des ordres pour créer des bâtiments ou des unités.
 */
public abstract class Player implements Identifiable, ISerializable {
    /**
     * Identifiant du joueur dans la base de données.
     */
    private final int id;

    /**
     * Association entre les types de bâtiments et le nombre de bâtiments de chaque type.
     */
    protected final ObjectIntMap<BuildingType> buildingMap;
    /**
     * Association entre les types d'unités et le nombre d'unités de chaque type.
     */
    protected final ObjectIntMap<UnitType> unitMap;
    /**
     * Liste des ordres à exécuter au prochain tour pour le joueur.
     */
    protected final ArrayList<BaseOrder> pendingOrders = new ArrayList<>();
    /**
     * Race du joueur
     */
    private final Race race;
    /**
     * Quantité d'or que possède le joueur.
     * Initialisé à 50 pièces d'or au début de la partie.
     */
    protected int goldAmount = 50;
    /**
     * Quantité d'intelligence que possède le joueur.
     */
    private int intelligence = 0;

    /**
     * Constructeur de la classe Player.
     *
     * @param race La race choisie par le joueur.
     */
    public Player(int id, Race race) {
        this.id = id;
        this.race = race;
        this.buildingMap = new ObjectIntMap<>(
                ServerData.getInstance().buildingTypes().stream()
                        .filter(buildingType -> buildingType.getAccessibleByRace() == null || buildingType.getAccessibleByRace() == this.race)
                        .collect(Collectors.toList())
        );
        this.unitMap = new ObjectIntMap<>(
                ServerData.getInstance().unitTypes().stream()
                        .filter(unitType -> unitType.getAccessibleByRace() == null || unitType.getAccessibleByRace() == this.race)
                        .collect(Collectors.toList())
        );

        for (Entry<BuildingType> entry : this.buildingMap) {
            entry.setValue(entry.getKey().getInitialAmount());
        }
    }

    /**
     * Méthode pour obtenir l'identifiant du joueur.
     *
     * @return L'identifiant du joueur.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Méthode pour obtenir la race du joueur.
     *
     * @return La race du joueur.
     */
    public Race getRace() {
        return this.race;
    }

    /**
     * Méthode pour obtenir la quantité d'or actuelle du joueur.
     *
     * @return La quantité d'or actuelle du joueur.
     */
    public int getGoldAmount() {
        return this.goldAmount;
    }

    /**
     * Méthode pour définir la quantité d'or du joueur.
     *
     * @param goldAmount La nouvelle quantité d'or à définir.
     */
    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    /**
     * Méthode pour obtenir la quantité d'intelligence actuelle du joueur.
     *
     * @return La quantité d'intelligence actuelle du joueur.
     */
    public int getIntelligence() {
        return this.intelligence;
    }

    /**
     * Méthode pour définir la quantité d'intelligence du joueur.
     *
     * @param intelligence La nouvelle quantité d'intelligence à définir.
     */
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void addIntelligence(int valeur) {
        this.intelligence += valeur;
    }

    /**
     * Méthode pour obtenir le nombre maximum d'unités que le joueur peut avoir.
     * On additionne le nombre maximum d'unités de chaque type d'unité multiplié par le nombre d'unités de ce type.
     *
     * @return Le nombre maximum d'unités que le joueur peut avoir.
     */
    public int getAllowedUnitCount() {
        int allowedCount = 0;
        for (Entry<BuildingType> entry : this.buildingMap) {
            allowedCount += entry.getValue() * entry.getKey().getMaxUnits();
        }

        return allowedCount;
    }

    /**
     * Méthode pour ajouter une certaine quantité d'or au joueur.
     *
     * @param goldAmount La quantité d'or à ajouter.
     */
    public void addGoldAmount(int goldAmount) {
        this.goldAmount += goldAmount;
    }

    /**
     * Méthode pour retirer une certaine quantité d'or au joueur.
     *
     * @param goldAmount La quantité d'or à retirer.
     */
    public void removeGoldAmount(int goldAmount) {
        this.goldAmount -= goldAmount;
    }

    /**
     * Méthode pour obtenir la liste des bâtiments du joueur.
     *
     * @return La liste des bâtiments du joueur.
     */
    public ObjectIntMap<BuildingType> getBuildingMap() {
        return this.buildingMap;
    }

    /**
     * Méthode pour obtenir la liste des unités du joueur.
     *
     * @return La liste des unités du joueur.
     */
    public ObjectIntMap<UnitType> getUnitMap() {
        return this.unitMap;
    }

    /**
     * Méthode pour obtenir la liste des ordres en attente.
     *
     * @return La liste des ordres en attente.
     */
    public ArrayList<BaseOrder> getPendingOrders() {
        return this.pendingOrders;
    }

    /**
     * Méthode pour ajouter un ordre à la liste des ordres en attente.
     * Supprime tous les ordres en attente avant d'ajouter les nouveaux ordres.
     *
     * @param orders Liste des ordres à ajouter.
     */
    public void updatePendingOrders(List<BaseOrder> orders) {
        this.pendingOrders.clear();
        this.pendingOrders.addAll(orders);
    }

    @Override
    public String toString() {
        return "Player{id=%d, buildingMap=%s, unitMap=%s, pendingOrders=%s, race=%s, goldAmount=%d, intelligence=%d}".formatted(this.id, this.buildingMap, this.unitMap, this.pendingOrders, this.race, this.goldAmount, this.intelligence);
    }

    /**
     * Méthode pour désérialiser une liste d'ordres à partir d'un flux de données.
     * On lit d'abord le nombre d'ordres, puis on lit chaque ordre en fonction de son type.
     *
     * @param readHelper L'outil de lecture pour désérialiser les ordres.
     * @return La liste des ordres désérialisés.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public static ArrayList<BaseOrder> deserializeOrders(ReadHelper readHelper) throws IOException {
        int orderCount = readHelper.readInt();
        ArrayList<BaseOrder> orders = new ArrayList<>(orderCount);
        for (int i = 0; i < orderCount; i++) {
            OrderType orderType = OrderType.values()[readHelper.readInt()];
            orders.add(orderType.getDeserializer().deserialize(readHelper));
        }
        return orders;
    }

    /**
     * Méthode pour sérialiser une liste d'ordres dans un flux de données.
     * On écrit d'abord le nombre d'ordres, puis on écrit chaque ordre en fonction de son type.
     *
     * @param orders      La liste des ordres à sérialiser.
     * @param writeHelper L'outil d'écriture pour sérialiser les ordres.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    public static void serializeOrders(ArrayList<BaseOrder> orders, WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(orders.size());
        for (BaseOrder order : orders) {
            writeHelper.writeInt(OrderType.getByClass(order.getClass()).ordinal());
            order.toBytes(writeHelper);
        }
    }

    /**
     * Méthode pour sérialiser les données modifiables du joueur dans un flux de données.
     * On écrit d'abord la quantité d'or, puis la quantité d'intelligence,
     * ensuite on écrit chaque bâtiment et son nombre, puis chaque unité et son nombre.
     * Enfin, on sérialise les ordres en attente.
     *
     * @param writeHelper L'outil d'écriture pour sérialiser les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    public void serializeModifiableData(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.goldAmount);
        writeHelper.writeInt(this.intelligence);
        for (Entry<BuildingType> entry : this.buildingMap) {
            writeHelper.writeInt(entry.getKey().getId());
            writeHelper.writeInt(entry.getValue());
        }
        for (Entry<UnitType> entry : this.unitMap) {
            writeHelper.writeInt(entry.getKey().getId());
            writeHelper.writeInt(entry.getValue());
        }
        serializeOrders(this.pendingOrders, writeHelper);
    }

    /**
     * Méthode pour sérialiser les données du joueur dans un flux de données.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeInt(this.race.getId());
        this.serializeModifiableData(writeHelper);
    }
}
