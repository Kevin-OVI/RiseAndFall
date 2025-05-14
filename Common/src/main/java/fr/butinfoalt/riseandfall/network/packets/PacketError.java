package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au serveur pour s'authentifier.
 * Il contient le nom d'utilisateur et le mot de passe haché.
 */
public class PacketError implements IPacket {
    /**
     * Nom d'utilisateur du client
     */
    private final String error;
    /**
     * Mot de passe haché du client
     */
    private final String category;


    public PacketError(String error, String category) {
        this.error = error;
        this.category = category;
    }

    /**
     * Constructeur du paquet d'authentification pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketError(ReadHelper readHelper) throws IOException {
        this.error = readHelper.readString();
        this.category = readHelper.readString();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.error);
        writeHelper.writeString(this.category);
    }

    /**
     * Récupère le nom d'utilisateur du client
     *
     * @return Le nom d'utilisateur du client
     */
    public String getError() {
        return this.error;
    }

    /**
     * Récupère le mot de passe haché du client
     *
     * @return Le mot de passe haché du client
     */
    public String getCategory() {
        return this.category;
    }
}
