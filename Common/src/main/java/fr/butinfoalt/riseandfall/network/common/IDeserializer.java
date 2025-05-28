package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;

/**
 * Interfae pour les objets qui peuvent être désérialisés à partir d'un flux de données.
 *
 * @param <T> Le type de l'objet désérialisé, qui doit implémenter l'interface ISerializable.
 */
public interface IDeserializer<T extends ISerializable> {
    /**
     * Désérialise un objet à partir d'un flux de données.
     *
     * @param readHelper Le helper de lecture qui fournit les méthodes pour lire les données.
     * @return L'objet désérialisé.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    T deserialize(ReadHelper readHelper) throws IOException;
}
