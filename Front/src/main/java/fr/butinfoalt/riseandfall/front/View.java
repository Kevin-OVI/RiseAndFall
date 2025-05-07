package fr.butinfoalt.riseandfall.front;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Enum représentant les différentes vues de l'application.
 * Chaque vue est associée à un fichier FXML.
 */
public enum View {
    /**
     *
     */
    WELCOME("welcome-view.fxml", "Rise & Fall - Bienvenue"),

    /**
     * Vue principale de l'application.
     */
    MAIN("main-view.fxml", "Rise & Fall"),
    /**
     * Vue de gestion des ordres.
     */
    ORDERS("orders-view.fxml", "Rise & Fall - Définition des ordres"),
    /**
     * Vue de la description et des règles du jeu.
     */
    DESCRIPTION("description-view.fxml", "Rise & Fall - Description et règles du jeu"),
    ;


    private static final String GENERAL_STYLESHEET = Objects.requireNonNull(RiseAndFallApplication.class.getResource("styles/style.css")).toExternalForm();


    /**
     * Le titre de la fenêtre associé à la vue.
     */
    private final String windowTitle;

    /**
     * Le nom du fichier FXML associé à la vue.
     */
    private final String viewName;

    /**
     * Le loader FXML utilisé pour charger la vue.
     */
    private FXMLLoader fxmlLoader;

    /**
     * Constructeur de l'énumération View.
     * Charge le fichier FXML correspondant à la vue.
     *
     * @param viewName    Le nom du fichier FXML associé à la vue.
     * @param windowTitle Le titre de la fenêtre.
     */
    View(String viewName, String windowTitle) {
        this.viewName = viewName;
        this.windowTitle = windowTitle;
    }

    private FXMLLoader getFxmlLoader() {
        if (this.fxmlLoader == null) {
            this.fxmlLoader = new FXMLLoader(View.class.getResource(this.viewName));
            try {
                this.fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load FXML file: " + this.viewName, e);
            }
        }
        return this.fxmlLoader;
    }

    /**
     * Méthode pour obtenir la racine de la scène de la vue.
     *
     * @return La racine de la scène de la vue.
     */
    public Parent getSceneRoot() {
        Parent root = this.getFxmlLoader().getRoot();
        ObservableList<String> styleSheets = root.getStylesheets();
        if (!styleSheets.contains(GENERAL_STYLESHEET)) {
            styleSheets.add(GENERAL_STYLESHEET);
        }
        return root;
    }

    /**
     * Méthode pour obtenir le contrôleur de la vue.
     *
     * @return Le contrôleur de la vue.
     */
    public <T> T getController() {
        return this.getFxmlLoader().getController();
    }

    /**
     * Méthode pour obtenir le titre de la fenêtre associé à la vue.
     *
     * @return Le titre de la fenêtre.
     */
    public String getWindowTitle() {
        return this.windowTitle;
    }

    /**
     * Méthode pour obtenir la scène de la vue.
     * Si la scène n'existe pas, elle est créée avec les dimensions spécifiées.
     * On peut également spécifier une fonction de configuration à appliquer à la scène lors de sa création.
     *
     * @param width           La largeur de la scène.
     * @param height          La hauteur de la scène.
     * @param firstSceneSetup Une fonction de configuration à appliquer à la scène lors de sa création.
     * @return La scène de la vue.
     */
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

    /**
     * Méthode pour obtenir la scène de la vue.
     * Si la scène n'existe pas, elle est créée avec les dimensions spécifiées.
     *
     * @param width  La largeur de la scène.
     * @param height La hauteur de la scène.
     * @return La scène de la vue.
     */
    public Scene getScene(double width, double height) {
        return this.getScene(width, height, null);
    }
}
