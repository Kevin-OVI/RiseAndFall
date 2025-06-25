package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OtherClientPlayer extends ClientPlayer implements NamedItem {
    /**
     * Nom du joueur.
     */
    private String name;

    /**
     * Liste des messages de chat dans le chat avec ce joueur.
     */
    private final ArrayList<ChatMessage> messages = new ArrayList<>();

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

    /**
     * Ajoute un message de chat à la liste des messages de ce joueur.
     *
     * @param message Le message de chat à ajouter.
     */
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    /**
     * Obtient la liste des messages de chat de ce joueur.
     *
     * @return Une liste non modifiable des messages de chat.
     */
    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public boolean isEliminated() {
        // La liste de bâtiments et d'unités est toujours vide pour les autres joueurs sur le client.
        return this.getEliminationTurn() != -1;
    }
}
