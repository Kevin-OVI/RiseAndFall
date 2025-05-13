package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.authentification.LoadingController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;
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
    private static final Stack<StageViewElement> stageViewStack = new Stack<>();
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
        Font.loadFont(Objects.requireNonNull(RiseAndFallApplication.class.getResourceAsStream("fonts/IMFellEnglishSC-Regular.ttf")), 12);
        Application.launch();
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
     * On peut choisir de remplacer la vue actuelle ou de l'empiler pour y revenir plus tard.
     *
     * @param view    La nouvelle vue à afficher.
     * @param replace Indique si la vue actuelle doit être remplacée ou non.
     */
    public static void switchToView(View view, boolean replace) {
        Parent newRoot = view.getSceneRoot();
        if (!replace) {
            stageViewStack.push(new StageViewElement(mainWindow.getScene().getRoot(), mainWindow.getTitle()));
        }
        mainWindow.getScene().setRoot(newRoot);
        mainWindow.setTitle(view.getWindowTitle());
    }

    /**
     * Méthode pour changer la vue de la fenêtre principale.
     * On empile d'abord la vue actuelle avant de la remplacer par la nouvelle vue afin de pouvoir y revenir.
     *
     * @param view La nouvelle vue à afficher.
     */
    public static void switchToView(View view) {
        switchToView(view, false);
    }

    /**
     * Méthode pour revenir à la vue précédente.
     * On dépile la vue précédente et on la remet comme racine de la scène.
     */
    public static void switchToPreviousView() {
        if (!stageViewStack.isEmpty()) {
            StageViewElement previousViewElement = stageViewStack.pop();
            mainWindow.getScene().setRoot(previousViewElement.root());
            mainWindow.setTitle(previousViewElement.title());
        }
    }

    /**
     * Méthode appelée au démarrage de l'application. Elle initialise la fenêtre principale et affiche la scène principale.
     *
     * @param stage La fenêtre principale de l'application.
     */
    @Override
    public void start(Stage stage) {
        mainWindow = stage;

        Scene scene = new Scene(View.LOADING.getSceneRoot(), WIDTH, HEIGHT);
        stage.setTitle(View.LOADING.getWindowTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest(this::onCloseRequest);
        stage.show();

        RiseAndFall.initSocketClient();

        LoadingController controller = View.LOADING.getController();
        controller.initializeScene(scene);
    }

    private void onCloseRequest(WindowEvent windowEvent) {
        if (!windowEvent.isConsumed()) {
            try {
                RiseAndFall.getClient().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Représente un élément de la vue avec sa racine et son titre.
     *
     * @param root  La racine de la scène à enregistrer.
     * @param title Le titre de la fenêtre à enregistrer.
     */
    private record StageViewElement(Parent root, String title) {
    }
}
