package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.chat.ChatController;
import fr.butinfoalt.riseandfall.gamelogic.data.ChatMessage;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;
import javafx.application.Platform;

import java.util.*;

public class OtherClientPlayer extends ClientPlayer implements NamedItem {
    /**
     * Nom du joueur.
     */
    private String name;

    /**
     * Liste des messages de chat dans le chat avec ce joueur.
     */
    private final SortedSet<ChatMessage> messages = new TreeSet<>(Comparator.comparing(ChatMessage::getTimestamp));
    private final HashMap<Long, ChatMessage> sendingMessages = new HashMap<>();

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

    private synchronized void addMessage(ChatMessage message) {
        this.messages.add(message);
        ChatController chatController = View.CHAT.getController();
        Platform.runLater(() -> chatController.onAddMessage(this, message));
    }

    /**
     * Ajoute un message en cours d'envoi à la liste des messages de ce joueur.
     *
     * @param message Le message à ajouter.
     */
    public synchronized void addSendingMessage(ChatMessage message) {
        this.addMessage(message);
        this.sendingMessages.put(message.getNonce(), message);
    }

    /**
     * Ajoute un message reçu à la liste des messages de ce joueur.
     * Si le message a un nonce, il est traité différemment selon qu'il a été envoyé par le joueur ou reçu d'un autre joueur.
     *
     * @param message Le message de chat reçu.
     */
    public synchronized void addReceivedMessage(ChatMessage message) {
        if (message.getNonce() != -1) {
            ChatController chatController = View.CHAT.getController();
            if (message.getSender() == RiseAndFall.getPlayer()) {
                ChatMessage sendingMessage = this.sendingMessages.remove(message.getNonce());
                if (sendingMessage != null) {
                    this.messages.remove(sendingMessage);
                    Platform.runLater(() -> chatController.onRemoveMessage(this, sendingMessage));
                }
            } else {
                Platform.runLater(() -> chatController.triggerMessageNotification(this, message));
            }
            message.resetNonce();
        }
        this.addMessage(message);
    }

    /**
     * Obtient la liste des messages de chat de ce joueur.
     *
     * @return Une liste non modifiable des messages de chat.
     */
    public SortedSet<ChatMessage> getMessages() {
        return Collections.unmodifiableSortedSet(this.messages);
    }

    @Override
    public boolean isEliminated() {
        // La liste de bâtiments et d'unités est toujours vide pour les autres joueurs sur le client.
        return this.getEliminationTurn() != -1;
    }
}
