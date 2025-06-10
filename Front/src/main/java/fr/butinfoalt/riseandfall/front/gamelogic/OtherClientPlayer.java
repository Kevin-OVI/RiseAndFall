package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

public class OtherClientPlayer extends ClientPlayer {
    /**
     * Nom du joueur.
     */
    private final String playerName;

    /**
     * Constructeur de la classe OtherClientPlayer.
     *
     * @param id         L'identifiant unique du joueur.
     * @param race       La race choisie par le joueur.
     * @param playerName Le nom du joueur.
     */
    public OtherClientPlayer(int id, Race race, String playerName) {
        super(id, race);
        this.playerName = playerName;
    }

    /**
     * Méthode pour obtenir le nom du joueur.
     *
     * @return Le nom du joueur.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    protected ToStringFormatter toStringFormatter() {
        return super.toStringFormatter()
                .add("playerName", this.playerName);
    }
}
