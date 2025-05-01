package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.Version;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Packet qui contient les informations d'initialisation du client, c'est-à-dire sa version et le nom d'utilisateur.
 */
public class PacketInitInfo implements IPacket {
    /**
     * Version du client
     */
    private final Version clientVersion;
    /**
     * Nom d'utilisateur du client
     */
    private final String username;

    /**
     * Constructeur du paquet d'initialisation
     *
     * @param clientVersion Version du client
     * @param username      Nom d'utilisateur du client
     */
    public PacketInitInfo(Version clientVersion, String username) {
        this.clientVersion = clientVersion;
        this.username = username;
    }

    /**
     * Constructeur du paquet d'initialisation à partir d'un helper de lecture
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketInitInfo(ReadHelper readHelper) throws IOException {
        this.clientVersion = new Version(readHelper);
        this.username = readHelper.readString();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        this.clientVersion.toBytes(writeHelper);
        writeHelper.writeString(this.username);
    }

    /**
     * Récupère la version du client
     *
     * @return La version du client
     */
    public Version getClientVersion() {
        return this.clientVersion;
    }

    /**
     * Récupère le nom d'utilisateur du client
     *
     * @return Le nom d'utilisateur du client
     */
    public String getUsername() {
        return this.username;
    }
}
