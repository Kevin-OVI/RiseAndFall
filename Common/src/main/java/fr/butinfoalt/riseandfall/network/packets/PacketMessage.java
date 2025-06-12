package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.Chat;
import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente un paquet de chat contenant un message.
 * Ce paquet pourra être utilisé pour envoyer des messages depuis le joueur
 */
public class PacketMessage implements IPacket {
    private int chatId;
    private int playerId;
    private String message;
    private long timestamp;


    /**
     * Constructeur du paquet de chat
     *
     * @param message Le message à envoyer
     */
    public PacketMessage(int chatId, int playerId, String message, long timestamp) {
        this.chatId = chatId;
        this.playerId = playerId;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Constructeur du paquet de chat à partir d'un helper de lecture
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketMessage(ReadHelper readHelper) throws IOException {
        this.chatId = readHelper.readInt();
        this.playerId = readHelper.readInt();
        this.message = readHelper.readString();
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
        writeHelper.writeInt(this.chatId);
        writeHelper.writeInt(this.playerId);
        writeHelper.writeString(this.message);
        writeHelper.writeLong(this.timestamp);
    }

    /**
     * Récupère le message du paquet
     *
     * @return Le message du paquet
     */
    public String getMessage() {
        return this.message;
    }

    public int getChatId() {
        return chatId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
