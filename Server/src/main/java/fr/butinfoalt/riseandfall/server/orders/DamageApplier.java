package fr.butinfoalt.riseandfall.server.orders;

import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.function.ToFloatFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Classe utilitaire pour appliquer des dégâts sur des éléments cibles, tels que des unités ou des bâtiments.
 * Elle permet de gérer la destruction d'éléments en fonction de leur résistance et de la quantité de dégâts appliqués.
 *
 * @param <T> Le type des éléments cibles, par exemple {@link UnitType} ou {@link BuildingType}.
 */
public class DamageApplier<T> {
    private static final Random PRNG = new Random();

    private final ObjectIntMap<T> remainingTargetElements;
    private final ToFloatFunction<T> resistanceConverter;
    private final ArrayList<T> flattenedElements;

    /**
     * Constructeur de la classe DamageApplier.
     *
     * @param targetElements      Les éléments cibles, association de leur type à leur quantité.
     *                            Cette association est modifiée pour refléter les éléments restants après les applications de dégâts.
     * @param resistanceConverter La fonction de conversion pour obtenir la résistance d'un élément cible.
     */
    public DamageApplier(ObjectIntMap<T> targetElements, ToFloatFunction<T> resistanceConverter) {
        this.remainingTargetElements = targetElements.clone();
        this.resistanceConverter = resistanceConverter;

        // On aplatit les éléments cibles en une liste dans laquelle chaque élément apparaît autant de fois que sa quantité.
        this.flattenedElements = new ArrayList<>(targetElements.getValues().stream().mapToInt(Integer::intValue).sum());
        for (ObjectIntMap.Entry<T> entry : targetElements) {
            T element = entry.getKey();
            int quantity = entry.getValue();
            for (int i = 0; i < quantity; i++) {
                flattenedElements.add(element);
            }
        }
        // On mélange les éléments pour appliquer les dégâts de manière aléatoire.
        Collections.shuffle(flattenedElements, PRNG);
    }

    /**
     * Applique des dégâts sur des éléments cibles en fonction de leur résistance.
     *
     * @param damage La quantité de dégâts à appliquer.
     * @return Le résultat de l'attaque, contenant les dégâts restants et les éléments détruits.
     */
    public DamageApplyResult<T> applyDamage(float damage) {
        ObjectIntMap<T> destroyedElements = remainingTargetElements.createEmptyClone();
        if (damage <= 0 || remainingTargetElements.isEmpty()) {
            return new DamageApplyResult<>(damage, destroyedElements);
        }

        while (damage > 0 && !this.flattenedElements.isEmpty()) {
            T element = this.flattenedElements.removeLast();
            float resistance = this.resistanceConverter.applyAsFloat(element);
            if (damage > resistance * 0.5) { // Si les dégâts sont supérieurs à la moitié de la résistance, l'élément est détruit.
                this.remainingTargetElements.decrement(element, 1);
                destroyedElements.increment(element, 1);
            }
            damage -= resistance;
        }
        return new DamageApplyResult<>(damage, destroyedElements);
    }

    /**
     * Représente le résultat d'une attaque sur des éléments cibles.
     */
    public static final class DamageApplyResult<T> {
        /**
         * La quantité de dégâts restants à appliquer après la destruction des éléments cibles.
         */
        private final float remainingDamage;
        /**
         * Les éléments cibles qui ont été détruits, association de leur type à leur quantité.
         */
        private final ObjectIntMap<T> destroyedElements;

        /**
         * Constructeur du résultat de l'application de dégâts.
         *
         * @param remainingDamage   La quantité de dégâts restants après l'application.
         * @param destroyedElements Les éléments cibles qui ont été détruits, association de leur type à leur quantité.
         */
        private DamageApplyResult(float remainingDamage, ObjectIntMap<T> destroyedElements) {
            this.remainingDamage = remainingDamage;
            this.destroyedElements = destroyedElements;
        }

        /**
         * Obtient la quantité de dégâts restants après l'application.
         *
         * @return La quantité de dégâts restants.
         */
        public float getRemainingDamage() {return remainingDamage;}

        /**
         * Obtient les éléments cibles qui ont été détruits, association de leur type à leur quantité.
         *
         * @return Les éléments détruits.
         */
        public ObjectIntMap<T> getDestroyedElements() {return destroyedElements;}
    }
}
