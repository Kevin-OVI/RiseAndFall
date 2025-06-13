package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au client lorsqu'il découvre un autre joueur.
 * Ce paquet contient les informations simples du joueur découvert, telles que son identifiant, sa race et son nom.
 */
public class PacketDiscoverPlayer implements IPacket {
    /**
     * Identifiant unique du joueur découvert.
     */
    private final int playerId;
    /**
     * La race du joueur découvert.
     */
    private final Race playerRace;
    /**
     * Le nom du joueur découvert.
     */
    private final String playerName;

    /**
     * Constructeur de la classe PacketDiscoverPlayer.
     * Ce constructeur est utilisé pour créer un paquet à envoyer au client.
     *
     * @param playerId   L'identifiant unique du joueur découvert.
     * @param playerRace La race du joueur découvert.
     * @param playerName Le nom du joueur découvert.
     */
    public PacketDiscoverPlayer(int playerId, Race playerRace, String playerName) {
        this.playerId = playerId;
        this.playerRace = playerRace;
        this.playerName = playerName;
    }

    /**
     * Constructeur de la classe PacketDiscoverPlayer.
     * Ce constructeur est utilisé pour désérialiser les données du paquet.
     *
     * @param readHelper L'outil de lecture pour désérialiser les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public PacketDiscoverPlayer(ReadHelper readHelper) throws IOException {
        this.playerId = readHelper.readInt();
        this.playerRace = Identifiable.getById(ServerData.getRaces(), readHelper.readInt());
        this.playerName = readHelper.readString();
    }

    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.playerId);
        writeHelper.writeInt(this.playerRace.getId());
        writeHelper.writeString(this.playerName);
    }

    /**
     * Méthode pour obtenir l'identifiant du joueur découvert.
     *
     * @return L'identifiant unique du joueur découvert.
     */
    public int getPlayerId() {
        return this.playerId;
    }

    /**
     * Méthode pour obtenir la race du joueur découvert.
     *
     * @return La race du joueur découvert.
     */
    public Race getPlayerRace() {
        return this.playerRace;
    }

    /**
     * Méthode pour obtenir le nom du joueur découvert.
     *
     * @return Le nom du joueur découvert.
     */
    public String getPlayerName() {
        return this.playerName;
    }
}
