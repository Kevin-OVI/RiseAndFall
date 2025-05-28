package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;

public interface IRawHandler {
    /**
     * Gère un paquet directement à partir d'un flux de données, sans passer par un objet IPacket.
     *
     * @param sender     L'objet SocketWrapper qui représente l'expéditeur du paquet.
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    void deserialize(SocketWrapper sender, ReadHelper readHelper) throws IOException;
}
