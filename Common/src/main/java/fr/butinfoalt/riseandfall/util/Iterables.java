package fr.butinfoalt.riseandfall.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Classe utilitaire pour manipuler des objets {@link Iterable}.
 */
public class Iterables {
    /**
     * Concatène plusieurs objets {@link Iterable} en un seul.
     *
     * @param iterables Les objets {@link Iterable} à concaténer.
     * @param <T>       Le type des éléments contenus dans les objets {@link Iterable}.
     * @return Un nouvel objet {@link Iterable} qui permet d'itérer sur tous les éléments des objets fournis.
     */
    @SafeVarargs
    public static <T> Iterable<T> concat(Iterable<T>... iterables) {
        return () -> new Iterator<T>() {

            private final Iterator<Iterable<T>> pos = Arrays.stream(iterables).iterator();
            private Iterator<T> iter = pos.next().iterator();

            @Override
            public boolean hasNext() {
                if (iter.hasNext()) {
                    return true;
                }
                if (pos.hasNext()) {
                    iter = pos.next().iterator();
                }
                return iter.hasNext();
            }

            @Override
            public T next() {
                return iter.next();
            }
        };
    }
}