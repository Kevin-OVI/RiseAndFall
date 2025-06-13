package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
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

    /**
     * Ajoute un joueur à la liste des joueurs découverts par le client.
     *
     * @param player Le joueur à ajouter.
     */
    public void addOtherPlayer(int playerId, Race race, String name) {
        OtherClientPlayer otherPlayer = otherPlayers.get(playerId);
        if (otherPlayer == null) {
            this.otherPlayers.put(playerId, new OtherClientPlayer(playerId, race, name));
        } else {
            otherPlayer.setRace(race);
            otherPlayer.setName(name);
        }
    }

    /**
     * Obtient un joueur découvert par son identifiant.
     *
     * @param playerId L'identifiant du joueur à récupérer.
     * @return Le joueur découvert correspondant à l'identifiant, ou un joueur factice créé s'il n'a pas été découvert.
     * Il sera mis à jour par {@link #addOtherPlayer(int, Race, String)} s'il est découvert plus tard.
     */
    public OtherClientPlayer getOtherPlayer(int playerId) {
        return this.otherPlayers.computeIfAbsent(playerId, OtherClientPlayer::new);
    }

    /**
     * Obtient une collection non modifiable de tous les joueurs découverts par le client.
     *
     * @return Une collection de tous les joueurs découverts.
     */
    public Collection<OtherClientPlayer> getOtherPlayers() {
        return Collections.unmodifiableCollection(this.otherPlayers.values());
    }

    /**
     * Obtient le nombre de joueurs découverts par le client.
     *
     * @return Le nombre de joueurs découverts.
     */
    public int getOtherPlayersCount() {
        return this.otherPlayers.size();
    }
}
