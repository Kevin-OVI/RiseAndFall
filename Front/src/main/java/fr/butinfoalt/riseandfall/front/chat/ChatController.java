package fr.butinfoalt.riseandfall.front.chat;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.Chat;
import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.network.packets.PacketAuthentification;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatController implements ViewController {

    @FXML
    private ListView<Chat> chatListView;

    @FXML
    private Label receiverLabel;

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageField;

    private Chat currentChat;
    private Map<Integer, Chat> chats = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    @FXML
    public void initialize() {
        chatListView.setCellFactory(listView -> new ChatListCell());
        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldChat, newChat) -> {
            if (newChat != null) {
                openChat(newChat);
            }
        });
        chatListView.setStyle("-fx-background-color: #1a1a1a; -fx-background-insets: 0; -fx-padding: 0;");
    }

    private void openChat(Chat chat) {
        currentChat = chat;
        receiverLabel.setText(((OtherClientPlayer)chat.getReceiver()).getName());

        messageContainer.getChildren().clear();

        for (ChatMessage message : chat.getMessages()) {
            addMessageToView(message);
        }

        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    @FXML
    private void sendMessage() {
        if (currentChat == null || messageField.getText().trim().isEmpty()) {
            return;
        }

        String messageText = messageField.getText().trim();
        ChatMessage newMessage = new ChatMessage(currentChat, RiseAndFall.getPlayer(), messageText);

        try {
            RiseAndFall.getClient().sendPacket(new PacketMessage(newMessage.getChat().getId(), RiseAndFall.getPlayer().getId(), messageText, newMessage.getTimestamp()));
            currentChat.addMessage(newMessage);
            addMessageToView(newMessage);
            messageField.clear();
            Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
        } catch (IOException e) {
            LogManager.logError("Impossible d'envoyer le packet de message", e);
            showErrorMessage("Erreur lors de l'envoi du message.");
            return;
        }
    }

    private void addMessageToView(ChatMessage message) {
        HBox messageBox = new HBox(10);
        messageBox.setPadding(new Insets(5));

        boolean isOwnMessage = message.getSender().equals(RiseAndFall.getPlayer());

        // Bulle de message
        VBox bubble = new VBox(5);
        bubble.setMaxWidth(400);
        bubble.setPadding(new Insets(10));
        bubble.setStyle(isOwnMessage ?
                "-fx-background-color: white; -fx-background-radius: 15;" :
                "-fx-background-color: #e0e0e0; -fx-background-radius: 15;");

        // Nom de l'expéditeur (seulement pour les messages reçus)
        if (!isOwnMessage) {
            Label senderLabel = new Label(((OtherClientPlayer)message.getSender()).getName());
            senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #666;");
            bubble.getChildren().add(senderLabel);
        }

        // Texte du message
        Label messageText = new Label(message.getMessage());
        messageText.setWrapText(true);
        messageText.setMaxWidth(380);
        bubble.getChildren().add(messageText);

        // Heure
        Label timeLabel = new Label(dateFormat.format(new Date(message.getTimestamp())));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        bubble.getChildren().add(timeLabel);

        // Espaceur pour aligner les messages
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (isOwnMessage) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.getChildren().addAll(spacer, bubble);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.getChildren().addAll(bubble, spacer);
        }

        messageContainer.getChildren().add(messageBox);
    }

    @FXML
    private void closeChat() {
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);

        loadData();
    }

    private class ChatListCell extends ListCell<Chat> {
        @Override
        protected void updateItem(Chat chat, boolean empty) {
            super.updateItem(chat, empty);

            if (empty || chat == null) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: #1a1a1a;");
            } else {
                VBox cellContent = new VBox(3);
                cellContent.setPadding(new Insets(5));

                Label nameLabel = new Label(((OtherClientPlayer)chat.getReceiver()).getName());
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                String lastMessage = "";
                if (!chat.isEmpty()) {
                    ChatMessage[] messages = chat.getMessages();
                    lastMessage = messages[messages.length - 1].getMessage();
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

    private void loadData() {
        chatListView.getItems().clear();
        Collection<Chat> chats = RiseAndFall.getGame().getChats();
        chatListView.getItems().addAll(chats);
        for (Chat chat : chats) {
            this.chats.put(chat.getId(), chat);
        }
    }

    public void addChat(Chat chat) {
        chats.put(chat.getId(), chat);
        chatListView.getItems().add(chat);
    }

    public void receiveMessage(ChatMessage message) {
        // Cette méthode est maintenant appelée depuis Platform.runLater() dans RiseAndFallClient
        Chat chat = message.getChat();
        if (chat.equals(currentChat)) {
            addMessageToView(message);
            Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
        }
        chatListView.refresh();
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