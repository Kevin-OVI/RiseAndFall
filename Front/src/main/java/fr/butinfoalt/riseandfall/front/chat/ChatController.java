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

public class ChatController {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

    private final SortedMap<ChatMessage, HBox> messageViews = new TreeMap<>(Comparator.comparingLong(ChatMessage::getTimestamp));
    private OtherClientPlayer currentlyChattingWith;

    @FXML
    private ListView<OtherClientPlayer> chatListView;

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageField;

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

    private void openChat(OtherClientPlayer otherPlayer) {
        this.currentlyChattingWith = otherPlayer;
        ChatStage.setTitleExtra(otherPlayer.getName());

        this.messageContainer.getChildren().clear();
        this.messageViews.clear();

        for (ChatMessage message : otherPlayer.getMessages()) {
            this.createMessageBox(message);
        }
        this.updateDisplayedMessages();

        Platform.runLater(() -> this.messageScrollPane.setVvalue(1.0));
    }

    @FXML
    private void sendMessage() {
        if (this.currentlyChattingWith == null || this.messageField.getText().trim().isEmpty()) {
            return;
        }

        String messageText = this.messageField.getText().trim();
        long nonce = System.currentTimeMillis();
        ChatMessage newMessage = new ChatMessage(RiseAndFall.getPlayer(), this.currentlyChattingWith, messageText, nonce);
        this.currentlyChattingWith.addSendingMessage(newMessage);
        this.messageField.clear();

        try {
            RiseAndFall.getClient().sendPacket(new PacketMessage(RiseAndFall.getPlayer().getId(), newMessage.getReceiver().getId(), messageText, nonce, newMessage.getTimestamp()));
        } catch (IOException e) {
            LogManager.logError("Impossible d'envoyer le packet de message", e);
            showErrorMessage("Erreur lors de l'envoi du message.");
        }
    }

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

    private void updateDisplayedMessages() {
        this.messageContainer.getChildren().setAll(this.messageViews.sequencedValues());
        Platform.runLater(() -> this.messageScrollPane.setVvalue(1.0));
    }

    private void createMessageBoxAndUpdate(ChatMessage message) {
        this.createMessageBox(message);
        this.updateDisplayedMessages();
    }

    public void loadData() {
        this.clearState();

        for (OtherClientPlayer otherPlayer : RiseAndFall.getGame().getOtherPlayers()) {
            this.chatListView.getItems().add(otherPlayer);
        }
    }

    public void clearState() {
        this.currentlyChattingWith = null;
        this.messageContainer.getChildren().clear();
        this.messageField.clear();
        this.chatListView.getItems().clear();
    }

    public void onAddMessage(OtherClientPlayer inChatWith, ChatMessage message) {
        if (inChatWith.equals(this.currentlyChattingWith)) {
            this.createMessageBoxAndUpdate(message);
        }
    }

    public void onRemoveMessage(OtherClientPlayer inChatWith, ChatMessage message) {
        HBox messageBox = this.messageViews.remove(message);
        if (messageBox != null) {
            this.messageContainer.getChildren().remove(messageBox);
        }
    }

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

    private static class ChatListCell extends ListCell<OtherClientPlayer> {
        @Override
        protected void updateItem(OtherClientPlayer otherPlayer, boolean empty) {
            super.updateItem(otherPlayer, empty);

            if (empty || otherPlayer == null) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: #1a1a1a;");
            } else {
                VBox cellContent = new VBox(3);
                cellContent.setPadding(new Insets(5));

                Label nameLabel = new Label(otherPlayer.getName());
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                String lastMessage = "";
                SortedSet<ChatMessage> messages = otherPlayer.getMessages();
                if (!messages.isEmpty()) {
                    lastMessage = messages.getLast().getMessage();
                    if (lastMessage.length() > 30) {
                        lastMessage = lastMessage.substring(0, 30) + "...";
                    }
                }

                Label lastMessageLabel = new Label(lastMessage);
                lastMessageLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

                cellContent.getChildren().addAll(nameLabel, lastMessageLabel);
                setGraphic(cellContent);

                setStyle("-fx-background-color: " + (isSelected() ? "#333" : "#1a1a1a") + "; -fx-padding: 5;");
            }
        }
    }

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