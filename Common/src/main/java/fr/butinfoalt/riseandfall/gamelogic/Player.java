package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt.riseandfall.gamelogic.map.EnumIntMap.Entry;
import fr.butinfoalt.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;

import java.util.ArrayList;


/**
 * Représente un joueur dans le jeu.
 * Chaque joueur a une race, une quantité d'or, une quantité d'intelligence, des bâtiments et des unités.
 * Il peut également donner des ordres pour créer des bâtiments ou des unités.
 */
public class Player {
    /**
     * Association entre les types de bâtiments et le nombre de bâtiments de chaque type.
     */
    private final EnumIntMap<BuildingType> buildingMap = new EnumIntMap<>(BuildingType.class);
    /**
     * Association entre les types d'unités et le nombre d'unités de chaque type.
     */
    private final EnumIntMap<UnitType> unitMap = new EnumIntMap<>(UnitType.class);
    /**
     * Liste des ordres à exécuter au prochain tour pour le joueur.
     */
    private final ArrayList<BaseOrder> pendingOrders = new ArrayList<>();
    /**
     * Quantité d'or que possède le joueur.
     * Initialisé à 50 pièces d'or au début de la partie.
     */
    private int goldAmount = 50;

    /**
     * Quantité d'intelligence que possède le joueur.
     */
    private int intelligence = 50;

    /**
     * Race du joueur
     */
    private final Race race;

    /**
     * Constructeur de la classe Player.
     *
     * @param race La race choisie par le joueur.
     */
    public Player(Race race) {
        this.race = race;

        for (Entry<BuildingType> entry : this.buildingMap) {
            entry.setValue(entry.getKey().getInitialAmount());
        }
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
    public EnumIntMap<BuildingType> getBuildingMap() {
        return this.buildingMap;
    }

    /**
     * Méthode pour obtenir la liste des unités du joueur.
     *
     * @return La liste des unités du joueur.
     */
    public EnumIntMap<UnitType> getUnitMap() {
        return this.unitMap;
    }

    /**
     * Méthode pour ajouter un ordre à la liste des ordres en attente.
     *
     * @param order L'ordre à ajouter.
     */
    public void addPendingOrder(BaseOrder order) {
        this.pendingOrders.add(order);
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
     * Méthode pour supprimer les ordres en attente.
     */
    public void clearPendingOrders() {
        this.pendingOrders.clear();
    }

    /**
     * Méthode pour exécuter les ordres en attente.
     * On commence par ajouter l'or produit par les bâtiments.
     * Ensuite, on exécute chaque ordre en attente si le joueur a suffisamment d'or.
     * Enfin, on vide la liste des ordres en attente.
     */
    public void executeOrders() {
        for (Entry<BuildingType> entry : this.buildingMap) {
            this.addGoldAmount(entry.getValue() * entry.getKey().getGoldProduction());
            this.addIntelligence(entry.getValue() * entry.getKey().getIntelligenceProduction());
        }

        for (BaseOrder order : this.pendingOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
                this.removeGoldAmount(order.getPrice());
            }
        }
        this.pendingOrders.clear();
    }

    @Override
    public String toString() {
        return "Player{buildingMap=%s, unitMap=%s, pendingOrders=%s, goldAmount=%d, intelligence=%d}".formatted(this.buildingMap, this.unitMap, this.pendingOrders, this.goldAmount, this.intelligence);
    }
}