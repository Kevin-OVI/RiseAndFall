package fr.butinfoalt.riseandfall.server.data;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.server.RiseAndFallServer;
import fr.butinfoalt.riseandfall.server.ServerPlayer;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Représente une partie de jeu.
 * Chaque partie a :
 * - un identifiant
 * - un nom
 * - un intervalle de tours
 * - un nombre minimum et maximum de joueurs
 * - indication de partie privée
 * - un état (en attente, en cours, terminée)
 * - un tour actuel
 * - une liste de joueurs
 */
public class ServerGame extends Game {
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
     * Map des joueurs dans la partie.
     * Associe l'identifiant de l'utilisateur à l'objet ServerPlayer.
     */
    private final Map<Integer, ServerPlayer> players = new HashMap<>();

    /**
     * Timer pour gérer le temps avant le demarrage de la partie ou entre les tours.
     * Il peut être utilisé pour démarrer un compte à rebours ou gérer les tours.
     */
    public Timer startTimer;

    /**
     * Constructeur de la classe Game.
     *
     * @param id           Identifiant de la partie dans la base de données.
     * @param name         Nom de la partie.
     * @param turnInterval Intervalle entre chaque tour (en minutes).
     * @param minPlayers   Nombre minimum de joueurs pour commencer la partie.
     * @param maxPlayers   Nombre maximum de joueurs dans la partie.
     * @param isPrivate    Indique si la partie est privée ou publique.
     * @param state        État de la partie (en attente, en cours, terminée).
     * @param currentTurn  Tour actuel de la partie.
     */
    public ServerGame(int id, String name, int turnInterval, int minPlayers, int maxPlayers, boolean isPrivate, GameState state, Timestamp lastTurnTimestamp, int currentTurn) {
        super(id, name, turnInterval, state, lastTurnTimestamp, currentTurn);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
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
     * Méthode pour passer au tour suivant.
     * Exécute les ordres de chaque joueur et incrémente le tour actuel.
     *
     * @throws IllegalStateException Si la partie n'est pas en cours.
     */
    public void nextTurn() throws IllegalStateException {
        if (this.state != GameState.RUNNING) {
            throw new IllegalStateException("Cannot proceed to the next turn when the game is not running.");
        }
        for (ServerPlayer player : this.players.values()) {
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
    public Collection<ServerPlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players.values());
    }

    /**
     * Méthode pour obtenir un joueur à partir d'un utilisateur.
     *
     * @param user L'utilisateur dont on veut obtenir le joueur.
     * @return Le joueur correspondant à l'utilisateur, ou null si l'utilisateur ne joue pas dans cette partie.
     */
    public ServerPlayer getPlayerFor(User user) {
        return this.players.get(user.getId());
    }

    /**
     * Méthode pour ajouter un joueur à la partie.
     * Un joueur ne peut être ajouté que si la partie est en attente et qu'il y a de la place.
     *
     * @param player Le joueur à ajouter.
     * @throws IllegalStateException Si la partie n'est pas en attente ou si elle est pleine.
     */
    public void addPlayer(ServerPlayer player) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot add player to a game that has already started.");
        }
        if (this.players.size() >= this.maxPlayers) {
            throw new IllegalStateException("Cannot add player, game is full.");
        }

        this.players.put(player.getUser().getId(), player);
    }

    /**
     * Méthode pour ajouter un joueur de manière forcée à la partie, sans vérifier son état.
     * Cette méthode est utilisée lors du chargement des joueurs d'une partie depuis la base de données, voir {@link RiseAndFallServer#loadServerData()}
     * Ne pas utiliser en fonctionnement normal pour ajouter des joueurs pendant le jeu, car cela pourrait violer les règles de la partie et causer des incohérences avec les joueurs déjà présents.
     *
     * @param player Le joueur à ajouter.
     */
    public void forceAddPlayer(ServerPlayer player) {
        this.players.put(player.getUser().getId(), player);
    }

    /**
     * Méthode pour retirer un joueur de la partie.
     * Un joueur ne peut être retiré que si la partie est en attente.
     *
     * @param user L'utilisateur dont on veut retirer le joueur.
     * @return Le joueur retiré, ou null si l'utilisateur ne joue pas dans cette partie.
     */
    public ServerPlayer removePlayer(User user) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot remove player from a game that has already started.");
        }

        return this.players.remove(user.getId());
    }

    @Override
    public ToStringFormatter toStringFormatter() {
        return super.toStringFormatter()
                .add("minPlayers", this.minPlayers)
                .add("maxPlayers", this.maxPlayers)
                .add("isPrivate", this.isPrivate)
                .add("playersCount", this.players.size());
    }
}
