package fr.butinfoalt.riseandfall.util;

import java.util.*;

/**
 * Une classe pour associer plusieurs types d'énumération à des entiers.
 *
 * @param <T> Le type d'énumération à associer.
 */
public class ObjectIntMap<T> implements Iterable<ObjectIntMap.Entry<T>> {
    /**
     * Un tableau d'entiers pour stocker les valeurs associées à chaque valeur de l'énumération.
     */
    private final LinkedHashMap<T, Integer> map;

    /**
     * Constructeur privé de la classe EnumIntMap.
     * On crée un tableau d'entiers de la taille spécifiée.
     *
     * @param keyUniverse Un tableau contenant les valeurs de l'énumération filtrées.
     */
    public ObjectIntMap(Collection<T> keyUniverse) {
        this.map = new LinkedHashMap<>();
        for (T key : keyUniverse) {
            this.map.put(key, 0);
        }
    }

    /**
     * Permet de définir la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être définie.
     * @param count La valeur à associer au type d'énumération.
     */
    public void set(T type, int count) {
        this.map.put(type, count);
    }

    /**
     * Permet d'obtenir la valeur associée à un type d'énumération.
     *
     * @param type Le type d'énumération pour lequel la valeur doit être obtenue.
     * @return La valeur associée au type d'énumération.
     */
    public int get(T type) {
        return this.map.get(type);
    }

    /**
     * Permet d'incrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être incrémentée.
     * @param count La valeur à ajouter au type d'énumération.
     * @return La nouvelle valeur associée au type d'énumération après l'incrémentation.
     */
    public int increment(T type, int count) {
        int value = this.get(type) + count;
        this.set(type, value);
        return value;
    }

    /**
     * Permet de décrémenter la valeur associée à un type d'énumération.
     *
     * @param type  Le type d'énumération pour lequel la valeur doit être décrémentée.
     * @param count La valeur à soustraire au type d'énumération.
     * @return La nouvelle valeur associée au type d'énumération après la décrémentation.
     */
    public int decrement(T type, int count) {
        int value = this.get(type) - count;
        this.set(type, value);
        return value;
    }

    /**
     * Permet d'obtenir les types de l'énumération.
     *
     * @return Un tableau contenant les types de l'énumération.
     */
    public Set<T> getEnumConstants() {
        return this.map.keySet();
    }

    /**
     * Permet d'obtenir le nombre de types dans l'énumération.
     *
     * @return Le nombre de types dans l'énumération.
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Permet d'obtenir le nombre total de valeurs associées dans l'association.
     *
     * @return Le nombre total de valeurs associées dans l'association.
     */
    public int getTotal() {
        return this.map.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Permet de créer une copie vide de l'association.
     *
     * @return Une nouvelle instance d'EnumIntMap vide.
     */
    public ObjectIntMap<T> createEmptyClone() {
        return new ObjectIntMap<>(this.getEnumConstants());
    }

    /**
     * Permet d'obtenir un itérateur sur les entrées de l'association.
     * Nécessaire pour l'implémentation de l'interface Iterable.
     *
     * @return Un itérateur sur les entrées de l'association.
     */
    @Override
    public Iterator<Entry<T>> iterator() {
        return new EnumIntMapIterator<>(this);
    }

    /**
     * Permet d'obtenir un spliterator sur les entrées de l'association.
     * Nécessaire pour l'implémentation de l'interface Iterable.
     *
     * @return Un spliterator sur les entrées de l'association.
     */
    @Override
    public Spliterator<Entry<T>> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.ORDERED);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ObjectIntMap<?> that = (ObjectIntMap<?>) o;
        return Objects.equals(this.map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.map);
    }

    @Override
    public String toString() {
        ToStringFormatter formatter = new ToStringFormatter("ObjectIntMap");
        for (T key : this.map.keySet()) {
            formatter.add(key.toString(), this.map.get(key));
        }
        return formatter.build();
    }

    /**
     * Un itérateur sur les entrées de l'association entre les types d'énumération et les entiers.
     *
     * @param <T> Le type d'énumération à associer.
     */
    public static class EnumIntMapIterator<T> implements Iterator<Entry<T>> {
        /**
         * L'instance de l'association sur laquelle itérer
         */
        private final ObjectIntMap<T> objectIntMap;
        private final Iterator<T> internalIterator;

        /**
         * Constructeur de l'itérateur.
         *
         * @param objectIntMap L'instance de l'association sur laquelle itérer.
         */
        public EnumIntMapIterator(ObjectIntMap<T> objectIntMap) {
            this.objectIntMap = objectIntMap;
            this.internalIterator = objectIntMap.map.keySet().iterator();
        }

        /**
         * Vérifie s'il y a encore des éléments à itérer.
         * Nécessaire pour l'implémentation de l'interface Iterator.
         *
         * @return true s'il y a encore des éléments, false sinon.
         */
        @Override
        public boolean hasNext() {
            return this.internalIterator.hasNext();
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
            return new Entry<>(this.objectIntMap, this.internalIterator.next());
        }
    }

    /**
     * Une entrée de l'association entre un type d'énumération et un entier.
     *
     * @param <T> Le type d'énumération à associer.
     */
    public static final class Entry<T> {
        /**
         * L'instance de l'association sur laquelle l'entrée est associée.
         */
        private final ObjectIntMap<T> objectIntMap;

        /**
         * Le type d'énumération associé à l'entrée.
         */
        private final T key;

        /**
         * Constructeur de l'entrée.
         *
         * @param objectIntMap L'instance de l'association sur laquelle l'entrée est associée.
         * @param key          Le type d'énumération associé à l'entrée.
         */
        private Entry(ObjectIntMap<T> objectIntMap, T key) {
            this.objectIntMap = objectIntMap;
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
            return this.objectIntMap.get(this.key);
        }

        /**
         * Définit la valeur associée à la clé de l'entrée.
         *
         * @param value La valeur à associer à la clé de l'entrée.
         */
        public void setValue(int value) {
            this.objectIntMap.set(this.key, value);
        }

        /**
         * Incrémente la valeur associée à la clé de l'entrée.
         *
         * @param count La valeur à ajouter à la clé de l'entrée.
         * @return La nouvelle valeur associée à la clé de l'entrée après l'incrémentation.
         */
        public int increment(int count) {
            return this.objectIntMap.increment(this.key, count);
        }

        /**
         * Décrémente la valeur associée à la clé de l'entrée.
         *
         * @param count La valeur à soustraire à la clé de l'entrée.
         * @return La nouvelle valeur associée à la clé de l'entrée après la décrémentation.
         */
        public int decrement(int count) {
            return this.objectIntMap.decrement(this.key, count);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?> entry = (Entry<?>) o;
            return Objects.equals(this.objectIntMap, entry.objectIntMap) && Objects.equals(this.key, entry.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.objectIntMap, this.key);
        }

        @Override
        public String toString() {
            return new ToStringFormatter("ObjectIntMap.Entry")
                    .add("key", this.key)
                    .add("value", this.getValue())
                    .build();
        }
    }
}
