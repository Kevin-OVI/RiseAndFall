package fr.butinfoalt.riseandfall.util.counter;

/**
 * Classe représentant un modificateur appliqué à un compteur.
 * Un modificateur est associé à un compteur et a une valeur delta qui modifie la valeur actuelle du compteur.
 */
public class Modifier<T extends Number> {
    /**
     * Le compteur auquel ce modificateur est associé.
     */
    private final Counter<T> counter;
    /**
     * La valeur delta du modificateur.
     * Elle représente la quantité à ajouter ou soustraire à la valeur actuelle du compteur.
     */
    private T delta;

    /**
     * Constructeur de la classe Modifier.
     * Initialise le modificateur avec le compteur associé et la valeur delta.
     *
     * @param counter Le compteur auquel ce modificateur est associé.
     * @param delta   La valeur delta du modificateur.
     */
    Modifier(Counter<T> counter, T delta) {
        this.counter = counter;
        this.delta = delta;
    }

    /**
     * Récupère le compteur associé à ce modificateur.
     *
     * @return Le compteur associé à ce modificateur.
     */
    public Counter<T> getCounter() {
        return this.counter;
    }

    /**
     * Récupère la valeur delta du modificateur.
     *
     * @return La valeur delta du modificateur.
     */
    public T getDelta() {
        return this.delta;
    }

    /**
     * Définit une nouvelle valeur delta pour le modificateur.
     * Met à jour la valeur actuelle du compteur en fonction de la nouvelle valeur delta.
     *
     * @param delta La nouvelle valeur delta du modificateur.
     */
    public void setDelta(T delta) {
        if (!this.counter.hasModifier(this)) {
            throw new IllegalStateException("This modifier got removed from the counter.");
        }
        T previousDelta = this.delta;
        this.delta = delta;
        this.counter.updateCurrentValue(previousDelta, delta);
    }

    /**
     * Supprime ce modificateur du compteur.
     * Met à jour la valeur actuelle du compteur en fonction de la valeur delta du modificateur.
     */
    public void remove() {
        this.counter.removeModifier(this);
    }

    /**
     * Calcule la valeur du compteur en considérant que le modifieur actuel a une valeur delta différente.
     *
     * @param alternativeDelta La valeur delta alternative à utiliser pour le calcul.
     * @return La valeur actuelle du compteur après application de la valeur delta alternative.
     */
    public T computeWithAlternativeDelta(T alternativeDelta) {
        return this.counter.computeNewValue(this.delta, alternativeDelta).getValue();
    }

    @Override
    public String toString() {
        // On n'affiche pas le compteur pour éviter les boucles infinies
        return "Modifier{delta=%s}".formatted(this.delta);
    }
}
