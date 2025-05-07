package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.GameState;

import java.sql.Timestamp;

/**
 * Classe représentant une partie côté client.
 * Elle peut être utilisée pour afficher les informations de la partie dans l'interface utilisateur.
 */
public class ClientGame extends Game {
    /**
     * Constructeur de la classe ClientGame.
     *
     * @param id                Identifiant de la partie dans la base de données.
     * @param name              Nom de la partie.
     * @param turnInterval      Intervalle entre chaque tour (en minutes).
     * @param state             État de la partie (en attente, en cours, terminée).
     * @param lastTurnTimestamp Timestamp du dernier tour.
     * @param currentTurn       Tour actuel de la partie.
     */
    public ClientGame(int id, String name, int turnInterval, GameState state, Timestamp lastTurnTimestamp, int currentTurn) {
        super(id, name, turnInterval, state, lastTurnTimestamp, currentTurn);
    }
}
