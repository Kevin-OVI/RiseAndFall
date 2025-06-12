package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;

import java.io.IOException;

public class ChatMessage {
    private Chat chat;
    private Player sender;
    private String message;
    private long timestamp;

    public ChatMessage(Chat chat, Player sender, String message) {
        this.chat = chat;
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Chat getChat() {
        return chat;
    }

    /**
     * Constructeur de la classe ChatMessage à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param chat L'objet Chat auquel ce message appartient.
     * @param sender Le joueur qui a envoyé le message.
     * @param message Le contenu du message.
     * @param timestamp L'horodatage du message, en millisecondes depuis l'époque Unix.
     */
    public ChatMessage(Chat chat, Player sender, String message, long timestamp) {
        this.chat = chat;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }
}
