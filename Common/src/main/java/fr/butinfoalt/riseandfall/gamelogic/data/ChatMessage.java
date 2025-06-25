package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;

public class ChatMessage {
    private final Player sender;
    private final Player receiver;
    private final String message;
    private final long timestamp;

    public ChatMessage(Player sender, Player receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructeur de la classe ChatMessage à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param chat      L'objet Chat auquel ce message appartient.
     * @param sender    Le joueur qui a envoyé le message.
     * @param message   Le contenu du message.
     * @param timestamp L'horodatage du message, en millisecondes depuis l'époque Unix.
     */
    public ChatMessage(Player sender, Player receiver, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
