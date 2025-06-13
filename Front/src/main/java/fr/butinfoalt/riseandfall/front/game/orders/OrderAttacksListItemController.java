package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur pour un élément de la liste des attaques dans l'interface utilisateur.
 * Permet d'afficher les informations d'une attaque et de gérer les actions d'édition et de suppression.
 */
public class OrderAttacksListItemController {
    /**
     * Le contrôleur parent de la liste des attaques, utilisé pour ajouter, modifier ou supprimer des attaques.
     */
    private OrderAttacksListController parentController;
    /**
     * Les données de l'attaque associée à cet élément de la liste.
     */
    private AttackPlayerOrderData attack;

    /**
     * Champ pour afficher le nom du joueur cible de l'attaque.
     */
    @FXML
    public Label targetPlayerName;

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton d'édition de l'attaque.
     * Elle ouvre la vue de création ou d'édition d'attaque avec les données de l'attaque actuelle.
     */
    @FXML
    public void onEdit() {
        this.parentController.createOrEditAttack(this.attack);
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton de suppression de l'attaque.
     * Elle supprime l'attaque de la liste des attaques du contrôleur parent.
     */
    @FXML
    public void onDelete() {
        this.parentController.removeAttack(this.attack);
    }

    /**
     * Initialise le contrôleur avec les données de l'attaque et le contrôleur parent.
     * Cette méthode est appelée par {@link OrderAttacksListController#onDisplayed(String)} pour configurer
     * l'élément de la liste avec les informations de l'attaque.
     *
     * @param parentController Le contrôleur de la liste des attaques parent à cet élément.
     * @param attack           Les données de l'attaque à afficher dans cet élément de la liste.
     */
    void init(OrderAttacksListController parentController, AttackPlayerOrderData attack) {
        this.parentController = parentController;
        this.attack = attack;
        this.targetPlayerName.setText("Attaque contre " + ((OtherClientPlayer) attack.getTargetPlayer()).getName());
    }
}
