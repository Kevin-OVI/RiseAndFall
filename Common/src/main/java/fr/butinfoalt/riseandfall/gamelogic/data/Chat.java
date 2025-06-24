package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;

public class Chat {
    private int id;
    private Player receiver;
    private ChatMessage[] messages;

    public Chat(Integer id, Player receiver) {
        this.id = id;
        this.receiver = receiver;
        this.messages = new ChatMessage[0];
    }

    public int getId() {
        return id;
    }

    public Player getReceiver() {
        return receiver;
    }

    public ChatMessage[] getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        ChatMessage[] newMessages = new ChatMessage[messages.length + 1];
        System.arraycopy(messages, 0, newMessages, 0, messages.length);
        newMessages[messages.length] = message;
        messages = newMessages;
    }

    public boolean isEmpty() {
        return messages.length == 0;
    }
}
