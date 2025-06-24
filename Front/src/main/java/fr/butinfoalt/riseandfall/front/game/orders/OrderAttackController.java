package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.game.orders.amountselector.ItemAmountSelector;
import fr.butinfoalt.riseandfall.front.game.orders.table.ItemTableRow;
import fr.butinfoalt.riseandfall.front.game.orders.table.UnitsSelectTable;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;

/**
 * Contrôleur pour la vue de création ou d'édition d'une attaque.
 */
public class OrderAttackController implements ViewController {
    /**
     * Champ pour le composant racine de la vue.
     */
    @FXML
    public ScrollPane root;

    /**
     * Les unités utilisées pour l'attaque.
     */
    private ObjectIntMap<UnitType> usingUnits;
    /**
     * Le contrôleur de la liste des attaques, utilisé pour ajouter ou modifier des attaques.
     */
    private OrderAttacksListController listController;
    /**
     * Les données de l'attaque en cours d'édition, ou null si on crée une nouvelle attaque.
     */
    private AttackPlayerOrderData editingAttack;

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

    /**
     * Champ pour l'image de fond de la vue.
     */
    @FXML
    private ImageView backgroundImageView;

    /**
     * Méthode appelée quand la vue est affichée.
     * Elle initialise les composants et affiche un message d'erreur si nécessaire.
     *
     * @param errorMessage Le message d'erreur à afficher, ou null si aucun message d'erreur n'est à afficher.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        if (errorMessage != null) {
            this.showError(errorMessage);
        }

        this.unitsTable.getItems().clear();
    }

    /**
     * Méthode appelée quand la vue est cachée.
     * Elle réinitialise les sélections et les données de l'attaque en cours d'édition.
     */
    @Override
    public void onHidden() {
        ViewController.super.onHidden();
        this.targetPlayerSelector.setValue(null);
        this.usingUnits = null;
        this.listController = null;
        this.editingAttack = null;
    }

    /**
     * Méthode appelée par JavaFX quand la vue est initialisée.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);

        NamedItemStringConverter<OtherClientPlayer> converter = new NamedItemStringConverter<>();
        targetPlayerSelector.setConverter(converter);

        this.unitsTable.setMaxWidth(1000);
    }

    /**
     * Méthode appelée par JavaFX pour revenir à la vue précédente.
     */
    @FXML
    private void switchBack() {
        RiseAndFallApplication.switchToView(View.ORDERS_ATTACK_LIST);
    }

    /**
     * Méthode appelée par JavaFX pour gérer l'action de sauvegarde.
     * Elle vérifie si un joueur cible est sélectionné et si au moins une unité est choisie pour l'attaque.
     * Si tout est valide, elle ajoute ou remplace l'attaque dans la liste des attaques.
     * Si des erreurs sont détectées, elle affiche un message d'erreur approprié.
     */
    @FXML
    private void handleSave() {
        OtherClientPlayer targetPlayer = this.targetPlayerSelector.getValue();
        if (targetPlayer == null) {
            this.showError("Veuillez sélectionner un joueur cible pour l'attaque.");
            return;
        }

        if (this.usingUnits.isEmpty()) {
            this.showError("Veuillez sélectionner au moins une unité à utiliser pour l'attaque.");
            return;
        }

        this.showError(null);

        this.listController.addOrReplaceAttack(new AttackPlayerOrderData(targetPlayer, this.usingUnits), this.editingAttack);
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

    /**
     * Méthode pour initialiser le contrôleur avec les données de l'attaque et le contrôleur de la liste des attaques.
     * Appelée par {@link OrderAttacksListController#createOrEditAttack(AttackPlayerOrderData)}.
     * Cette méthode prépare le contrôleur pour créer ou éditer une attaque en remplissant
     * la liste des joueurs cibles et en initialisant les unités à utiliser pour l'attaque.
     *
     * @param listController Le contrôleur de la liste des attaques.
     * @param attack         Les données de l'attaque à éditer, ou null si on crée une nouvelle attaque.
     */
    public void init(OrderAttacksListController listController, AttackPlayerOrderData attack) {
        this.listController = listController;
        this.editingAttack = attack;

        ObservableList<OtherClientPlayer> selectablePlayers = this.targetPlayerSelector.getItems();
        selectablePlayers.clear();
        selectablePlayers.addAll(listController.getAttackablePlayers());
        ObjectIntMap<UnitType> remainingUnits = listController.getRemainingUnits();

        if (attack != null) {
            OtherClientPlayer currentAttackedPlayer = (OtherClientPlayer) attack.getTargetPlayer();
            selectablePlayers.add(currentAttackedPlayer);
            remainingUnits.increment(attack.getUsingUnits());
            this.targetPlayerSelector.setValue(currentAttackedPlayer);
            this.usingUnits = attack.getUsingUnits().clone();
        } else {
            this.targetPlayerSelector.setValue(null);
            this.usingUnits = RiseAndFall.getPlayer().getUnitMap().createEmptyClone();
        }

        for (ObjectIntMap.Entry<UnitType> entry : this.usingUnits) {
            ItemAmountSelector<UnitType> selector = new ItemAmountSelector<>(entry, value -> value <= remainingUnits.get(entry.getKey()));
            this.unitsTable.getItems().add(new ItemTableRow<>(entry.getKey(), selector));
            selector.updateButtonsState();
        }
    }
}
