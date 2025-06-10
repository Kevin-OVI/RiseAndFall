package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Classe représentant une partie côté client.
 * Elle peut être utilisée pour afficher les informations de la partie dans l'interface utilisateur.
 */
public class ClientGame extends Game {
    private final HashMap<Integer, OtherClientPlayer> otherPlayers = new HashMap<>();

    /**
     * Constructeur de la classe ClientGame à partir d'un helper de lecture.
     * Cette méthode est utilisée pour désérialiser les données de la partie à partir d'un flux de données.
     *
     * @param readHelper Le helper de lecture pour lire les données de la partie.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation des données de la partie.
     */
    public ClientGame(ReadHelper readHelper) throws IOException {
        super(readHelper);
    }

    public void addOtherPlayer(OtherClientPlayer player) {
        this.otherPlayers.put(player.getId(), player);
    }

    public OtherClientPlayer getOtherPlayer(int playerId) {
        return this.otherPlayers.get(playerId);
    }

    public Collection<OtherClientPlayer> getOtherPlayers() {
        return Collections.unmodifiableCollection(this.otherPlayers.values());
    }
}
