package fr.butinfoalt1.riseandfall.gamelogic;

import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.map.EnumIntMap;
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
     * Méthode pour obtenir la quantité d'or restante après avoir soustrait le prix des ordres en attente.
     *
     * @return La quantité d'or restante après soustraction des prix des ordres.
     */
    public int getRemainingGoldAmount() {
        int gold = this.goldAmount;
        for (BaseOrder order : this.pendingOrders) {
            gold -= order.getPrice();
        }
        return gold;
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
        buildingMap.add(type, count);
    }

    /**
     * Méthode pour retirer un bâtiment du joueur.
     *
     * @param type  Le type de bâtiment à retirer.
     * @param count Le nombre de bâtiments de ce type à retirer.
     */
    public void removeBuildings(BuildingType type, int count) {
        buildingMap.remove(type, count);
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
     * Méthode pour ajouter une unité au joueur.
     *
     * @param type  Le type d'unité à ajouter.
     * @param count Le nombre d'unités de ce type à ajouter.
     */
    public void addUnits(UnitType type, int count) {
        unitMap.add(type, count);
    }

    /**
     * Méthode pour retirer une unité du joueur.
     *
     * @param type  Le type d'unité à retirer.
     * @param count Le nombre d'unités de ce type à retirer.
     */
    public void removeUnits(UnitType type, int count) {
        unitMap.remove(type, count);
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
     * Méthode pour ajouter un ordre à la liste des ordres en attente.
     *
     * @param order L'ordre à ajouter.
     */
    public void addOrder(BaseOrder order) {
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
    public void clearOrders() {
        this.pendingOrders.clear();
    }

    /**
     * Méthode pour exécuter les ordres en attente.
     * On commence par ajouter l'or produit par les bâtiments (TODO).
     * Ensuite, on exécute chaque ordre en attente si le joueur a suffisamment d'or.
     * Enfin, on vide la liste des ordres en attente.
     */
    public void executeOrders() {
        // TODO : Add goldAmount according to the buildings

        for (BaseOrder order : this.pendingOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
                this.removeGoldAmount(order.getPrice());
            }
        }
        this.pendingOrders.clear();
    }
}
