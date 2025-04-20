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

    @FXML
    private Label goldField;

    @FXML
    private Label troopField;

    @FXML
    private Label hutField;

    @FXML
    private void increaseTroop() {
        updateLabel(troopField, 1);
    }

    @FXML
    private void decreaseTroop() {
        updateLabel(troopField, -1);
    }

    @FXML
    private void increaseHut() {
        updateLabel(hutField, 1);
    }

    @FXML
    private void decreaseHut() {
        updateLabel(hutField, -1);
    }

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
    }

    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToPreviousView();
    }

    @FXML
    private void handleSave() {
        int nbTroops = Integer.parseInt(troopField.getText());
        int nbHuts = Integer.parseInt(hutField.getText());

        Player.SINGLE_PLAYER.clearPendingOrders();
        Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateUnit(UnitType.WARRIOR, nbTroops));
        Player.SINGLE_PLAYER.addPendingOrder(new OrderCreateBuilding(BuildingType.HUT, nbHuts));
    }

    @FXML
    private void handleEndTurn() {
        handleSave();
        Player.SINGLE_PLAYER.executeOrders();
        switchBack();
    }
}
