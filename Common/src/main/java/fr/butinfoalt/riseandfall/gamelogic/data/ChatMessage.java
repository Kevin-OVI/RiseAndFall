package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;

/**
 * Représente un message de chat entre deux joueurs.
 * Contient des informations sur l'expéditeur, le destinataire, le contenu du message,
 * un nonce pour éviter les doublons et un horodatage.
 */
public class ChatMessage {
    /**
     * Le joueur qui a envoyé le message.
     */
    private final Player sender;

    /**
     * Le joueur qui a reçu le message.
     */
    private final Player receiver;

    /**
     * Le contenu du message.
     */
    private final String message;

    /**
     * Un nonce pour identifier de manière unique le message.
     * Utilisé pour éviter les doublons et vérifier s'il s'agit d'un nouveau message.
     */
    private long nonce;

    /**
     * L'horodatage du message, en millisecondes depuis l'époque Unix.
     * Utilisé pour trier les messages et afficher l'heure d'envoi.
     */
    private final long timestamp;

    /**
     * Constructeur de la classe ChatMessage utilisé sur le client au moment de créer un nouveau message.
     *
     * @param sender      Le joueur qui envoie le message.
     * @param receiver    Le joueur qui reçoit le message.
     * @param message     Le contenu du message.
     * @param currentTime L'horodatage actuel du message, en millisecondes depuis l'époque Unix.
     */
    public ChatMessage(Player sender, Player receiver, String message, long currentTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.nonce = currentTime;
        this.timestamp = currentTime;
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

    /**
     * @return Le joueur qui a envoyé le message.
     */
    public Player getSender() {
        return sender;
    }

    /**
     * @return Le joueur qui a reçu le message.
     */
    public Player getReceiver() {
        return receiver;
    }

    /**
     * @return Le contenu du message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Le nonce du message, utilisé pour éviter les doublons et vérifier s'il s'agit d'un nouveau message.
     */
    public long getNonce() {
        return nonce;
    }

    /**
     * Supprime le nonce du message en le réinitialisant à -1.
     */
    public void resetNonce() {
        this.nonce = -1;
    }

    /**
     * @return L'horodatage du message, en millisecondes depuis l'époque Unix.
     */
    public long getTimestamp() {
        return timestamp;
    }
}
