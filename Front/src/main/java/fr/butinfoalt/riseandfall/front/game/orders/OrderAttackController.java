package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.game.orders.amountselector.ItemAmountSelector;
import fr.butinfoalt.riseandfall.front.game.orders.table.ItemTableRow;
import fr.butinfoalt.riseandfall.front.game.orders.table.UnitsSelectTable;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.gamelogic.order.OrderAttackPlayer;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateOrders;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class OrderAttackController implements ViewController {
    private ObjectIntMap<UnitType> usingUnits;

    /**
     * Champs de sélection du joueur cible de l'attaque.
     */
    @FXML
    public ChoiceBox<OtherClientPlayer> targetPlayerSelector;

    /**
     * Champ de sélections des unités à utiliser pour l'attaque.
     */
    @FXML
    public UnitsSelectTable unitsTable;

    /**
     * Champ pour le composant du message d'erreur.
     */
    @FXML
    public Label errorMessage;

    @FXML
    private ImageView backgroundImageView;

    private void loadExistingOrder(CurrentClientPlayer player) {
        for (BaseOrder order : player.getPendingOrders()) {
            if (order instanceof OrderAttackPlayer orderAttackPlayer) {
                this.targetPlayerSelector.setValue((OtherClientPlayer) orderAttackPlayer.getTargetPlayer());
                this.usingUnits = orderAttackPlayer.getUsingUnits();
                return;
            }
        }
        this.targetPlayerSelector.setValue(null);
        this.usingUnits = player.getUnitMap().createEmptyClone();
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        if (errorMessage != null) {
            this.showError(errorMessage);
        }

        this.targetPlayerSelector.getItems().clear();
        this.targetPlayerSelector.getItems().addAll(RiseAndFall.getGame().getOtherPlayers());

        CurrentClientPlayer player = RiseAndFall.getPlayer();
        ObjectIntMap<UnitType> playerUnits = player.getUnitMap();
        this.loadExistingOrder(player);

        this.unitsTable.getItems().clear();
        for (ObjectIntMap.Entry<UnitType> entry : this.usingUnits) {
            ItemAmountSelector<UnitType> selector = new ItemAmountSelector<>(entry, value -> value <= playerUnits.get(entry.getKey()));
            this.unitsTable.getItems().add(new ItemTableRow<>(entry.getKey(), selector));
            selector.updateButtonsState();
        }
    }

    /**
     * Méthode appelée par JavaFX quand la vue est initialisée.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);

        NamedItemStringConverter<OtherClientPlayer> converter = new NamedItemStringConverter<>();
        targetPlayerSelector.setConverter(converter);

        this.unitsTable.setMaxWidth(1000);
    }

    /**
     * Méthode appelée par JavaFX pour revenir à la vue précédente.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    /**
     * Méthode appelée par JavaFX pour gérer l'action de sauvegarde.
     * Elle enregistre les ordres en attente du joueur et revient à la vue précédente.
     */
    @FXML
    private void handleSave() {
        OtherClientPlayer targetPlayer = this.targetPlayerSelector.getValue();
        if (targetPlayer == null) {
            this.showError("Veuillez sélectionner un joueur cible pour l'attaque.");
            return;
        }

        if (this.usingUnits.isReset()) {
            this.showError("Veuillez sélectionner au moins une unité à utiliser pour l'attaque.");
            return;
        }

        this.showError(null);

        CurrentClientPlayer player = RiseAndFall.getPlayer();

        ArrayList<BaseOrder> newOrders = new ArrayList<>();
        player.getPendingOrders().stream()
                .filter(baseOrder -> !(baseOrder instanceof OrderAttackPlayer))
                .forEach(newOrders::add);

        newOrders.add(new OrderAttackPlayer(targetPlayer, this.usingUnits));
        try {
            RiseAndFall.getClient().sendPacket(new PacketUpdateOrders(newOrders));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des ordres", e);
            return;
        }
        player.updatePendingOrders(newOrders);

        this.switchBack();
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
