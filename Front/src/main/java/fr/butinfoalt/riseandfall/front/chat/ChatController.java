package fr.butinfoalt.riseandfall.front.chat;

import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.network.packets.PacketMessage;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contrôleur pour la fenêtre de chat.
 * Gère l'affichage des messages, l'envoi de nouveaux messages et la gestion des conversations.
 */
public class ChatController {
    /**
     * Format de date utilisé pour afficher l'heure des messages.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * Map pour stocker les messages et leurs vues associées.
     * Utilisée pour trier les messages par timestamp et faciliter leur affichage.
     */
    private final SortedMap<ChatMessage, HBox> messageViews = new TreeMap<>(Comparator.comparingLong(ChatMessage::getTimestamp));

    /**
     * Le joueur actuellement en conversation.
     */
    private OtherClientPlayer currentlyChattingWith;

    /**
     * Liste des joueurs avec lesquels on peut chatter.
     * Affichée dans la ListView.
     */
    @FXML
    private ListView<OtherClientPlayer> chatListView;

    /**
     * ScrollPane contenant les messages de la conversation actuelle.
     * Permet de faire défiler les messages.
     */
    @FXML
    private ScrollPane messageScrollPane;

    /**
     * Conteneur pour les messages de la conversation actuelle.
     */
    @FXML
    private VBox messageContainer;

    /**
     * Champ de texte pour saisir un nouveau message.
     * Utilisé pour envoyer des messages dans la conversation actuelle.
     */
    @FXML
    private TextField messageField;

    /**
     * Bouton pour envoyer le message saisi dans le champ de texte.
     * Déclenche l'envoi du message au serveur et à l'autre joueur.
     */
    @FXML
    public Button sendButton;

    /**
     * Initialisation du contrôleur.
     * Configure la ListView pour afficher les joueurs,
     */
    @FXML
    public void initialize() {
        this.chatListView.setCellFactory(listView -> new ChatListCell());
        this.chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                openChat(newValue);
            }
        });
        this.chatListView.setStyle("-fx-background-color: #1a1a1a; -fx-background-insets: 0; -fx-padding: 0;");
    }

    /**
     * Ouvre une conversation avec un autre joueur.
     * Vide le conteneur de messages et affiche les messages de la conversation sélectionnée.
     *
     * @param otherPlayer Le joueur avec lequel on souhaite chatter.
     */
    private void openChat(OtherClientPlayer otherPlayer) {
        this.currentlyChattingWith = otherPlayer;
        ChatStage.setTitleExtra(otherPlayer.getName());

        this.messageContainer.getChildren().clear();
        this.messageViews.clear();

        for (ChatMessage message : otherPlayer.getMessages()) {
            this.createMessageBox(message);
        }
        this.updateDisplayedMessages();

        this.scrollToEnd();
    }

    /**
     * Envoie le message saisi dans le champ de texte.
     * Vérifie que le joueur est en conversation et que le message n'est pas vide.
     * Crée un nouveau ChatMessage et l'envoie au serveur.
     */
    @FXML
    private void sendMessage() {
        if (this.currentlyChattingWith == null || this.messageField.getText().trim().isEmpty()) {
            return;
        }

        String messageText = this.messageField.getText().trim();
        long currentTime = System.currentTimeMillis();
        ChatMessage newMessage = new ChatMessage(RiseAndFall.getPlayer(), this.currentlyChattingWith, messageText, currentTime);
        this.currentlyChattingWith.addSendingMessage(newMessage);
        this.messageField.clear();

        try {
            RiseAndFall.getClient().sendPacket(new PacketMessage(newMessage));
        } catch (IOException e) {
            LogManager.logError("Impossible d'envoyer le packet de message", e);
            showErrorMessage("Erreur lors de l'envoi du message.");
        }
    }

    private void scrollToEnd() {
        Platform.runLater(() -> this.messageScrollPane.setVvalue(1.0));
        RiseAndFall.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> ChatController.this.messageScrollPane.setVvalue(1.0));
            }
        }, 10);
    }

    /**
     * Crée une boîte de message pour afficher un ChatMessage.
     * La boîte est stylisée en fonction de l'expéditeur (propre message ou message d'un autre joueur).
     *
     * @param message Le message à afficher.
     */
    private void createMessageBox(ChatMessage message) {
        HBox messageBox = new HBox(10);
        messageBox.setPadding(new Insets(5));

        boolean isOwnMessage = message.getSender() == RiseAndFall.getPlayer();

        VBox bubble = new VBox(5);
        bubble.setMaxWidth(400);
        bubble.setPadding(new Insets(10));
        bubble.setStyle(isOwnMessage ?
                "-fx-background-color: white; -fx-background-radius: 15;" :
                "-fx-background-color: #e0e0e0; -fx-background-radius: 15;");

        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        if (isOwnMessage) {
            if (message.getNonce() != -1) {
                timeLabel.setText("envoi...");
            }
        } else {
            Label senderLabel = new Label(((OtherClientPlayer) message.getSender()).getName());
            senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #666;");
            bubble.getChildren().add(senderLabel);
        }

        Label messageText = new Label(message.getMessage());
        messageText.setWrapText(true);
        messageText.setMaxWidth(380);
        bubble.getChildren().add(messageText);

        if (timeLabel.getText() == null || timeLabel.getText().isEmpty()) {
            timeLabel.setText(DATE_FORMAT.format(new Date(message.getTimestamp())));
        }
        bubble.getChildren().add(timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isOwnMessage) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.getChildren().addAll(spacer, bubble);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.getChildren().addAll(bubble, spacer);
        }

        this.messageViews.put(message, messageBox);
    }

    /**
     * Met à jour l'affichage des messages dans la fenêtre de chat.
     * Réinitialise le conteneur de messages avec les vues triées des messages.
     * Fait défiler la vue vers le bas pour afficher le dernier message.
     */
    private void updateDisplayedMessages() {
        this.messageContainer.getChildren().setAll(this.messageViews.sequencedValues());
        this.chatListView.refresh();
        this.scrollToEnd();
    }

    /**
     * Crée une boîte de message et met à jour l'affichage des messages.
     * Utilisée pour ajouter un nouveau message à la conversation actuelle.
     *
     * @param message Le message à ajouter.
     */
    private void createMessageBoxAndUpdate(ChatMessage message) {
        this.createMessageBox(message);
        this.updateDisplayedMessages();
    }

    /**
     * Charge les données de la fenêtre de chat.
     * Vide l'état actuel et ajoute tous les autres joueurs de la partie à la liste de chat.
     */
    public void loadData() {
        this.clearState();

        for (OtherClientPlayer otherPlayer : RiseAndFall.getGame().getOtherPlayers()) {
            this.chatListView.getItems().add(otherPlayer);
        }
    }

    /**
     * Vide l'état actuel du contrôleur de chat.
     * Réinitialise le joueur actuellement en conversation, vide le conteneur de messages et la liste de chat.
     */
    public void clearState() {
        this.currentlyChattingWith = null;
        this.messageContainer.getChildren().clear();
        this.chatListView.getItems().clear();
    }

    /**
     * Méthodes pour gérer les messages entrants et sortants.
     * Appelées par le gestionnaire de réseau pour mettre à jour l'interface utilisateur.
     */
    public void onAddMessage(OtherClientPlayer inChatWith, ChatMessage message) {
        if (inChatWith.equals(this.currentlyChattingWith)) {
            this.createMessageBoxAndUpdate(message);
        }
    }

    /**
     * Supprime un message de la conversation actuelle.
     * Utilisée lorsqu'un message en cours d'envoi est supprimé pour être remplacé par le message final.
     *
     * @param inChatWith le joueur avec lequel on discute
     * @param message    le message à supprimer
     */
    public void onRemoveMessage(OtherClientPlayer inChatWith, ChatMessage message) {
        HBox messageBox = this.messageViews.remove(message);
        if (messageBox != null) {
            this.messageContainer.getChildren().remove(messageBox);
        }
    }

    /**
     * Déclenche une notification pour un nouveau message reçu.
     * Affiche une notification avec le nom du joueur et le contenu du message.
     * Permet à l'utilisateur de cliquer sur la notification pour ouvrir la fenêtre de chat.
     *
     * @param inChatWith Le joueur avec lequel on discute.
     * @param message    Le message reçu.
     */
    public void triggerMessageNotification(OtherClientPlayer inChatWith, ChatMessage message) {
        Notifications.create()
                .title("Rise & Fall - Chat - " + inChatWith.getName())
                .text(message.getMessage())
                .onAction(event -> {
                    ChatStage.openWindow();
                    this.chatListView.getSelectionModel().select(inChatWith);
                })
                .show();
    }

    /**
     * Met à jour l'état du joueur pour indiquer s'il est éliminé.
     * Si le joueur est éliminé, désactive le champ de message et le bouton d'envoi,
     * et affiche un message d'information dans le champ de texte.
     *
     * @param eliminated Indique si le joueur est éliminé ou non.
     */
    public void setPlayerEliminated(boolean eliminated) {
        if (eliminated) {
            this.messageField.clear();
            this.messageField.setDisable(true);
            this.messageField.setPromptText("Vous avez été éliminé, vous ne pouvez plus envoyer de messages.");
            this.sendButton.setDisable(true);
        } else {
            this.messageField.setDisable(false);
            this.messageField.setPromptText("Entrez votre message ici...");
            this.sendButton.setDisable(false);
        }
    }

    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     * Utilisé pour informer l'utilisateur en cas de problème lors de l'envoi de messages.
     *
     * @param errorText Le texte du message d'erreur à afficher.
     */
    private void showErrorMessage(String errorText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(errorText);
            alert.showAndWait();
        });
    }
}