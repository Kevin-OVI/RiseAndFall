package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;

/**
 * Interfae pour les objets qui peuvent être désérialisés à partir d'un flux de données et d'un argument de contexte.
 *
 * @param <T> Le type de l'objet désérialisé, qui doit implémenter l'interface ISerializable.
 * @param <U> Le type de l'argument de contexte utilisé lors de la désérialisation.
 */
public interface IContextDeserializer<T extends ISerializable, U> {
    /**
     * Désérialise un objet à partir d'un flux de données.
     *
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @param context    Le contexte utilisé lors de la désérialisation.
     * @return L'objet désérialisé.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    T deserialize(ReadHelper readHelper, U context) throws IOException;
}
