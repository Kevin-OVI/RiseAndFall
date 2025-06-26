package fr.butinfoalt.riseandfall.front.chat;

import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.SortedSet;

/**
 * Classe interne pour représenter une cellule de la ListView des joueurs.
 * Affiche le nom du joueur et le dernier message échangé.
 * Gère le style en fonction de l'état du joueur (éliminé ou non).
 */
class ChatListCell extends ListCell<OtherClientPlayer> {
    /**
     * Définit le style de la cellule en fonction de l'état du joueur.
     *
     * @param otherPlayer Le joueur dont les informations doivent être affichées.
     * @param empty       Indique si la cellule est vide.
     */
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
            String textColor = otherPlayer.isEliminated() ? "#aaa" : "#fff";
            nameLabel.setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold;");

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
