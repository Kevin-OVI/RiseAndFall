package fr.butinfoalt.riseandfall.gamelogic.map;

import java.util.*;

/**
 * Une classe pour associer plusieurs types d'énumération à des entiers.
 *
 * @param <T> Le type d'énumération à associer.
 */
public class EnumIntMap<T extends Enum<T>> implements Iterable<EnumIntMap.Entry<T>> {
    /**
     * La classe de l'énumération pour laquelle les valeurs doivent être associées.
     */
    private final Class<T> enumClass;

    /**
     * Un tableau d'entiers pour stocker les valeurs associées à chaque valeur de l'énumération.
     */
    private final int[] map;

    /**
     * Constructeur de la classe EnumIntMap.
     * On crée un tableau d'entiers de la même taille que le nombre d'éléments dans l'énumération.
     *
     * @param enumClass La classe de l'énumération pour laquelle les valeurs doivent être associées.
     */
    public EnumIntMap(Class<T> enumClass) {
        this.enumClass = enumClass;
        this.map = new int[enumClass.getEnumConstants().length];
    }

    /**
     * Permet de définir la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être définie.
     * @param count La valeur à associer au type d'énumération.
     */
    public void set(T type, int count) {
        this.map[type.ordinal()] = count;
    }

    /**
     * Permet d'obtenir la valeur associée à un type d'énumération.
     *
     * @param type Le type d'énumération pour lequel la valeur doit être obtenue.
     * @return La valeur associée au type d'énumération.
     */
    public int get(T type) {
        return this.map[type.ordinal()];
    }

    /**
     * Permet d'incrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être incrémentée.
     * @param count La valeur à ajouter au type d'énumération.
     * @return La nouvelle valeur associée au type d'énumération après l'incrémentation.
     */
    public int increment(T type, int count) {
        return this.map[type.ordinal()] += count;
    }

    /**
     * Permet de décrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être décrémentée.
     * @param count La valeur à soustraire au type d'énumération.
     * @return La nouvelle valeur associée au type d'énumération après la décrémentation.
     */
    public int decrement(T type, int count) {
        return this.map[type.ordinal()] -= count;
    }

    /**
     * Permet d'obtenir les types de l'énumération.
     *
     * @return Un tableau contenant les types de l'énumération.
     */
    public T[] getEnumConstants() {
        return this.enumClass.getEnumConstants();
    }

    /**
     * Permet d'obtenir le nombre de types dans l'énumération.
     *
     * @return Le nombre de types dans l'énumération.
     */
    public int size() {
        return this.map.length;
    }

    /**
     * Permet d'obtenir le nombre total de valeurs associées dans l'association.
     *
     * @return Le nombre total de valeurs associées dans l'association.
     */
    public int getTotal() {
        return Arrays.stream(this.map).sum();
    }

    /**
     * Permet d'obtenir un itérateur sur les entrées de l'association.
     * Nécessaire pour l'implémentation de l'interface Iterable.
     *
     * @return Un itérateur sur les entrées de l'association.
     */
    @Override
    public Iterator<Entry<T>> iterator() {
        return new EnumIntMapIterator<T>(this);
    }

    /**
     * Permet d'obtenir un spliterator sur les entrées de l'association.
     * Nécessaire pour l'implémentation de l'interface Iterable.
     *
     * @return Un spliterator sur les entrées de l'association.
     */
    @Override
    public Spliterator<Entry<T>> spliterator() {
        return Spliterators.spliterator(this.iterator(), map.length, Spliterator.ORDERED);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnumIntMap<?> that = (EnumIntMap<?>) o;
        return Objects.equals(this.enumClass, that.enumClass) && Arrays.equals(this.map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.enumClass, Arrays.hashCode(this.map));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EnumIntMap{enumClass=");
        sb.append(this.enumClass);
        sb.append(", map={");
        boolean removeLastComma = false;
        for (Entry<T> entry : this) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            removeLastComma = true;
        }
        if (removeLastComma) {
            sb.setLength(sb.length() - 2); // Remove the last comma and space
        }
        sb.append("}}");
        return sb.toString();
    }

    /**
     * Un itérateur sur les entrées de l'association entre les types d'énumération et les entiers.
     *
     * @param <T> Le type d'énumération à associer.
     */
    public static class EnumIntMapIterator<T extends Enum<T>> implements Iterator<Entry<T>> {
        /**
         * L'instance de l'association sur laquelle itérer
         */
        private final EnumIntMap<T> enumIntMap;

        /**
         * L'indice courant de l'itération.
         */
        private int currentIndex = 0;

        /**
         * Constructeur de l'itérateur.
         *
         * @param enumIntMap L'instance de l'association sur laquelle itérer.
         */
        public EnumIntMapIterator(EnumIntMap<T> enumIntMap) {
            this.enumIntMap = enumIntMap;
        }

        /**
         * Vérifie s'il y a encore des éléments à itérer.
         * Nécessaire pour l'implémentation de l'interface Iterator.
         *
         * @return true s'il y a encore des éléments, false sinon.
         */
        @Override
        public boolean hasNext() {
            return this.currentIndex < this.enumIntMap.size();
        }

        /**
         * Récupère l'élément suivant de l'itération.
         * Nécessaire pour l'implémentation de l'interface Iterator.
         *
         * @return L'entrée suivante de l'itération.
         * @throws NoSuchElementException Si aucun élément n'est disponible.
         */
        @Override
        public Entry<T> next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements to iterate.");
            }
            T key = this.enumIntMap.getEnumConstants()[this.currentIndex++];
            return new Entry<T>(this.enumIntMap, key);
        }
    }

    /**
     * Une entrée de l'association entre un type d'énumération et un entier.
     *
     * @param <T> Le type d'énumération à associer.
     */
    public static final class Entry<T extends Enum<T>> {
        /**
         * L'instance de l'association sur laquelle l'entrée est associée.
         */
        private final EnumIntMap<T> enumIntMap;

        /**
         * Le type d'énumération associé à l'entrée.
         */
        private final T key;

        /**
         * Constructeur de l'entrée.
         *
         * @param enumIntMap L'instance de l'association sur laquelle l'entrée est associée.
         * @param key        Le type d'énumération associé à l'entrée.
         */
        private Entry(EnumIntMap<T> enumIntMap, T key) {
            this.enumIntMap = enumIntMap;
            this.key = key;
        }

        /**
         * Récupère la clé de l'entrée.
         *
         * @return La clé de l'entrée.
         */
        public T getKey() {
            return this.key;
        }

        /**
         * Récupère la valeur associée à la clé de l'entrée.
         *
         * @return La valeur associée à la clé de l'entrée.
         */
        public int getValue() {
            return this.enumIntMap.get(this.key);
        }

        /**
         * Définit la valeur associée à la clé de l'entrée.
         *
         * @param value La valeur à associer à la clé de l'entrée.
         */
        public void setValue(int value) {
            this.enumIntMap.set(this.key, value);
        }

        /**
         * Incrémente la valeur associée à la clé de l'entrée.
         *
         * @param count La valeur à ajouter à la clé de l'entrée.
         * @return La nouvelle valeur associée à la clé de l'entrée après l'incrémentation.
         */
        public int increment(int count) {
            return this.enumIntMap.increment(this.key, count);
        }

        /**
         * Décrémente la valeur associée à la clé de l'entrée.
         *
         * @param count La valeur à soustraire à la clé de l'entrée.
         * @return La nouvelle valeur associée à la clé de l'entrée après la décrémentation.
         */
        public int decrement(int count) {
            return this.enumIntMap.decrement(this.key, count);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?> entry = (Entry<?>) o;
            return Objects.equals(this.enumIntMap, entry.enumIntMap) && Objects.equals(this.key, entry.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.enumIntMap, this.key);
        }

        @Override
        public String toString() {
            return "Entry{enumIntMap=%s, key=%s, value=%s}".formatted(this.enumIntMap, this.key, this.getValue());
        }
    }
}
