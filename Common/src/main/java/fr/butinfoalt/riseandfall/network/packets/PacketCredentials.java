package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet d'authentification envoyé au serveur pour s'authentifier (login ou register).
 * Il contient le nom d'utilisateur et le mot de passe haché.
 */
public abstract class PacketCredentials implements IPacket {
    /**
     * Nom d'utilisateur du client
     */
    private final String username;
    /**
     * Mot de passe haché du client
     */
    private final byte[] passwordHash;

    /**
     * Constructeur du paquet d'authentification
     *
     * @param username     Le nom d'utilisateur du client
     * @param passwordHash Le mot de passe haché du client
     */
    public PacketCredentials(String username, byte[] passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Constructeur du paquet d'authentification pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketCredentials(ReadHelper readHelper) throws IOException {
        this.username = readHelper.readString();
        this.passwordHash = readHelper.readSizedByteArray();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeString(this.username);
        writeHelper.writeSizedByteArray(this.passwordHash);
    }

    /**
     * Récupère le nom d'utilisateur du client
     *
     * @return Le nom d'utilisateur du client
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Récupère le mot de passe haché du client
     *
     * @return Le mot de passe haché du client
     */
    public byte[] getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Classe représentant un paquet de connexion.
     */
    public static class PacketLogin extends PacketCredentials {
        public PacketLogin(String username, byte[] passwordHash) {
            super(username, passwordHash);
        }

        public PacketLogin(ReadHelper readHelper) throws IOException {
            super(readHelper);
        }
    }

    /**
     * Classe représentant un paquet d'enregistrement.
     */
    public static class PacketRegister extends PacketCredentials {
        public PacketRegister(String username, byte[] passwordHash) {
            super(username, passwordHash);
        }

        public PacketRegister(ReadHelper readHelper) throws IOException {
            super(readHelper);
        }
    }
}