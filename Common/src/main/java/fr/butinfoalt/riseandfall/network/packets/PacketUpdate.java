package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.File;
import java.io.IOException;

/**
 * Packet qui contient le fichier de mise à jour du client.
 */
public class PacketUpdate implements IPacket {
    /**
     * Fichier de mise à jour du client
     */
    private final File updateJarFile;

    /**
     * Constructeur du paquet de mise à jour
     *
     * @param updateJarFile Fichier de mise à jour du client
     */
    public PacketUpdate(File updateJarFile) {
        this.updateJarFile = updateJarFile;
    }

    /**
     * Constructeur du paquet de mise à jour à partir d'un helper de lecture
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketUpdate(ReadHelper readHelper) throws IOException {
        this.updateJarFile = readHelper.readFile();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeFile(this.updateJarFile);
    }

    /**
     * Récupère le fichier de mise à jour du client
     *
     * @return Le fichier de mise à jour du client
     */
    public File getUpdateJarFile() {
        return this.updateJarFile;
    }
}
