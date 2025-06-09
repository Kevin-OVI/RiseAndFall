package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.DataDeserializer;

/**
 * Désérialiseur de données spécifique au serveur.
 * Il est utilisé pour désérialiser les données côté serveur.
 */
public class ServerDataDeserializer implements DataDeserializer {
    /**
     * Référence au serveur Rise and Fall.
     */
    private final RiseAndFallServer server;

    /**
     * Constructeur du désérialiseur de données serveur.
     *
     * @param server Le serveur Rise and Fall auquel ce désérialiseur est associé.
     */
    public ServerDataDeserializer(RiseAndFallServer server) {
        this.server = server;
    }

    /**
     * Obtient le joueur par son identifiant.
     * Cette méthode est utilisée pour récupérer un joueur spécifique à partir de son identifiant.
     *
     * @param playerId L'identifiant du joueur à récupérer.
     * @return Le joueur correspondant à l'identifiant, ou null si aucun joueur n'est trouvé.
     */
    @Override
    public Player getPlayerById(int playerId) {
        return this.server.getUserManager().getPlayer(playerId);
    }
}
