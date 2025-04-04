package fr.butinfoalt1.riseandfall.front;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class RiseAndFallApplication extends Application {
    public static final int WIDTH = 800, HEIGHT = 500;
    private static Stage mainWindow;
    private static final Stack<Parent> STAGE_ROOT_STACK = new Stack<>();

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainWindow() {
        return mainWindow;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(View.MAIN.getSceneRoot(), WIDTH, HEIGHT);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        mainWindow = stage;
    }

    public static void switchToView(View view) {
        Parent newRoot = view.getSceneRoot();
        STAGE_ROOT_STACK.push(mainWindow.getScene().getRoot());
        mainWindow.getScene().setRoot(newRoot);
    }

    public static void switchToPreviousView() {
        if (!STAGE_ROOT_STACK.isEmpty()) {
            Parent previousRoot = STAGE_ROOT_STACK.pop();
            mainWindow.getScene().setRoot(previousRoot);
        }
    }
}