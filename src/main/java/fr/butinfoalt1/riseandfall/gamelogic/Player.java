package fr.butinfoalt1.riseandfall.gamelogic;

import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap.Entry;
import fr.butinfoalt1.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt1.riseandfall.gamelogic.order.BaseOrder;

import java.util.ArrayList;

/**
 * Représente un joueur dans le jeu.
 * Chaque joueur a une quantité d'or, des bâtiments et des unités.
 * Il peut également donner des ordres pour créer des bâtiments ou des unités.
 */
public class Player {
    /**
     * Instance unique du joueur (singleton).
     * Utilisé pour représenter un joueur unique dans le jeu.
     * Ce champ sera retiré lors de l'implémentation du mode multijoueur.
     */
    public static final Player SINGLE_PLAYER = new Player();

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
    private int intelligence = 50;

    public Player() {
        for (Entry<BuildingType> entry : this.buildingMap) {
            entry.setValue(entry.getKey().getInitialAmount());
        }
    }

    /**
     * Méthode pour obtenir la quantité d'or actuelle du joueur.
     *
     * @return La quantité d'or actuelle du joueur.
     */
    public int getGoldAmount() {
        return goldAmount;
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
        return intelligence;
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
     * Méthode pour ajouter un bâtiment au joueur.
     *
     * @param type  Le type de bâtiment à ajouter.
     * @param count Le nombre de bâtiments de ce type à ajouter.
     */
    public void addBuildings(BuildingType type, int count) {
        buildingMap.increment(type, count);
    }

    /**
     * Méthode pour retirer un bâtiment du joueur.
     *
     * @param type  Le type de bâtiment à retirer.
     * @param count Le nombre de bâtiments de ce type à retirer.
     */
    public void removeBuildings(BuildingType type, int count) {
        buildingMap.decrement(type, count);
    }

    /**
     * Méthode pour obtenir le nombre de bâtiments d'un type donné.
     *
     * @param type Le type de bâtiment à obtenir.
     * @return Le nombre de bâtiments de ce type.
     */
    public int getBuildings(BuildingType type) {
        return buildingMap.get(type);
    }

    /**
     * Méthode pour obtenir le nombre total de bâtiments du joueur.
     * On additionne le nombre de bâtiments de chaque type.
     *
     * @return Le nombre total de bâtiments du joueur.
     */
    public int getBuildingsCount() {
        int count = 0;
        for (Entry<BuildingType> entry : this.buildingMap) {
            count += entry.getValue();
        }
        return count;
    }

    /**
     * Méthode pour ajouter une unité au joueur.
     *
     * @param type  Le type d'unité à ajouter.
     * @param count Le nombre d'unités de ce type à ajouter.
     */
    public void addUnits(UnitType type, int count) {
        unitMap.increment(type, count);
    }

    /**
     * Méthode pour retirer une unité du joueur.
     *
     * @param type  Le type d'unité à retirer.
     * @param count Le nombre d'unités de ce type à retirer.
     */
    public void removeUnits(UnitType type, int count) {
        unitMap.decrement(type, count);
    }

    /**
     * Méthode pour obtenir le nombre d'unités d'un type donné.
     *
     * @param type Le type d'unité à obtenir.
     * @return Le nombre d'unités de ce type.
     */
    public int getUnits(UnitType type) {
        return unitMap.get(type);
    }

    /**
     * Méthode pour obtenir le nombre total d'unités du joueur.
     * On additionne le nombre d'unités de chaque type.
     *
     * @return Le nombre total d'unités du joueur.
     */
    public int getUnitsCount() {
        int count = 0;
        for (Entry<UnitType> entry : this.unitMap) {
            count += entry.getValue();
        }
        return count;
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
}
