package fr.butinfoalt1.riseandfall.front;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.function.Consumer;

public enum View {
    MAIN("main-view.fxml"),
    ORDERS("orders-view.fxml"),
    DESCRIPTION("description-view.fxml");

    private final FXMLLoader fxmlLoader;

    View(String viewName) {
        this.fxmlLoader = new FXMLLoader(View.class.getResource(viewName));
        try {
            this.fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + viewName, e);
        }
    }

    public Parent getSceneRoot() {
        return this.fxmlLoader.getRoot();
    }

    public <T> T getController() {
        return this.fxmlLoader.getController();
    }

    public Scene getScene(double width, double height, Consumer<Scene> firstSceneSetup) {
        Scene scene = this.getSceneRoot().getScene();
        if (scene == null) {
            scene = new Scene(this.getSceneRoot(), width, height);
            if (firstSceneSetup != null) {
                firstSceneSetup.accept(scene);
            }
        }
        return scene;
    }

    public Scene getScene(double width, double height) {
        return this.getScene(width, height, null);
    }
}
