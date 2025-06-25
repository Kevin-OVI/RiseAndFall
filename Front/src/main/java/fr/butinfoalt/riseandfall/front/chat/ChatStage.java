package fr.butinfoalt.riseandfall.front.chat;

import fr.butinfoalt.riseandfall.front.View;
import javafx.stage.Stage;

/**
 * Représente la fenêtre de chat
 */
public class ChatStage extends Stage {
    /**
     * Instance unique de la fenêtre de chat.
     * Utilisée pour garantir qu'il n'y a qu'une seule fenêtre de ce type ouverte à la fois.
     */
    private static final ChatStage INSTANCE = new ChatStage();

    /**
     * Constructeur privé pour initialiser la fenêtre de chat.
     * Définit la taille minimale, le titre et la scène de la fenêtre.
     */
    private ChatStage() {
        this.setMinWidth(400);
        this.setMinHeight(300);
        this.setTitle(View.CHAT.getWindowTitle());
        this.setScene(View.CHAT.getScene(800, 600));
    }

    /**
     * Méthode statique pour ouvrir la fenêtre de chat et la mettre au premier plan.
     */
    public static void openWindow() {
        INSTANCE.show();
        INSTANCE.toFront();
        ChatController controller = View.CHAT.getController();
        controller.onDisplayed(null);
    }

    /**
     * Méthode statique pour définir le titre de la fenêtre de chat avec un texte supplémentaire.
     *
     * @param extra Le texte supplémentaire à ajouter au titre de la fenêtre.
     */
    public static void setTitleExtra(String extra) {
        INSTANCE.setTitle(extra == null || extra.isEmpty() ? View.CHAT.getWindowTitle() : View.CHAT.getWindowTitle() + " - " + extra);
    }
}
