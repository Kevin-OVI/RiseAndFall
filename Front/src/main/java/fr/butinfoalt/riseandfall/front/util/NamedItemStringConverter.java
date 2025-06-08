package fr.butinfoalt.riseandfall.front.util;

import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import javafx.util.StringConverter;

/**
 * Convertit un objet de type NamedItem en une chaîne de caractères.
 * Utilisé pour afficher les éléments dans les choix de l'interface utilisateur.
 *
 * @param <T> le type d'objet qui étend NamedItem
 */
public class NamedItemStringConverter<T extends NamedItem> extends StringConverter<T> {
    /**
     * Convertit un objet de type T en une chaîne de caractères.
     */
    @Override
    public String toString(T item) {
        if (item == null) {
            return "Non sélectionné";
        }
        return item.getName();
    }

    /**
     * Convertit une chaîne de caractères en un objet de type T.
     * Cette méthode n'est pas utilisée dans le contexte actuel, donc n'est pas implémentée.
     *
     * @param string la chaîne à convertir
     * @return l'objet converti
     */
    @Override
    public T fromString(String string) {
        // This method is not used in the current context.
        throw new UnsupportedOperationException("Conversion from string is not supported.");
    }
}
