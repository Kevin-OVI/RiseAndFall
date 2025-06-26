package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;

public class ChatMessage {
    private final Player sender;
    private final Player receiver;
    private final String message;
    private long nonce;
    private final long timestamp;

    public ChatMessage(Player sender, Player receiver, String message, long nonce) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.nonce = nonce;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructeur de la classe ChatMessage à partir des valeurs de chaque champ.
     * Il est utilisé sur le serveur au moment de charger les données depuis la base de données.
     *
     * @param sender    Le joueur qui a envoyé le message.
     * @param receiver  Le joueur qui a reçu le message.
     * @param message   Le contenu du message.
     * @param nonce     Le nonce du message, utilisé pour éviter les doublons et vérifier s'il s'agit d'un nouveau message.
     * @param timestamp L'horodatage du message, en millisecondes depuis l'époque Unix.
     */
    public ChatMessage(Player sender, Player receiver, String message, long nonce, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public long getNonce() {
        return nonce;
    }

    public void resetNonce() {
        this.nonce = -1;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
