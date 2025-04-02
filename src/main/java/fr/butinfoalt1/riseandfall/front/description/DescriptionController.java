package fr.butinfoalt1.riseandfall.front.description;

import fr.butinfoalt1.riseandfall.front.RiseAndFallApplication;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class DescriptionController {
    @FXML
    public ImageView backgroundImageView;

    @FXML
    public StackPane rootPane;

    public void initialize() {
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
    }
}