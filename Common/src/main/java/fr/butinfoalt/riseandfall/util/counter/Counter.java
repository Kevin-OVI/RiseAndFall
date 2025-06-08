package fr.butinfoalt.riseandfall.util.counter;

import fr.butinfoalt.riseandfall.util.Dispatcher;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Classe représentant un compteur d'entiers avec des modificateurs et des écouteurs de changement.
 * Elle permet de gérer un compteur qui peut être modifié par des modificateurs et d'informer les
 * écouteurs lorsque la valeur du compteur change.
 */
public class Counter<T extends Number> {
    /**
     * Valeur initiale du compteur.
     */
    private final OperationHelper<T> initialValue;

    /**
     * Ensemble de modificateurs appliqués au compteur.
     */
    private final HashSet<Modifier<T>> modifiers = new HashSet<>();

    /**
     * Ensemble d'écouteurs de changement de valeur du compteur.
     */
    private final Dispatcher<T> dispatcher = new Dispatcher<>(false);

    /**
     * Valeur actuelle du compteur.
     */
    private OperationHelper<T> currentValue;

    /**
     * Constructeur de la classe Counter.
     * Initialise le compteur avec une valeur initiale.
     *
     * @param initialValue La valeur initiale du compteur.
     */
    private Counter(OperationHelper<T> initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    /**
     * @return Un compteur de type Integer initialisé à la valeur spécifiée.
     */
    public static Counter<Integer> of(int initialValue) {
        return new Counter<>(new IntOperationHelper(initialValue));
    }

    /**
     * @return Un compteur de type Float initialisé à la valeur spécifiée.
     */
    public static Counter<Float> of(float initialValue) {
        return new Counter<>(new FloatOperationHelper(initialValue));
    }

    /**
     * Ajoute un modificateur au compteur.
     * Le modificateur est ajouté à la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @param delta La valeur du modificateur à ajouter.
     * @return Le modificateur ajouté.
     */
    public Modifier<T> addModifier(T delta) {
        Modifier<T> modifier = new Modifier<>(this, delta);
        this.modifiers.add(modifier);
        this.dispatcher.dispatch((this.currentValue = this.currentValue.add(delta)).getValue());
        return modifier;
    }

    /**
     * Ajoute un modificateur au compteur avec une valeur de 0.
     * Le modificateur est ajouté à la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @return Le modificateur ajouté.
     */
    public Modifier<T> addModifier() {
        return this.addModifier(this.initialValue.getDefaultModifierValue());
    }

    /**
     * Supprime un modificateur du compteur.
     * Le modificateur est retiré de la liste des modificateurs et la valeur actuelle du compteur est mise à jour.
     *
     * @param modifier Le modificateur à supprimer.
     */
    public void removeModifier(Modifier<T> modifier) {
        if (this.modifiers.remove(modifier)) {
            this.dispatcher.dispatch((this.currentValue = this.currentValue.subtract(modifier.getDelta())).getValue());
        }
    }

    /**
     * Vérifie si le compteur a un modificateur spécifique.
     *
     * @param modifier Le modificateur à vérifier.
     * @return true si le modificateur est présent, false sinon.
     */
    public boolean hasModifier(Modifier<T> modifier) {
        return this.modifiers.contains(modifier);
    }

    /**
     * Récupère la valeur initiale du compteur sans prendre en compte les modificateurs.
     *
     * @return La valeur initiale du compteur.
     */
    public T getInitialValue() {
        return this.initialValue.getValue();
    }

    /**
     * Récupère la valeur actuelle du compteur, y compris les modificateurs.
     *
     * @return La valeur actuelle du compteur.
     */
    public T getCurrentValue() {
        return this.currentValue.getValue();
    }

    /**
     * Calcule la nouvelle valeur du compteur en fonction des deltas précédents et nouveaux.
     *
     * @param previousDelta L'ancien delta
     * @param newDelta      Le nouveau delta
     * @return Une nouvelle instance d'OperationHelper représentant la valeur actuelle du compteur après application des deltas.
     */
    OperationHelper<T> computeNewValue(T previousDelta, T newDelta) {
        return this.currentValue.add(newDelta).subtract(previousDelta);
    }

    /**
     * Met à jour la valeur actuelle du compteur en fonction des deltas précédents et nouveaux.
     *
     * @param previousDelta L'ancien delta
     * @param newDelta      Le nouveau delta
     */
    void updateCurrentValue(T previousDelta, T newDelta) {
        this.dispatcher.dispatch((this.currentValue = this.computeNewValue(previousDelta, newDelta)).getValue());
    }

    /**
     * Ajoute un écouteur de changement de valeur du compteur.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(Consumer<T> listener) {
        this.dispatcher.addListener(listener);
    }

    /**
     * Supprime un écouteur de changement de valeur du compteur.
     *
     * @param listener L'écouteur à supprimer.
     */
    public void removeListener(Consumer<T> listener) {
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
            this.dispatcher.dispatch(this.currentValue.getValue());
        }
    }

    @Override
    public String toString() {
        return "Counter{initialValue=%s, modifiers=%s, dispatcher=%s, currentValue=%s}".formatted(this.initialValue, this.modifiers, this.dispatcher, this.currentValue);
    }
}
