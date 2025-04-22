package fr.butinfoalt1.riseandfall.gamelogic;

import fr.butinfoalt1.riseandfall.front.MainController;
import fr.butinfoalt1.riseandfall.front.View;
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
        return buildingMap;
    }

    /**
     * Méthode pour obtenir la liste des unités du joueur.
     *
     * @return La liste des unités du joueur.
     */
    public EnumIntMap<UnitType> getUnitMap() {
        return unitMap;
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
        }

        for (BaseOrder order : this.pendingOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
                this.removeGoldAmount(order.getPrice());
            }
        }
        this.pendingOrders.clear();
        MainController mainController = View.MAIN.getController();
        mainController.updateFields();
    }
}
