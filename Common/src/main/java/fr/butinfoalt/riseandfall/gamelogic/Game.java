package fr.butinfoalt.riseandfall.gamelogic;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

/**
 * Représente une partie de jeu.
 * Chaque partie a :
 * - un nom
 * - un intervalle de tours
 * - un nombre minimum et maximum de joueurs
 * - indication de partie privée
 * - un état (en attente, en cours, terminée)
 * - un tour actuel
 * - une liste de joueurs
 */
public class Game {
    /**
     * Nom de la partie.
     */
    private final String name;
    /**
     * Intervalle entre chaque tour (en minutes).
     */
    private final int turnInterval;
    /**
     * Nombre minimum de joueurs pour commencer la partie.
     */
    private final int minPlayers;
    /**
     * Nombre maximum de joueurs dans la partie.
     */
    private final int maxPlayers;
    /**
     * Indique si la partie est privée ou publique.
     */
    private final boolean isPrivate;

    /**
     * État de la partie (en attente, en cours, terminée).
     */
    private GameState state;

    /**
     * Timestamp du dernier tour.
     */
    private Timestamp lastTurnTimestamp;

    /**
     * Tour actuel de la partie.
     */
    private int currentTurn;

    /**
     * Liste des joueurs dans la partie.
     */
    private final Set<Player> players;

    /**
     * Constructeur de la classe Game.
     *
     * @param name         Nom de la partie.
     * @param turnInterval Intervalle entre chaque tour (en minutes).
     * @param minPlayers   Nombre minimum de joueurs pour commencer la partie.
     * @param maxPlayers   Nombre maximum de joueurs dans la partie.
     * @param isPrivate    Indique si la partie est privée ou publique.
     * @param state        État de la partie (en attente, en cours, terminée).
     * @param currentTurn  Tour actuel de la partie.
     * @param players      Liste des joueurs dans la partie.
     */
    public Game(String name, int turnInterval, int minPlayers, int maxPlayers, boolean isPrivate, GameState state, Timestamp lastTurnTimestamp, int currentTurn, Set<Player> players) {
        this.name = name;
        this.turnInterval = turnInterval;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.state = state;
        this.lastTurnTimestamp = lastTurnTimestamp;
        this.currentTurn = currentTurn;
        this.players = players;
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
     * Méthode pour obtenir le nombre minimum de joueurs pour commencer la partie.
     *
     * @return Le nombre minimum de joueurs pour commencer la partie.
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Méthode pour obtenir le nombre maximum de joueurs dans la partie.
     *
     * @return Le nombre maximum de joueurs dans la partie.
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Méthode pour savoir si la partie est privée ou publique.
     *
     * @return true si la partie est privée, false sinon.
     */
    public boolean isPrivate() {
        return this.isPrivate;
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
     * Méthode pour démarrer la partie.
     * La partie ne peut être démarrée que si elle est en attente et qu'il y a suffisamment de joueurs.
     *
     * @throws IllegalStateException Si la partie n'est pas en attente ou s'il n'y a pas assez de joueurs.
     */
    public void start() throws IllegalStateException {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot start a game that is not in waiting state.");
        }
        if (this.players.size() < this.minPlayers) {
            throw new IllegalStateException("Cannot start this game with less than %d players.".formatted(this.minPlayers));
        }
        this.state = GameState.RUNNING;
    }

    /**
     * Méthode pour terminer la partie.
     * La partie ne peut être terminée que si elle est en cours.
     *
     * @throws IllegalStateException Si la partie n'est pas en cours.
     */
    public void end() throws IllegalStateException {
        if (this.state != GameState.RUNNING) {
            throw new IllegalStateException("Cannot end a game that is not running.");
        }
        this.state = GameState.ENDED;
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
     * Méthode pour passer au tour suivant.
     * Exécute les ordres de chaque joueur et incrémente le tour actuel.
     */
    public void nextTurn() {
        for (Player player : this.players) {
            player.executeOrders();
        }
        this.currentTurn++;
        this.lastTurnTimestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Méthode pour obtenir la liste des joueurs dans la partie.
     *
     * @return La liste des joueurs dans la partie.
     */
    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    /**
     * Méthode pour ajouter un joueur à la partie.
     * Un joueur ne peut être ajouté que si la partie est en attente et qu'il y a de la place.
     *
     * @param player Le joueur à ajouter.
     * @throws IllegalStateException Si la partie n'est pas en attente ou si elle est pleine.
     */
    public void addPlayer(Player player) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot add player to a game that has already started.");
        }
        if (this.players.size() >= this.maxPlayers) {
            throw new IllegalStateException("Cannot add player, game is full.");
        }
        this.players.add(player);
    }

    /**
     * Méthode pour retirer un joueur de la partie.
     * Un joueur ne peut être retiré que si la partie est en attente.
     *
     * @param player Le joueur à retirer.
     * @throws IllegalStateException Si la partie n'est pas en attente.
     */
    public void removePlayer(Player player) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot remove player from a game that has already started.");
        }
        this.players.remove(player);
    }
}
