package fr.butinfoalt1.riseandfall.front.description;

import fr.butinfoalt1.riseandfall.front.RiseAndFallApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class DescriptionStage extends Stage {
    public DescriptionStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DescriptionStage.class.getResource("description-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        this.setTitle("Description de Rise and fall");
        this.setScene(scene);
        DescriptionController controller = fxmlLoader.getController();
        controller.backgroundImageView.setImage(new Image(Objects.requireNonNull(RiseAndFallApplication.class.getResourceAsStream("images/background.jpg"))));

        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            controller.backgroundImageView.setFitHeight(this.getHeight());
            controller.backgroundImageView.setFitWidth(this.getWidth());
        });

        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            controller.backgroundImageView.setFitHeight(this.getHeight());
            controller.backgroundImageView.setFitWidth(this.getWidth());
        });
    }
}


