package fr.butinfoalt1.riseandfall.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RiseAndFallApplication extends Application {
    public static final int WIDTH = 800, HEIGHT = 500;
    private static Stage mainWindow;

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainWindow() {
        return mainWindow;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RiseAndFallApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        mainWindow = stage;
    }
}