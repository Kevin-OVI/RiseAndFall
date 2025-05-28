package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.network.common.IDeserializer;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.util.List;

/**
 * Paquet envoyé au client contenant la liste des parties en attente.
 * Il est utilisé pour informer le client des parties disponibles auxquelles il peut se joindre.
 */
public class PacketWaitingGames<G extends Game> implements IPacket {
    /**
     * Liste des parties en attente.
     * Chaque partie est représentée par un objet Game.
     */
    private final List<G> waitingGames;

    /**
     * Constructeur du paquet de parties en attente.
     *
     * @param waitingGames Liste des parties en attente.
     */
    public PacketWaitingGames(List<G> waitingGames) {
        this.waitingGames = waitingGames;
    }

    public PacketWaitingGames(ReadHelper readHelper, IDeserializer<G> gameDeserializer) throws IOException {
        this.waitingGames = readHelper.readSerializableList(gameDeserializer);
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeSerializableList(this.waitingGames);
    }

    /**
     * Récupère la liste des parties en attente
     *
     * @return La liste des parties en attente
     */
    public List<G> getWaitingGames() {
        return this.waitingGames;
    }
}
