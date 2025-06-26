package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente un paquet de chat contenant un message entre deux joueurs.
 * Envoyé au client et au serveur pour envoyer des messages de chat.
 */
public class PacketMessage implements IPacket {
    private final int senderId;
    private final int receiverId;
    private final String message;
    private final long nonce;
    private final long timestamp;

    /**
     * Constructeur du paquet de chat
     *
     * @param senderId   L'ID du joueur qui envoie le message
     * @param receiverId L'ID du joueur qui reçoit le message
     * @param message    Le contenu du message
     * @param nonce      Un nonce pour identifier de manière unique le message
     * @param timestamp  L'horodatage du message, en millisecondes depuis l'époque Unix
     */
    public PacketMessage(int senderId, int receiverId, String message, long nonce, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    /**
     * Constructeur du paquet de chat à partir d'un message
     *
     * @param message Le message à envoyer
     */
    public PacketMessage(ChatMessage message) {
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.message = message.getMessage();
        this.nonce = message.getNonce();
        this.timestamp = message.getTimestamp();
    }

    /**
     * Constructeur du paquet de chat pour la désérialisation
     *
     * @param readHelper L'outil de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketMessage(ReadHelper readHelper) throws IOException {
        this.senderId = readHelper.readInt();
        this.receiverId = readHelper.readInt();
        this.message = readHelper.readString();
        this.nonce = readHelper.readLong();
        this.timestamp = readHelper.readLong();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.senderId);
        writeHelper.writeInt(this.receiverId);
        writeHelper.writeString(this.message);
        writeHelper.writeLong(this.nonce);
        writeHelper.writeLong(this.timestamp);
    }

    /**
     * @return L'ID du joueur qui a envoyé le message
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * @return L'ID du joueur qui a reçu le message
     */
    public int getReceiverId() {
        return receiverId;
    }

    /**
     * @return Le contenu du message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return Le nonce du message, utilisé pour éviter les doublons et vérifier s'il s'agit d'un nouveau message
     */
    public long getNonce() {
        return this.nonce;
    }

    /**
     * @return L'horodatage du message, en millisecondes depuis l'époque Unix
     */
    public long getTimestamp() {
        return timestamp;
    }
}
