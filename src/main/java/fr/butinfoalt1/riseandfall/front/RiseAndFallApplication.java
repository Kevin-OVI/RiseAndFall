package fr.butinfoalt1.riseandfall.front;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Classe principale de l'application. Elle gère la création de la fenêtre principale et le changement de vues.
 */
public class RiseAndFallApplication extends Application {
    /**
     * Largeur et hauteur de la fenêtre principale.
     */
    public static final int WIDTH = 800, HEIGHT = 500;
    /**
     * Pile pour gérer les vues précédentes.
     */
    private static final Stack<Parent> STAGE_ROOT_STACK = new Stack<>();
    /**
     * Fenêtre principale de l'application.
     */
    private static Stage mainWindow;

    /**
     * Méthode principale de l'application. Elle lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Méthode pour obtenir la fenêtre principale de l'application.
     *
     * @return La fenêtre principale de l'application.
     */
    public static Stage getMainWindow() {
        return mainWindow;
    }

    /**
     * Méthode pour changer la vue de la fenêtre principale.
     * On empile d'abord la vue actuelle avant de la remplacer par la nouvelle vue afin de pouvoir y revenir.
     *
     * @param view La nouvelle vue à afficher.
     */
    public static void switchToView(View view) {
        Parent newRoot = view.getSceneRoot();
        STAGE_ROOT_STACK.push(mainWindow.getScene().getRoot());
        mainWindow.getScene().setRoot(newRoot);
    }

    /**
     * Méthode pour revenir à la vue précédente.
     * On dépile la vue précédente et on la remet comme racine de la scène.
     */
    public static void switchToPreviousView() {
        if (!STAGE_ROOT_STACK.isEmpty()) {
            Parent previousRoot = STAGE_ROOT_STACK.pop();
            mainWindow.getScene().setRoot(previousRoot);
        }
    }

    /**
     * Méthode appelée au démarrage de l'application. Elle initialise la fenêtre principale et affiche la scène principale.
     *
     * @param stage La fenêtre principale de l'application.
     */
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(View.MAIN.getSceneRoot(), WIDTH, HEIGHT);
        stage.setTitle("Rise & Fall");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        mainWindow = stage;
    }
}
