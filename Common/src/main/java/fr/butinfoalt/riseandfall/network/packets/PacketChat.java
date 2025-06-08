package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Représente un paquet de chat contenant un message.
 * Ce paquet pourra être utilisé pour envoyer des messages depuis le joueur
 */
public class PacketChat implements IPacket {
    /**
     * Le message du paquet
     */
    private final String message;

    /**
     * Constructeur du paquet de chat
     *
     * @param message Le message à envoyer
     */
    public PacketChat(String message) {
        this.message = message;
    }

    /**
     * Constructeur du paquet de chat à partir d'un helper de lecture
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketChat(ReadHelper readHelper) throws IOException {
        this.message = readHelper.readString();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.message);
    }

    /**
     * Récupère le message du paquet
     *
     * @return Le message du paquet
     */
    public String getMessage() {
        return this.message;
    }
}
