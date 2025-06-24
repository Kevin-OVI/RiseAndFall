package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackResult;
import fr.butinfoalt.riseandfall.gamelogic.data.DataDeserializer;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Packet envoyé par le serveur au client pour informer des résultats des attaques d'un tour.
 */
public class PacketTurnResults implements IPacket {
    /**
     * Le numéro du tour pour lequel les résultats sont envoyés.
     */
    private final int turn;

    /**
     * La liste des résultats des attaques impliquant le joueur durant ce tour.
     */
    private final List<AttackResult> attackResults;

    /**
     * La liste des joueurs éliminés durant ce tour.
     */
    private final List<Player> eliminatedPlayers;

    /**
     * Constructeur du packet pour les résultats d'un tour.
     *
     * @param turn              Le numéro du tour.
     * @param attackResults     La liste des résultats des attaques.
     * @param eliminatedPlayers La liste des joueurs éliminés durant ce tour.
     */
    public PacketTurnResults(int turn, List<AttackResult> attackResults, List<Player> eliminatedPlayers) {
        this.turn = turn;
        this.attackResults = attackResults;
        this.eliminatedPlayers = eliminatedPlayers;
    }

    /**
     * Constructeur pour la désérialisation du packet.
     *
     * @param readHelper       L'outil de lecture pour désérialiser les données du packet.
     * @param dataDeserializer Le désérialiseur de données pour obtenir les joueurs par leur identifiant.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public PacketTurnResults(ReadHelper readHelper, DataDeserializer dataDeserializer) throws IOException {
        this.turn = readHelper.readInt();
        this.attackResults = readHelper.readSerializableList(AttackResult::new, dataDeserializer);
        int eliminatedPlayersCount = readHelper.readInt();
        this.eliminatedPlayers = new ArrayList<>(eliminatedPlayersCount);
        for (int i = 0; i < eliminatedPlayersCount; i++) {
            this.eliminatedPlayers.add(dataDeserializer.getPlayerById(readHelper.readInt()));
        }
    }

    /**
     * Sérialise les données du packet pour l'envoi.
     *
     * @param writeHelper L'outil d'écriture pour sérialiser les données du packet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.turn);
        writeHelper.writeSerializableList(this.attackResults);
        writeHelper.writeInt(this.eliminatedPlayers.size());
        for (Player player : this.eliminatedPlayers) {
            writeHelper.writeInt(player.getId());
        }
    }

    /**
     * Obtient le numéro du tour pour lequel les résultats sont envoyés.
     *
     * @return Le numéro du tour.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Obtient la liste des résultats des attaques impliquant le joueur durant ce tour.
     *
     * @return La liste des résultats des attaques.
     */
    public List<AttackResult> getAttackResults() {
        return attackResults;
    }

    /**
     * Obtient la liste des joueurs éliminés durant ce tour.
     *
     * @return La liste des joueurs éliminés.
     */
    public List<Player> getEliminatedPlayers() {
        return eliminatedPlayers;
    }
}
