package fr.butinfoalt.riseandfall.gamelogic.data;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Interface représentant un objet identifiable dans la base de données.
 * Chaque objet doit avoir un identifiant unique.
 */
public interface Identifiable {
    /**
     * Retourne l'identifiant de l'objet dans la base de données.
     *
     * @return L'identifiant de l'objet dans la base de données
     */
    int getId();


    /**
     * Récupère un objet identifiable par son identifiant dans un tableau.
     *
     * @param list Le tableau d'objets identifiables
     * @param id   L'identifiant de l'objet à rechercher
     * @param <T>  Le type de l'objet identifiable
     * @return Un objet {@link Optional} contenant l'objet trouvé, ou vide si aucun objet n'a été trouvé
     */
    static <T extends Identifiable> Optional<T> getOptionalById(T[] list, int id) {
        return Arrays.stream(list).filter(e -> e.getId() == id).findFirst();
    }

    /**
     * Récupère un objet identifiable par son identifiant dans un tableau.
     *
     * @param list Le tableau d'objets identifiables
     * @param id   L'identifiant de l'objet à rechercher
     * @param <T>  Le type de l'objet identifiable
     * @return L'objet trouvé
     * @throws NoSuchElementException Si aucun objet n'a été trouvé
     */
    static <T extends Identifiable> T getById(T[] list, int id) throws NoSuchElementException {
        Optional<T> foundObj = getOptionalById(list, id);
        if (foundObj.isEmpty()) {
            throw new NoSuchElementException();
        }
        return foundObj.get();
    }

    /**
     * Récupère un objet identifiable par son identifiant dans un tableau.
     *
     * @param list Le tableau d'objets identifiables
     * @param id   L'identifiant de l'objet à rechercher
     * @param <T>  Le type de l'objet identifiable
     * @return L'objet trouvé, ou null si aucun objet n'a été trouvé ou si l'identifiant est négatif
     */
    static <T extends Identifiable> T getByIdOrNull(T[] list, int id) {
        if (id < 0) return null;
        return getOptionalById(list, id).orElse(null);
    }
}
