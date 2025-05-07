package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au serveur pour créer ou rejoindre une partie. Il contient la race que choisi le joueur pour cette partie.
 */
public class PacketCreateOrJoinGame implements IPacket {
    /**
     * La race choisie par le joueur pour cette partie
     */
    private final Race chosenRace;

    /**
     * Constructeur du paquet de création ou de jointure de partie.
     *
     * @param chosenRace La race choisie par le joueur pour cette partie.
     */
    public PacketCreateOrJoinGame(Race chosenRace) {
        this.chosenRace = chosenRace;
    }

    /**
     * Constructeur du paquet de création ou de jointure de partie.
     * Utilisé pour la désérialisation du paquet.
     *
     * @param readHelper L'outil de lecture pour désérialiser le paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public PacketCreateOrJoinGame(ReadHelper readHelper) throws IOException {
        this.chosenRace = Identifiable.getById(ServerData.getRaces(), readHelper.readInt());
    }

    /**
     * Sérialise le paquet en un flux de données.
     *
     * @param writeHelper L'outil d'écriture pour sérialiser le paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.chosenRace.getId());
    }

    /**
     * Récupère la race choisie par le joueur
     *
     * @return La race choisie par le joueur
     */
    public Race getChosenRace() {
        return this.chosenRace;
    }
}
