package fr.butinfoalt.riseandfall.util;

import java.util.Collection;

/**
 * Classe utilitaire pour les opérations mathématiques.
 */
public class MathUtils {
    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private MathUtils() {
    }

    /**
     * Calcule la somme de tous les floats dans une collection.
     *
     * @param values La collection de floats à sommer.
     * @return La somme des floats dans la collection.
     */
    public static float sumFloats(Collection<Float> values) {
        return values.stream().reduce(0f, Float::sum);
    }
}
