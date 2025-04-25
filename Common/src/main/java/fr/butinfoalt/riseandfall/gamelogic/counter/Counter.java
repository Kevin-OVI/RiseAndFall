package fr.butinfoalt.riseandfall.gamelogic.counter;

import fr.butinfoalt.riseandfall.gamelogic.Dispatcher;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Classe représentant un compteur d'entiers avec des modificateurs et des écouteurs de changement.
 * Elle permet de gérer un compteur qui peut être modifié par des modificateurs et d'informer les
 * écouteurs lorsque la valeur du compteur change.
 */
public class Counter {
    /**
     * Valeur initiale du compteur.
     */
    private final int initialValue;

    /**
     * Ensemble de modificateurs appliqués au compteur.
     */
    private final HashSet<Modifier> modifiers = new HashSet<>();

    /**
     * Ensemble d'écouteurs de changement de valeur du compteur.
     */
    private final Dispatcher<Integer> dispatcher = new Dispatcher<>(false);

    /**
     * Valeur actuelle du compteur.
     */
    private int currentValue;

    /**
     * Constructeur de la classe Counter.
     * Initialise le compteur avec une valeur initiale.
     *
     * @param initialValue La valeur initiale du compteur.
     */
    public Counter(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    /**
     * Ajoute un modificateur au compteur.
     * Le modificateur est ajouté à la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @param delta La valeur du modificateur à ajouter.
     * @return Le modificateur ajouté.
     */
    public Modifier addModifier(int delta) {
        Modifier modifier = new Modifier(this, delta);
        this.modifiers.add(modifier);
        this.dispatcher.dispatch(this.currentValue += delta);
        return modifier;
    }

    /**
     * Ajoute un modificateur au compteur avec une valeur de 0.
     * Le modificateur est ajouté à la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @return Le modificateur ajouté.
     */
    public Modifier addModifier() {
        return this.addModifier(0);
    }

    /**
     * Supprime un modificateur du compteur.
     * Le modificateur est retiré de la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @param modifier Le modificateur à supprimer.
     */
    public void removeModifier(Modifier modifier) {
        if (this.modifiers.remove(modifier)) {
            this.dispatcher.dispatch(this.currentValue -= modifier.getDelta());
        }
    }

    /**
     * Vérifie si le compteur a un modificateur spécifique.
     *
     * @param modifier Le modificateur à vérifier.
     * @return true si le modificateur est présent, false sinon.
     */
    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    /**
     * Récupère la valeur initiale du compteur sans prendre en compte les modificateurs.
     *
     * @return La valeur initiale du compteur.
     */
    public int getInitialValue() {
        return this.initialValue;
    }

    /**
     * Récupère la valeur actuelle du compteur, y compris les modificateurs.
     *
     * @return La valeur actuelle du compteur.
     */
    public int getCurrentValue() {
        return this.currentValue;
    }

    /**
     * Met à jour la valeur actuelle du compteur en fonction des deltas précédents et nouveaux.
     *
     * @param previousDelta L'ancien delta
     * @param newDelta      Le nouveau delta
     */
    void updateCurrentValue(int previousDelta, int newDelta) {
        this.dispatcher.dispatch(this.currentValue += newDelta - previousDelta);
    }

    /**
     * Ajoute un écouteur de changement de valeur du compteur.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(Consumer<Integer> listener) {
        this.dispatcher.addListener(listener);
    }

    /**
     * Supprime un écouteur de changement de valeur du compteur.
     *
     * @param listener L'écouteur à supprimer.
     */
    public void removeListener(Consumer<Integer> listener) {
        this.dispatcher.removeListener(listener);
    }

    /**
     * Vérifie si la distribution des changements est activée.
     *
     * @return true si la distribution des changements est activée, false sinon.
     */
    public boolean isDispatchChanges() {
        return this.dispatcher.isDispatchChanges();
    }

    /**
     * Définit si la distribution des changements est activée ou non.
     * Si true, la valeur actuelle sera distribuée immédiatement.
     *
     * @param dispatchChanges true pour activer la distribution des changements, false sinon.
     */
    public void setDispatchChanges(boolean dispatchChanges) {
        this.dispatcher.setDispatchChanges(dispatchChanges);
        if (dispatchChanges) {
            this.dispatcher.dispatch(this.currentValue);
        }
    }

    @Override
    public String toString() {
        return "Counter{initialValue=%d, modifiers=%s, dispatcher=%s, currentValue=%d}".formatted(this.initialValue, this.modifiers, this.dispatcher, this.currentValue);
    }
}
