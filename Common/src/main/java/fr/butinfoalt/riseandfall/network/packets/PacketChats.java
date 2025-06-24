package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.Chat;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente un paquet de chat contenant un message.
 * Ce paquet pourra être utilisé pour envoyer des messages depuis le joueur
 */
public class PacketChats implements IPacket {
    /**
     * Liste des chats
     */
    private int id;
    private int receiverId;

    /**
     * Constructeur du paquet de chat
     *
     * @param id L'identifiant du chat
     * @param receiverId L'identifiant du joueur destinataire du chat
     *
     */
    public PacketChats(int id, int receiverId) {
        this.id = id;
        this.receiverId = receiverId;
    }

    /**
     * Constructeur du paquet de chat à partir d'un helper de lecture
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketChats(ReadHelper readHelper) throws IOException {
        this.id = readHelper.readInt();
        this.receiverId = readHelper.readInt();
    }


    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeInt(this.receiverId);
    }

    public int getReceriverId() {
        return this.receiverId;
    }

    public int getId() {
        return this.id;
    }
}
