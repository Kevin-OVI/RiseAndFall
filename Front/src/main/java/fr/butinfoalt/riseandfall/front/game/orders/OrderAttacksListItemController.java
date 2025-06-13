package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class OrderAttacksListItemController {
    private OrderAttacksListController parentController;
    private AttackPlayerOrderData attack;

    @FXML
    public Label targetPlayerName;

    public void onEdit(ActionEvent actionEvent) {
        this.parentController.createOrEditAttack(this.attack);
    }

    public void onDelete(ActionEvent actionEvent) {
        this.parentController.removeAttack(this.attack);
    }

    void init(OrderAttacksListController parentController, AttackPlayerOrderData attack) {
        this.parentController = parentController;
        this.attack = attack;
        this.targetPlayerName.setText("Attaque contre " + ((OtherClientPlayer) attack.getTargetPlayer()).getName());
    }
}
