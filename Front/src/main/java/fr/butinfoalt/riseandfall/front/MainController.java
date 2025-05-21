package fr.butinfoalt.riseandfall.front;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import java.util.AbstractMap;
import java.util.Map;

import fr.butinfoalt.riseandfall.front.description.DescriptionStage;
import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.orders.OrderController;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.network.packets.PacketGameAction;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Contrôleur pour la vue principale de l'application.
 * Il gère l'affichage des ressources du joueur, la navigation vers les autres vues,
 * ainsi que les actions principales comme terminer un tour ou quitter la partie.
 */
public class MainController {
    /**
     * Champ pour le composant de la quantité d'or.
     */
    @FXML
    public Label goldField;

    /**
     * Champ pour le composant de l'intelligence du joueur.
     */
    @FXML
    public Label intelligenceField;

    /**
     * Champ pour le composant de la race du joueur.
     */
    @FXML
    public Label raceField;

    /**
     * Champ pour le conteneur des unités possédées par le joueur.
     */
    @FXML
    public VBox unitVBox;

    /**
     * Champ pour le conteneur des bâtiments possédés par le joueur.
     */
    @FXML
    public VBox buildingsVBox;

    @FXML
    public VBox resourcesVBox;
    private TableView<Map.Entry<String, String>> resourceTableView;

    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    private TableView<Map.Entry<String, Integer>> unitTableView;
    private TableView<Map.Entry<String, Integer>> buildingTableView;

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Description".
     * Elle affiche la fenêtre de description des éléments du jeu.
     */
    @FXML
    public void switchToDescriptionPage() {
        DescriptionStage.INSTANCE.show();
        DescriptionStage.INSTANCE.toFront();
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Ordres".
     * Elle bascule vers la vue des ordres et charge les ordres en attente.
     */
    @FXML
    public void switchToOrders() {
        RiseAndFallApplication.switchToView(View.ORDERS);
        OrderController orderController = View.ORDERS.getController();
        orderController.loadPendingOrders();
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Fin du tour".
     * Elle exécute les ordres du joueur et applique les changements liés au tour.
     */
    @FXML
    private void handleEndTurn() {
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.NEXT_TURN));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du paquet de fin de tour :");
            e.printStackTrace();
        }
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton "Quitter".
     * Elle réinitialise l'état du joueur et retourne à la vue précédente.
     */
    @FXML
    public void handleQuitGame() {
        try {
            RiseAndFall.getClient().sendPacket(new PacketGameAction(PacketGameAction.Action.QUIT_GAME));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du paquet pour quitter la partie :");
            e.printStackTrace();
        }
        RiseAndFall.resetPlayer();
        RiseAndFallApplication.switchToPreviousView();
    }

    /**
     * Met à jour l'affichage des ressources et possessions du joueur dans l'interface.
     * Cette méthode met à jour les labels affichant l'or, l'intelligence et la race,
     * ainsi que les listes des unités et bâtiments du joueur.
     */
    public void updateFields() {
        ClientPlayer player = RiseAndFall.getPlayer();
        ObservableList<Map.Entry<String, String>> resourceData = FXCollections.observableArrayList();
        resourceData.add(new AbstractMap.SimpleEntry<>("Or", String.valueOf(player.getGoldAmount())));
        resourceData.add(new AbstractMap.SimpleEntry<>("Intelligence", String.valueOf(player.getIntelligence())));
        resourceData.add(new AbstractMap.SimpleEntry<>("Race", player.getRace().getName())); // Race en texte → ou omettre
        resourceTableView.setItems(resourceData);

        ObservableList<Map.Entry<String, Integer>> unitData = FXCollections.observableArrayList();
        for (var entry : player.getUnitMap()) {
            unitData.add(new AbstractMap.SimpleEntry<>(entry.getKey().getName(), entry.getValue()));
        }
        unitTableView.setItems(unitData);

        ObservableList<Map.Entry<String, Integer>> buildingData = FXCollections.observableArrayList();
        for (var entry : player.getBuildingMap()) {
            buildingData.add(new AbstractMap.SimpleEntry<>(entry.getKey().getName(), entry.getValue()));
        }
        buildingTableView.setItems(buildingData);
    }

    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);

        resourceTableView = new TableView<>();
        TableColumn<Map.Entry<String, String>, String> resourceNameCol = new TableColumn<>("Nom");
        resourceNameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getKey()));
        resourceNameCol.setPrefWidth(150);

        TableColumn<Map.Entry<String, String>, String> resourceValueCol = new TableColumn<>("Quantité");
        resourceValueCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue()));
        resourceValueCol.setPrefWidth(100);

        resourceTableView.getColumns().setAll(resourceNameCol, resourceValueCol);
        resourcesVBox.getChildren().setAll(resourceTableView);

        // Création du tableau pour les unités
        unitTableView = new TableView<>();
        TableColumn<Map.Entry<String, Integer>, String> unitNameCol = new TableColumn<>("Nom");
        unitNameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getKey()));
        unitNameCol.setPrefWidth(150);

        TableColumn<Map.Entry<String, Integer>, Integer> unitCountCol = new TableColumn<>("Quantité");
        unitCountCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getValue()));
        unitCountCol.setPrefWidth(100);

        unitTableView.getColumns().setAll(unitNameCol, unitCountCol);
        unitVBox.getChildren().setAll(unitTableView);

        // Création du tableau pour les bâtiments
        buildingTableView = new TableView<>();
        TableColumn<Map.Entry<String, Integer>, String> buildingNameCol = new TableColumn<>("Nom");
        buildingNameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getKey()));
        buildingNameCol.setPrefWidth(150);

        TableColumn<Map.Entry<String, Integer>, Integer> buildingCountCol = new TableColumn<>("Quantité");
        buildingCountCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getValue()));
        buildingCountCol.setPrefWidth(100);

        buildingTableView.getColumns().setAll(buildingNameCol, buildingCountCol);
        buildingsVBox.getChildren().setAll(buildingTableView);

        updateFields();
    }
}