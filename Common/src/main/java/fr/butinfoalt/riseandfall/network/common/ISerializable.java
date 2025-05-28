package fr.butinfoalt.riseandfall.network.common;

import java.io.IOException;

/**
 * Interface pour les objets qui peuvent être sérialisés en un flux de données.
 */
public interface ISerializable {
    /**
     * Sérialise l'objet en un flux de données.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    void toBytes(WriteHelper writeHelper) throws IOException;
}
