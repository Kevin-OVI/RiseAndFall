package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.DataDeserializer;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

/**
 * Désérialiseur de données spécifique au client.
 * Il est utilisé pour désérialiser les données côté client.
 */
public class ClientDataDeserializer implements DataDeserializer {
    /**
     * Instance unique du désérialiseur de données client
     */
    public static final ClientDataDeserializer INSTANCE = new ClientDataDeserializer();

    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * Utilisez {@link #INSTANCE} pour obtenir l'instance unique.
     */
    private ClientDataDeserializer() {
    }

    @Override
    public ClientPlayer getPlayerById(int playerId) {
        ClientPlayer player = RiseAndFall.getPlayer();
        if (player != null && player.getId() == playerId) {
            return player;
        }
        LogManager.logError("Tentative de récupération d'un joueur avec l'ID " + playerId + ", mais le joueur n'est pas connu du client !");
        return null;
    }
}
