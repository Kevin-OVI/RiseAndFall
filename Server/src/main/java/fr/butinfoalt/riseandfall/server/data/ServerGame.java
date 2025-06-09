package fr.butinfoalt.riseandfall.server.data;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;
import fr.butinfoalt.riseandfall.server.GameManager;
import fr.butinfoalt.riseandfall.server.RiseAndFallServer;
import fr.butinfoalt.riseandfall.server.ServerPlayer;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.sql.Timestamp;
import java.util.*;

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
     * Référence au serveur de jeu, pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

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
     * Tâche de minuterie pour contenir une action différée, comme le démarrage de la partie ou le passage au tour suivant.
     */
    public TimerTask delayedTask;

    /**
     * Constructeur de la classe Game.
     *
     * @param server       Référence au serveur de jeu, pour accéder aux fonctionnalités du serveur.
     * @param id           Identifiant de la partie dans la base de données.
     * @param name         Nom de la partie.
     * @param turnInterval Intervalle entre chaque tour (en minutes).
     * @param minPlayers   Nombre minimum de joueurs pour commencer la partie.
     * @param maxPlayers   Nombre maximum de joueurs dans la partie.
     * @param isPrivate    Indique si la partie est privée ou publique.
     * @param state        État de la partie (en attente, en cours, terminée).
     * @param currentTurn  Tour actuel de la partie.
     */
    public ServerGame(RiseAndFallServer server, int id, String name, int turnInterval, int minPlayers, int maxPlayers, boolean isPrivate, GameState state, Timestamp nextActionAt, int currentTurn) {
        super(id, name, turnInterval, state, nextActionAt, currentTurn);
        this.server = server;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
    }

    /**
     * Méthode pour obtenir le nombre minimum de joueurs pour commencer la partie.
     *
     * @return Le nombre minimum de joueurs pour commencer la partie.
     */
    public synchronized int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Méthode pour obtenir le nombre maximum de joueurs dans la partie.
     *
     * @return Le nombre maximum de joueurs dans la partie.
     */
    public synchronized int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Méthode pour savoir si la partie est privée ou publique.
     *
     * @return true si la partie est privée, false sinon.
     */
    public synchronized boolean isPrivate() {
        return this.isPrivate;
    }

    /**
     * Méthode pour démarrer la partie.
     * La partie ne peut être démarrée que si elle est en attente et qu'il y a suffisamment de joueurs.
     *
     * @throws IllegalStateException Si la partie n'est pas en attente ou s'il n'y a pas assez de joueurs.
     */
    public synchronized void start() throws IllegalStateException {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot start a game that is not in waiting state.");
        }
        if (this.players.size() < this.minPlayers) {
            throw new IllegalStateException("Cannot start this game with less than %d players.".formatted(this.minPlayers));
        }
        LogManager.logMessage("Démarrage de la partie %s avec %d joueurs.".formatted(this.name, this.players.size()));
        this.state = GameState.RUNNING;

        this.nextActionAt = new Timestamp(System.currentTimeMillis() + this.turnInterval * 60_000L);
        this.scheduleNextTurn();

        GameManager gameManager = this.server.getGameManager();
        gameManager.handleGameUpdate(this);
        gameManager.newRandomGame();
    }

    /**
     * Méthode pour terminer la partie.
     * La partie ne peut être terminée que si elle est en cours.
     *
     * @throws IllegalStateException Si la partie n'est pas en cours.
     */
    public synchronized void end() throws IllegalStateException {
        if (this.state != GameState.RUNNING) {
            throw new IllegalStateException("Cannot end a game that is not running.");
        }
        LogManager.logMessage("Fin de la partie %s.".formatted(this.name));
        this.state = GameState.ENDED;
        this.nextActionAt = null;
    }

    /**
     * Méthode pour planifier une action différée, comme le démarrage de la partie ou le passage au tour suivant.
     * Annule l'action précédente si elle existe et planifie la nouvelle action avec un délai calculé.
     *
     * @param logMessage Le message de log à afficher lors de la planification de l'action.
     * @param action     L'action à exécuter après le délai.
     */
    private synchronized void scheduleNextAction(String logMessage, Runnable action) {
        if (this.delayedTask != null) {
            this.delayedTask.cancel();
        }
        long delay = this.nextActionAt.getTime() - System.currentTimeMillis();
        LogManager.logMessage(logMessage.formatted(this.name, delay / 1000));
        if (delay <= 0) {
            this.delayedTask = null;
            action.run();
        } else {
            this.server.getTimer().schedule(this.delayedTask = new TimerTask() {
                @Override
                public void run() {
                    ServerGame.this.delayedTask = null;
                    action.run();
                }
            }, delay);
        }
    }

    /**
     * Méthode pour planifier le démarrage de la partie.
     * Utilise {@link #scheduleNextAction(String, Runnable)} pour planifier l'action avec un délai.
     */
    public void scheduleGameStart() {
        this.scheduleNextAction("Démarrage de la partie %s dans %d secondes.", this::start);
    }

    /**
     * Méthode pour planifier le passage au tour suivant.
     * Utilise {@link #scheduleNextAction(String, Runnable)} pour planifier l'action avec un délai.
     */
    public void scheduleNextTurn() {
        this.scheduleNextAction("Passage au tour suivant de la partie %s dans %d secondes.", this::nextTurn);
    }

    /**
     * Méthode pour passer au tour suivant.
     * Exécute les ordres de chaque joueur et incrémente le tour actuel.
     *
     * @throws IllegalStateException Si la partie n'est pas en cours.
     */
    public synchronized void nextTurn() throws IllegalStateException {
        if (this.state != GameState.RUNNING) {
            throw new IllegalStateException("Cannot proceed to the next turn when the game is not running.");
        }
        for (ServerPlayer player : this.players.values()) {
            player.executeOrders();
        }
        // TODO : Condition de Victoire pour arrêter la partie si nécessaire, pour le moment la partie ne s'arrête jamais.
        this.currentTurn++;
        LogManager.logMessage("Passage au tour %d de la partie %s.".formatted(this.currentTurn, this.name));
        this.nextActionAt = new Timestamp(System.currentTimeMillis() + this.turnInterval * 60_000L);
        this.scheduleNextTurn();
        this.server.getGameManager().handleGameUpdate(this);
    }

    /**
     * Méthode pour obtenir la liste des joueurs dans la partie.
     *
     * @return La liste des joueurs dans la partie.
     */
    public synchronized Collection<ServerPlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.players.values());
    }

    /**
     * Méthode pour obtenir un joueur à partir d'un utilisateur.
     *
     * @param user L'utilisateur dont on veut obtenir le joueur.
     * @return Le joueur correspondant à l'utilisateur, ou null si l'utilisateur ne joue pas dans cette partie.
     */
    public synchronized ServerPlayer getPlayerFor(User user) {
        return this.players.get(user.getId());
    }

    /**
     * Méthode pour ajouter un joueur à la partie.
     * Un joueur ne peut être ajouté que si la partie est en attente et qu'il y a de la place.
     *
     * @param player Le joueur à ajouter.
     * @throws IllegalStateException Si la partie n'est pas en attente ou si elle est pleine.
     */
    public synchronized void addPlayer(ServerPlayer player) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot add player to a game that has already started.");
        }
        if (this.players.size() >= this.maxPlayers) {
            throw new IllegalStateException("Cannot add player, game is full.");
        }

        LogManager.logMessage("Ajout du joueur %s à la partie %s.".formatted(player.getUser().getUsername(), this.name));
        this.players.put(player.getUser().getId(), player);

        if (this.state == GameState.WAITING && this.hasSufficientPlayers() && this.delayedTask == null) {
            LogManager.logMessage("Suffisamment de joueurs pour démarrer la partie %s, planification du démarrage.".formatted(this.name));
            this.nextActionAt = new Timestamp(System.currentTimeMillis() + 60_000L); // Démarrage prévu dans 1 minute
            this.scheduleGameStart();
            this.server.getGameManager().handleGameUpdate(this, player);
        }
    }

    /**
     * Méthode pour ajouter un joueur de manière forcée à la partie, sans vérifier son état.
     * Cette méthode est utilisée lors du chargement des joueurs d'une partie depuis la base de données, voir {@link RiseAndFallServer#loadServerData()}
     * Ne pas utiliser en fonctionnement normal pour ajouter des joueurs pendant le jeu, car cela pourrait violer les règles de la partie et causer des incohérences avec les joueurs déjà présents.
     *
     * @param player Le joueur à ajouter.
     */
    public synchronized void forceAddPlayer(ServerPlayer player) {
        this.players.put(player.getUser().getId(), player);
    }

    /**
     * Méthode pour retirer un joueur de la partie.
     * Un joueur ne peut être retiré que si la partie est en attente.
     *
     * @param user L'utilisateur dont on veut retirer le joueur.
     * @return Le joueur retiré, ou null si l'utilisateur ne joue pas dans cette partie.
     */
    public synchronized ServerPlayer removePlayer(User user) {
        if (this.state != GameState.WAITING) {
            throw new IllegalStateException("Cannot remove player from a game that has already started.");
        }

        ServerPlayer removedPlayer = this.players.remove(user.getId());
        LogManager.logMessage("Retrait du joueur %s de la partie %s.".formatted(user.getUsername(), this.name));
        if (removedPlayer != null && this.players.size() < this.minPlayers && this.delayedTask != null) {
            LogManager.logMessage("Moins de joueurs que le minimum requis pour démarrer la partie %s, annulation du démarrage différé.".formatted(this.name));
            this.delayedTask.cancel();
            this.delayedTask = null;
            this.nextActionAt = null;
            this.server.getGameManager().handleGameUpdate(this);
        }
        return removedPlayer;
    }

    /**
     * Méthode pour vérifier si la partie a suffisamment de joueurs pour commencer.
     *
     * @return true si la partie a suffisamment de joueurs, false sinon.
     */
    public synchronized boolean hasSufficientPlayers() {
        return this.players.size() >= this.minPlayers;
    }

    @Override
    public synchronized ToStringFormatter toStringFormatter() {
        return super.toStringFormatter()
                .add("minPlayers", this.minPlayers)
                .add("maxPlayers", this.maxPlayers)
                .add("isPrivate", this.isPrivate)
                .add("playersCount", this.players.size());
    }
}
