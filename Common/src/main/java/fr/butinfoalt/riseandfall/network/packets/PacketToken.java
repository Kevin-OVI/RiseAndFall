package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au client pour lui fournir un token d'authentification,
 * ou au serveur pour s'authentifier avec un token précédemment reçu.
 * Il contient le token d'authentification.
 */
public class PacketToken implements IPacket {
    /**
     * Token d'authentification
     */
    private final String token;

    /**
     * Constructeur du paquet de token
     *
     * @param token Le token d'authentification
     */
    public PacketToken(String token) {
        this.token = token;
    }

    /**
     * Constructeur du paquet de token pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketToken(ReadHelper readHelper) throws IOException {
        this.token = readHelper.readString();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.token);
    }

    /**
     * Récupère le token d'authentification
     *
     * @return Le token d'authentification
     */
    public String getToken() {
        return this.token;
    }
}
