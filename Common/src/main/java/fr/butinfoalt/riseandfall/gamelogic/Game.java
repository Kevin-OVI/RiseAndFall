package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Classe abstraite représentant une partie du jeu.
 * Elle contient les informations de base sur la partie, telles que l'identifiant, le nom, l'intervalle entre les tours,
 * l'état de la partie, le timestamp du dernier tour et le tour actuel.
 */
public abstract class Game implements Identifiable, ISerializable {
    /**
     * Identifiant de la partie dans la base de données.
     */
    private final int id;
    /**
     * Nom de la partie.
     */
    protected final String name;
    /**
     * Intervalle entre chaque tour (en minutes).
     */
    protected final int turnInterval;
    /**
     * État de la partie (en attente, en cours, terminée).
     */
    protected GameState state;
    /**
     * Timestamp du dernier tour.
     */
    protected Timestamp lastTurnTimestamp;
    /**
     * Tour actuel de la partie.
     */
    protected int currentTurn;

    /**
     * Constructeur de la classe Game.
     *
     * @param id                Identifiant de la partie dans la base de données.
     * @param name              Nom de la partie.
     * @param turnInterval      Intervalle entre chaque tour (en minutes).
     * @param state             État de la partie (en attente, en cours, terminée).
     * @param lastTurnTimestamp Timestamp du dernier tour.
     * @param currentTurn       Tour actuel de la partie.
     */
    public Game(int id, String name, int turnInterval, GameState state, Timestamp lastTurnTimestamp, int currentTurn) {
        this.id = id;
        this.name = name;
        this.turnInterval = turnInterval;
        this.state = state;
        this.lastTurnTimestamp = lastTurnTimestamp;
        this.currentTurn = currentTurn;
    }

    public Game(ReadHelper readHelper) throws IOException {
        this.id = readHelper.readInt();
        this.name = readHelper.readString();
        this.turnInterval = readHelper.readInt();
        this.state = GameState.values()[readHelper.readInt()];
        long ts = readHelper.readLong();
        this.lastTurnTimestamp = ts == -1 ? null : new Timestamp(ts);
        this.currentTurn = readHelper.readInt();
    }

    /**
     * Méthode pour obtenir l'identifiant de la partie.
     *
     * @return L'identifiant de la partie.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Méthode pour obtenir le nom de la partie.
     *
     * @return Le nom de la partie.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Méthode pour obtenir l'intervalle entre chaque tour.
     *
     * @return L'intervalle entre chaque tour (en minutes).
     */
    public int getTurnInterval() {
        return this.turnInterval;
    }

    /**
     * Méthode pour obtenir l'état de la partie.
     *
     * @return L'état de la partie (en attente, en cours, terminée).
     */
    public GameState getState() {
        return this.state;
    }

    /**
     * Méthode pour obtenir le timestamp du dernier tour.
     *
     * @return Le timestamp du dernier tour.
     */
    public Timestamp getLastTurnTimestamp() {
        return this.lastTurnTimestamp;
    }

    /**
     * Méthode pour obtenir le temps restant avant le prochain tour.
     *
     * @return Le temps restant avant le prochain tour (en millisecondes).
     */
    public int timeUntilNextTurn() {
        if (this.lastTurnTimestamp == null) {
            return 0;
        }
        return (int) (this.lastTurnTimestamp.getTime() + this.turnInterval * 60 * 1000 - System.currentTimeMillis());
    }

    /**
     * Méthode pour obtenir le tour actuel de la partie.
     *
     * @return Le tour actuel de la partie.
     */
    public int getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Sérialise les données modifiables de la partie.
     *
     * @param writeHelper L'instance de WriteHelper utilisée pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void serializeModifiableData(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.state.ordinal());
        writeHelper.writeLong(this.lastTurnTimestamp == null ? -1 : this.lastTurnTimestamp.getTime());
        writeHelper.writeInt(this.currentTurn);
    }

    /**
     * Désérialise et met à jour les données modifiables de la partie.
     *
     * @param readHelper L'instance de ReadHelper utilisée pour lire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void updateModifiableData(ReadHelper readHelper) throws IOException {
        this.state = GameState.values()[readHelper.readInt()];
        long lastTurnTimestampValue = readHelper.readLong();
        this.lastTurnTimestamp = lastTurnTimestampValue == -1 ? null : new Timestamp(lastTurnTimestampValue);
        this.currentTurn = readHelper.readInt();
    }

    /**
     * Méthode pour sérialiser les données de la partie dans un flux de sortie.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeString(this.name);
        writeHelper.writeInt(this.turnInterval);
        writeHelper.writeInt(this.state.ordinal());
        writeHelper.writeLong(this.lastTurnTimestamp == null ? -1 : this.lastTurnTimestamp.getTime());
        writeHelper.writeInt(this.currentTurn);
    }
}
