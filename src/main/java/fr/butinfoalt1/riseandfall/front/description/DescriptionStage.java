package fr.butinfoalt1.riseandfall.front.description;

import fr.butinfoalt1.riseandfall.front.RiseAndFallApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DescriptionStage extends Stage {
    private static final String text = """
            Rise and Fall est un jeu développé par une équipe de choc.
            Le but est de créer un jeu tour par tour dans un monde fantasy.
            On a présenté le projet, maintenant passons aux règles du jeu :
            Rise & Fall est un jeu de stratégie au tour par tour où le joueur dirige une civilisation.
            L'objectif est de gérer son économie et son expansion en construisant des bâtiments
            et en recrutant des unités tout en optimisant ses ressources.
            """;

    public DescriptionStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DescriptionStage.class.getResource("description-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        this.setTitle("Description de Rise and fall");
        this.setScene(scene);
        DescriptionController controller = fxmlLoader.getController();
        controller.backgroundImageView.setImage(new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResourceAsStream("images/background.jpg"))));

        // Adapter la taille de l'image de fond à la taille de la fenêtre
        controller.backgroundImageView.fitHeightProperty().bind(this.heightProperty());
        controller.backgroundImageView.fitWidthProperty().bind(this.widthProperty());

        // Adapter la largeur du texte à la taille de l'écran
        controller.textFlow.prefWidthProperty().bind(this.widthProperty().multiply(0.8)); // 80% de la largeur

        controller.textFlow.getChildren().add(new Text(text));
    }
}
