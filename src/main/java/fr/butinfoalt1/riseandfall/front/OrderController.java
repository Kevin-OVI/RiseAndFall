package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.gamelogic.Player;
import fr.butinfoalt1.riseandfall.gamelogic.map.BuildingType;
import fr.butinfoalt1.riseandfall.gamelogic.map.UnitType;
import fr.butinfoalt1.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateBuilding;
import fr.butinfoalt1.riseandfall.gamelogic.order.OrderCreateUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

/**
 * Contrôleur pour la vue de gestion des ordres.
 */
public class OrderController {
    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    private Label goldField;

    // TODO : Faire des champs pour chaque type de bâtiment et d'unité, de préférence de manière dynamique en utilisant BuildingType.getDisplayName() et UnitType.getDisplayName()
    /**
     * Champ pour le composant de la quantité d'unités (pour l'instant Guerriers).
     */
    @FXML
    private Label troopField;

    /**
     * Champ pour le composant de la quantité de bâtiments (pour l'instant Huttes).
     */
    @FXML
    private Label hutField;

    /**
     * Champ pour le composant de la quantité totale d'unités.
     */
    @FXML
    private Label totalUnitsField;

    /**
     * Champ pour le composant de la quantité totale de bâtiments.
     */
    @FXML
    private Label totalBuildingsField;

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour augmenter la quantité d'unités.
     */
    @FXML
    private void increaseTroop() {
        updateLabel(troopField, 1);
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour diminuer la quantité d'unités.
     */
    @FXML
    private void decreaseTroop() {
        updateLabel(troopField, -1);
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour augmenter la quantité de bâtiments.
     */
    @FXML
    private void increaseHut() {
        updateLabel(hutField, 1);
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour diminuer la quantité de bâtiments.
     */
    @FXML
    private void decreaseHut() {
        updateLabel(hutField, -1);
    }

    /**
     * Méthode pour mettre à jour la quantité d'unités ou de bâtiments.
     *
     * @param label Le label à mettre à jour.
     * @param delta La valeur à ajouter ou soustraire.
     */
    private void updateLabel(Label label, int delta) {
        int value = Integer.parseInt(label.getText());
        int gold = Integer.parseInt(goldField.getText());
        if (label == troopField) {
            gold -= delta * UnitType.WARRIOR.getPrice();
        } else if (label == hutField) {
            gold -= delta * BuildingType.HUT.getPrice();
        }
        // TODO : Vérifier par rapport à la quantité d'or et pour les unités par rapport à la capacité d'accueil des bâtiments
        if (gold < 0 || value + delta < 0 || value + delta > 5) {
            return;
        }
        int newValue = value + delta;
        label.setText(String.valueOf(newValue));
        goldField.setText(String.valueOf(gold));
    }

    /**
     * Méthode pour rafraîchir les informations affichées dans la vue.
     */
    public void refresh() {
        ArrayList<BaseOrder> orders = Player.SINGLE_PLAYER.getPendingOrders();
        int gold = Player.SINGLE_PLAYER.getGoldAmount();
        int nbTroops = 0;
        int nbHuts = 0;
        for (BaseOrder order : orders) {
            gold -= order.getPrice();
            if (order instanceof OrderCreateBuilding) {
                nbHuts += ((OrderCreateBuilding) order).getCount();
            } else if (order instanceof OrderCreateUnit) {
                nbTroops += ((OrderCreateUnit) order).getCount();
            }
        }
        goldField.setText(String.valueOf(gold));
        troopField.setText(String.valueOf(nbTroops));
        hutField.setText(String.valueOf(nbHuts));
        int totalUnits = Player.SINGLE_PLAYER.getUnits(UnitType.WARRIOR);
        int totalBuildings = Player.SINGLE_PLAYER.getBuildings(BuildingType.HUT);

        totalUnitsField.setText("Unités totales : " + totalUnits);
        totalBuildingsField.setText("Bâtiments totaux : " + totalBuildings);
    }

    /**
     * Méthode appelée par JavaFX pour revenir à la vue précédente.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToPreviousView();
    }

    /**
     * Méthode appelée par JavaFX pour gérer l'action de sauvegarde.
     * Elle enregistre les ordres en attente du joueur et revient à la vue précédente.
     */
    @FXML
    private void handleSave() {
        int nbTroops = Integer.parseInt(troopField.getText());
        int nbHuts = Integer.parseInt(hutField.getText());

        Player.SINGLE_PLAYER.clearPendingOrders();
        Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateUnit(UnitType.WARRIOR, nbTroops));
        Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateBuilding(BuildingType.HUT, nbHuts));

        this.switchBack();
    }
}
