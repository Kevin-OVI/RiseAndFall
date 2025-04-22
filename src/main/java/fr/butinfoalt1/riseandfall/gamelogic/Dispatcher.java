package fr.butinfoalt1.riseandfall.gamelogic;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Permet de pour gérer les événements et les écouteurs.
 * Elle permet d'ajouter, de supprimer des écouteurs et de déclencher des événements.
 *
 * @param <T> Le type de l'événement à dispatcher.
 */
public class Dispatcher<T> {
    /**
     * Ensemble d'écouteurs qui seront notifiés lors de la survenue d'un événement.
     */
    private final HashSet<Consumer<T>> listeners = new HashSet<>();
    /**
     * Indique si les changements doivent être dispatchés.
     */
    private boolean dispatchChanges;

    /**
     * Constructeur de la classe Dispatcher.
     * Initialise le dispatcher avec une option pour activer ou désactiver le dispatching des changements.
     *
     * @param dispatchChanges Indique si les changements doivent être dispatchés.
     */
    public Dispatcher(boolean dispatchChanges) {
        this.dispatchChanges = dispatchChanges;
    }

    /**
     * Ajoute un écouteur à la liste des écouteurs.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    /**
     * Supprime un écouteur de la liste des écouteurs.
     *
     * @param listener L'écouteur à supprimer.
     */
    public void removeListener(Consumer<T> listener) {
        this.listeners.remove(listener);
    }

    /**
     * Déclenche l'événement et notifie tous les écouteurs.
     *
     * @param event L'événement à dispatcher.
     */
    public void dispatch(T event) {
        if (this.dispatchChanges) {
            for (Consumer<T> listener : this.listeners) {
                listener.accept(event);
            }
        }
    }

    /**
     * Vérifie si les changements doivent être dispatchés.
     *
     * @return true si les changements doivent être dispatchés, false sinon.
     */
    public boolean isDispatchChanges() {
        return dispatchChanges;
    }

    /**
     * Définit si les changements doivent être dispatchés.
     *
     * @param dispatchChanges true pour activer le dispatching des changements, false pour le désactiver.
     */
    public void setDispatchChanges(boolean dispatchChanges) {
        this.dispatchChanges = dispatchChanges;
    }
}
