package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.front.gamelogic.ClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.gamelogic.data.DataDeserializer;

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
        return RiseAndFall.getPlayer(playerId);
    }
}
