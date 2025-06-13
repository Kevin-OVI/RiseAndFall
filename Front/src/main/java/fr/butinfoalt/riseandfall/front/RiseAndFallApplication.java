package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.authentification.LoadingController;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe principale de l'application. Elle gère la création de la fenêtre principale et le changement de vues.
 */
public class RiseAndFallApplication extends Application {
    /**
     * Largeur et hauteur de la fenêtre principale.
     */
    public static final int WIDTH = 800, HEIGHT = 500;
    /**
     * Fenêtre principale de l'application.
     */
    private static Stage mainWindow;

    /**
     * Vue actuellement affichée dans la fenêtre principale.
     */
    private static View currentView;

    /**
     * Méthode principale de l'application. Elle lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--auth-token-file")) {
                if (i + 1 < args.length) {
                    Environment.authTokenFile = args[i + 1];
                } else {
                    LogManager.logError("Le fichier d'authentification n'a pas été spécifié après l'option --auth-token-file");
                }
            }
        }


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
     * Méthode pour obtenir la vue actuellement affichée dans la fenêtre principale.
     *
     * @return La vue actuellement affichée.
     */
    public static View getCurrentView() {
        return currentView;
    }

    /**
     * Méthode pour changer la vue de la fenêtre principale.
     * Cette méthode est une surcharge de {@link #switchToView(View, String)} qui n'affiche pas de message d'erreur.
     *
     * @param view La nouvelle vue à afficher.
     */
    public static void switchToView(View view) {
        switchToView(view, null);
    }

    /**
     * Méthode pour changer la vue de la fenêtre principale.
     *
     * @param view         La nouvelle vue à afficher.
     * @param errorMessage Le message d'erreur à afficher dans la vue
     */
    public static void switchToView(View view, String errorMessage) {
        if (currentView != view) {
            if (currentView.getController() instanceof ViewController viewController) {
                viewController.onHidden();
            }
            Parent newRoot = view.getSceneRoot();
            mainWindow.getScene().setRoot(newRoot);
            mainWindow.setTitle(view.getWindowTitle());
            currentView = view;
        }
        if (view.getController() instanceof ViewController viewController) {
            viewController.onDisplayed(errorMessage);
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
        currentView = View.LOADING;

        Scene scene = new Scene(currentView.getSceneRoot(), WIDTH, HEIGHT);
        stage.setTitle(currentView.getWindowTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest(this::onCloseRequest);
        stage.show();

        RiseAndFall.initSocketClient();

        LoadingController controller = currentView.getController();
        controller.initializeScene(scene);
        if (currentView.getController() instanceof ViewController viewController) {
            viewController.onDisplayed(null);
        }
    }

    private void onCloseRequest(WindowEvent windowEvent) {
        if (!windowEvent.isConsumed()) {
            try {
                RiseAndFall.getClient().closeWithoutReconnect();
            } catch (IOException e) {
                LogManager.logError("Erreur lors de la fermeture du client", e);
            }
            RiseAndFall.TIMER.cancel();
        }
    }
}
