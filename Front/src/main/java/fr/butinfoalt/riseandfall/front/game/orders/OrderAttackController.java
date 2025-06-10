package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import fr.butinfoalt.riseandfall.front.gamelogic.ClientGame;
import fr.butinfoalt.riseandfall.front.util.NamedItemStringConverter;

import java.util.Collection;

public class OrderAttackController implements ViewController {
    /**
     * Champs de sélection du joueur cible de l'attaque.
     */
    @FXML
    public ChoiceBox<OtherClientPlayer> targetPlayer;
    /**
     * Champ pour le composant de la quantité d'unités utilisées pour l'attaque.
     */
    @FXML
    public Label totalUnit;

    @FXML
    private ImageView backgroundImageView;
    /**
     * Méthode appelée par JavaFX quand la vue est initialisée.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView);

        // Initialisation de la liste des joueurs cibles
        ClientGame game = RiseAndFall.getGame();
        Collection<OtherClientPlayer> players = game.getOtherPlayers();
        NamedItemStringConverter<OtherClientPlayer> converter = new NamedItemStringConverter<>();
        targetPlayer.setConverter(converter);
        targetPlayer.getItems().addAll(players);
        targetPlayer.getSelectionModel().selectFirst();

        // Initialisation de la quantité d'unités
        totalUnit.setText("0");

    }


}


