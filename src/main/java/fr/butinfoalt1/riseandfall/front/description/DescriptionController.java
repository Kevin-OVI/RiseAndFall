package fr.butinfoalt1.riseandfall.front.description;

import fr.butinfoalt1.riseandfall.front.RiseAndFallApplication;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

public class DescriptionController {
    @FXML
    public ImageView backgroundImageView;

    @FXML
    public StackPane rootPane;

    @FXML
    private TextFlow textFlow;

    @FXML
    public void initialize() {
        String texte = """
                Rise and Fall est un jeu développé par une équipe de choc.
                Le but est de créer un jeu tour par tour dans un monde fantasy.
                On a présenté le projet, maintenant passons aux règles du jeu :
                Rise & Fall est un jeu de stratégie au tour par tour où le joueur dirige une civilisation. 
                L'objectif est de gérer son économie et son expansion en construisant des bâtiments 
                et en recrutant des unités tout en optimisant ses ressources.
                """;

        Text textNode = new Text(texte);
        textFlow.getChildren().add(textNode);

        // Adapter la largeur du texte à la taille de l'écran
        textFlow.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.8)); // 80% de la largeur
    }
}