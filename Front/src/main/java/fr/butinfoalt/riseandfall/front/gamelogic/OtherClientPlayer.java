package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

public class OtherClientPlayer extends ClientPlayer implements NamedItem {
    /**
     * Nom du joueur.
     */
    private String name;

    /**
     * Constructeur de la classe OtherClientPlayer.
     *
     * @param id   L'identifiant unique du joueur.
     * @param race La race choisie par le joueur.
     * @param name Le nom du joueur.
     */
    public OtherClientPlayer(int id, Race race, String name) {
        super(id, race);
        this.name = name;
    }

    public OtherClientPlayer(int id) {
        super(id, null);
        this.name = "Unknown Player <" + id + ">";
    }

    @Override
    protected ToStringFormatter toStringFormatter() {
        return super.toStringFormatter()
                .add("name", this.name);
    }

    public void setRace(Race race) {
        this.race = race;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEliminated() {
        // La liste de bâtiments et d'unités est toujours vide pour les autres joueurs sur le client.
        return this.getEliminationTurn() != -1;
    }
}
