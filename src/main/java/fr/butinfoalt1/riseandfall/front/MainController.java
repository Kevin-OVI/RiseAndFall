package fr.butinfoalt1.riseandfall.front;

import fr.butinfoalt1.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt1.riseandfall.gamelogic.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Contrôleur pour la vue principale de l'application.
 */
public class MainController {
    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    public Label goldField;

    /**
     * Champ pour le composant de l'intelligence.
     */
    public Label intelligenceField;

    /**
     * Champ pour le composant contenant les unités.
     */
    @FXML
    public VBox unitVBox;

    /**
     * Champ pour le composant contenant les bâtiments.
     */
    @FXML
    public VBox buildingsVBox;

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour ouvrir la page de description.
     */
    @FXML
    public void switchToDescriptionPage() {
        DescriptionStage.INSTANCE.show();
        DescriptionStage.INSTANCE.toFront();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour afficher la page des ordres.
     */
    @FXML
    public void switchToOrders() {
        RiseAndFallApplication.switchToView(View.ORDERS);
        OrderController orderController = View.ORDERS.getController();
        orderController.loadPendingOrders();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour passer au tour suivant.
     */
    @FXML
    private void handleEndTurn() {
        Player.SINGLE_PLAYER.executeOrders();
    }

    /**
     * Méthode pour mettre à jour l'affichage des ressources du joueur.
     */
    public void updateFields() {
        this.goldField.setText("Or : " + Player.SINGLE_PLAYER.getGoldAmount());
        this.intelligenceField.setText("Intelligence : " + Player.SINGLE_PLAYER.getIntelligence());

        this.unitVBox.getChildren().clear();
        this.buildingsVBox.getChildren().clear();
        for (var entry : Player.SINGLE_PLAYER.getUnitMap()) {
            Label label = new Label(entry.getKey().getDisplayName() + " : " + entry.getValue());
            this.unitVBox.getChildren().add(label);
        }
        for (var entry : Player.SINGLE_PLAYER.getBuildingMap()) {
            Label label = new Label(entry.getKey().getDisplayName() + " : " + entry.getValue());
            this.buildingsVBox.getChildren().add(label);
        }
    }
}
