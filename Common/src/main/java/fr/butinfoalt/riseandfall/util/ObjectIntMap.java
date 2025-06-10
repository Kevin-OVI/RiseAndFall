package fr.butinfoalt.riseandfall.util;

import java.util.*;

/**
 * Une classe pour associer des objets à des entiers.
 *
 * @param <T> Le type d'objet à associer aux entiers.
 */
public class ObjectIntMap<T> implements Iterable<ObjectIntMap.Entry<T>> {
    /**
     * Un LinkedHashMap pour stocker l'association entre les objets et les entiers de manière ordonnée.
     */
    private final LinkedHashMap<T, Integer> map;

    /**
     * Constructeur de la classe ObjectIntMap.
     * Ce constructeur initialise l'association entre les objets et les entiers avec une valeur par défaut de 0 pour chaque objet.
     *
     * @param keyUniverse Un tableau contenant les valeurs possibles pour les clés de l'association.
     */
    public ObjectIntMap(Collection<T> keyUniverse) {
        this.map = new LinkedHashMap<>();
        for (T key : keyUniverse) {
            this.map.put(key, 0);
        }
    }

    /**
     * Permet de définir la valeur associée à un objet.
     *
     * @param key   La clée pour laquelle la valeur doit être définie.
     * @param value La valeur à associer à la clé.
     */
    public void set(T key, int value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Type not found in the map: " + key);
        }
        this.map.put(key, value);
    }

    /**
     * Permet d'obtenir la valeur associée à un objet.
     *
     * @param key La clé pour laquelle la valeur doit être récupérée.
     * @return La valeur associée à la clé.
     */
    public int get(T key) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Type not found in the map: " + key);
        }
        return this.map.get(key);
    }

    /**
     * Permet d'incrémenter la valeur associée à une clé.
     *
     * @param key   La clé pour laquelle la valeur doit être incrémentée.
     * @param count La valeur à ajouter à la valeur déjà associée à la clé.
     * @return La nouvelle valeur associée à la clé après l'incrémentation.
     */
    public int increment(T key, int count) {
        int value = this.get(key) + count;
        this.set(key, value);
        return value;
    }

    /**
     * Permet de décrémenter la valeur associée à une clé.
     *
     * @param key   La clé pour laquelle la valeur doit être décrémentée.
     * @param count La valeur à soustraire de la valeur déjà associée à la clé.
     * @return La nouvelle valeur associée à la clé après la décrémentation.
     */
    public int decrement(T key, int count) {
        int value = this.get(key) - count;
        this.set(key, value);
        return value;
    }

    /**
     * Permet d'obtenir un ensemble contenant toutes les clés de l'association.
     *
     * @return Un ensemble contenant toutes les clés de l'association.
     */
    public SequencedSet<T> getKeys() {
        return this.map.sequencedKeySet();
    }

    /**
     * Permet d'obtenir le nombre d'entrées dans l'association.
     *
     * @return Le nombre d'entrées dans l'association.
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Permet de créer une copie vide de l'association.
     *
     * @return Une nouvelle instance d'ObjectIntMap vide avec les mêmes clés que l'instance actuelle.
     */
    public ObjectIntMap<T> createEmptyClone() {
        return new ObjectIntMap<>(this.getKeys());
    }

    /**
     * Permet d'obtenir un itérateur sur les entrées de l'association.
     * Nécessaire pour l'implémentation de l'interface Iterable.
     *
     * @return Un itérateur sur les entrées de l'association.
     */
    @Override
    public ObjectIntMapIterator<T> iterator() {
        return new ObjectIntMapIterator<>(this);
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
    public static class ObjectIntMapIterator<T> implements Iterator<Entry<T>> {
        /**
         * L'instance de l'association sur laquelle itérer
         */
        private final ObjectIntMap<T> objectIntMap;
        /**
         * L'itérateur interne sur les clés de l'association.
         */
        private final Iterator<T> internalIterator;

        /**
         * Constructeur de l'itérateur.
         *
         * @param objectIntMap L'instance de l'association sur laquelle itérer.
         */
        public ObjectIntMapIterator(ObjectIntMap<T> objectIntMap) {
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
     * Une entrée de l'association entre une clé et une valeur.
     *
     * @param <T> Le type d'objet à associé aux entiers.
     */
    public static final class Entry<T> {
        /**
         * L'instance de l'association sur laquelle l'entrée est associée.
         */
        private final ObjectIntMap<T> objectIntMap;

        /**
         * L'objet clé associé à l'entrée.
         */
        private final T key;

        /**
         * Constructeur de l'entrée.
         *
         * @param objectIntMap L'instance de l'association sur laquelle l'entrée est associée.
         * @param key          La clé de l'entrée.
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
