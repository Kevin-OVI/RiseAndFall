package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

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
     * Champ pour le composant de la race.
     */
    @FXML
    public Label raceField;

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
    @FXML
    public ImageView backgroundImageView;

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
        RiseAndFall.getPlayer().executeOrders();
    }

    /**
     * Méthode appelée par JavaFX quand on clique sur le bouton pour quitter le jeu.
     */
    @FXML
    public void handleQuitGame() {
        RiseAndFall.resetPlayer();
        RiseAndFallApplication.switchToPreviousView();
    }

    /**
     * Méthode pour mettre à jour l'affichage des ressources du joueur.
     */
    public void updateFields() {
        ClientPlayer player = RiseAndFall.getPlayer();
        this.goldField.setText("Or : " + player.getGoldAmount());
        this.intelligenceField.setText("Intelligence : " + player.getIntelligence());
        this.raceField.setText("Race : " + player.getRace().getDisplayName());

        this.unitVBox.getChildren().clear();
        this.buildingsVBox.getChildren().clear();
        for (var entry : player.getUnitMap()) {
            Label label = new Label(entry.getKey().getDisplayName() + " : " + entry.getValue());
            this.unitVBox.getChildren().add(label);
        }
        for (var entry : player.getBuildingMap()) {
            Label label = new Label(entry.getKey().getDisplayName() + " : " + entry.getValue());
            this.buildingsVBox.getChildren().add(label);
        }
    }

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        // Définir l'image de fond
        Image image = new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResourceAsStream("images/background1.png")));
        this.backgroundImageView.setImage(image);

        // Adapter la taille de l'image de fond à la taille de la fenêtre.
        // On recadre l'image de manière à ce qu'elle recouvre tout l'écran sans être déformée.
        InvalidationListener adaptImageSize = (observable) -> {
            this.backgroundImageView.setFitWidth(Math.max(scene.getWidth(), scene.getHeight() * image.getWidth() / image.getHeight()));
            this.backgroundImageView.setFitHeight(Math.max(scene.getHeight(), scene.getWidth() * image.getHeight() / image.getWidth()));
            this.backgroundImageView.setX((scene.getWidth() - this.backgroundImageView.getFitWidth()) / 2);
            this.backgroundImageView.setY((scene.getHeight() - this.backgroundImageView.getFitHeight()) / 2);
        };

        scene.widthProperty().addListener(adaptImageSize);
        scene.heightProperty().addListener(adaptImageSize);
        adaptImageSize.invalidated(null); // Appel initial pour adapter l'image à la taille de la fenêtre
    }
}
