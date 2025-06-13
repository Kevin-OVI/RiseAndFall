package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.components.TitleLabel;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateOrders;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAttacksListController implements ViewController {
    private final HashMap<AttackPlayerOrderData, Node> attackNodes = new HashMap<>();
    private ArrayList<AttackPlayerOrderData> pendingAttacks;

    @FXML
    public ImageView backgroundImageView;

    @FXML
    public TitleLabel noAttacksScheduledTitle;

    @FXML
    public TitleLabel scheduledAttacksTitle;

    @FXML
    public VBox listContainer;

    @FXML
    public Button addAttackButton;

    @FXML
    public Label errorMessage;

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);
    }

    private void onPendingAttacksChange() {
        if (this.pendingAttacks.isEmpty()) {
            this.noAttacksScheduledTitle.setVisible(true);
            this.scheduledAttacksTitle.setVisible(false);
        } else {
            this.noAttacksScheduledTitle.setVisible(false);
            this.scheduledAttacksTitle.setVisible(true);
        }
        this.addAttackButton.setDisable(this.pendingAttacks.size() >= RiseAndFall.getGame().getOtherPlayersCount() || this.getRemainingUnits().isEmpty());
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);
        this.showError(errorMessage);

        if (this.pendingAttacks == null) {// Déjà chargée
            this.pendingAttacks = new ArrayList<>(RiseAndFall.getPlayer().getPendingAttacks());
        }

        this.onPendingAttacksChange();
        for (AttackPlayerOrderData attack : this.pendingAttacks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/butinfoalt/riseandfall/front/components/attack-component.fxml"));
                Node attackNode = loader.load();

                OrderAttacksListItemController controller = loader.getController();
                controller.init(this, attack);

                this.listContainer.getChildren().add(attackNode);
                this.attackNodes.put(attack, attackNode);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }

    @Override
    public void onHidden() {
        ViewController.super.onHidden();

        this.listContainer.getChildren().clear();
        this.attackNodes.clear();
    }

    @FXML
    public void handleAddAttack() {
        this.createOrEditAttack(null);
    }

    @FXML
    public void handleSave() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        player.setPendingAttacks(this.pendingAttacks);
        try {
            RiseAndFall.getClient().sendPacket(new PacketUpdateOrders(null, null, player.getPendingAttacks()));
        } catch (IOException e) {
            this.showError("Erreur lors de l'envoi des ordres : " + e.getMessage());
            LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des ordres", e);
            return;
        }
        this.switchBack();
        this.showError(null);
    }

    @FXML
    public void switchBack() {
        this.pendingAttacks = null;
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    public void createOrEditAttack(AttackPlayerOrderData attack) {
        RiseAndFallApplication.switchToView(View.ORDERS_ATTACK);
        OrderAttackController controller = View.ORDERS_ATTACK.getController();
        controller.init(this, attack);
    }

    void removeAttack(AttackPlayerOrderData attack) {
        Node attackNode = this.attackNodes.remove(attack);
        if (attackNode != null) {
            this.listContainer.getChildren().remove(attackNode);
        }
        this.pendingAttacks.remove(attack);
        this.onPendingAttacksChange();
    }

    void addOrReplaceAttack(AttackPlayerOrderData attack, AttackPlayerOrderData old) {
        if (old == null) {
            this.pendingAttacks.add(attack);
        } else {
            this.pendingAttacks.set(this.pendingAttacks.indexOf(old), attack);
        }
        this.onPendingAttacksChange();
    }

    List<OtherClientPlayer> getNotAttackedPlayers() {
        Set<OtherClientPlayer> alreadyAttackingPlayers = this.pendingAttacks.stream()
                .map(attackPlayerOrderData -> (OtherClientPlayer) attackPlayerOrderData.getTargetPlayer())
                .collect(Collectors.toSet());

        return RiseAndFall.getGame().getOtherPlayers().stream()
                .filter(otherClientPlayer -> !alreadyAttackingPlayers.contains(otherClientPlayer))
                .toList();
    }

    ObjectIntMap<UnitType> getRemainingUnits() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        ObjectIntMap<UnitType> remainingUnits = player.getUnitMap().clone();
        this.pendingAttacks.stream().map(AttackPlayerOrderData::getUsingUnits).forEach(remainingUnits::decrement);
        return remainingUnits;
    }

    /**
     * Méthode pour afficher un message d'erreur.
     *
     * @param error Le message d'erreur à afficher.
     */
    private void showError(String error) {
        if (error == null) {
            this.errorMessage.setVisible(false);
        } else {
            this.errorMessage.setText(error);
            this.errorMessage.setVisible(true);
        }
    }
}
