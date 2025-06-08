package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Paquet envoyé au serveur pour mettre à jour les ordres d'un joueur.
 * Il contient la liste des ordres mis à jour.
 */
public class PacketUpdateOrders implements IPacket {
    /**
     * Liste des ordres à mettre à jour.
     */
    private final ArrayList<BaseOrder> orders;

    /**
     * Constructeur du paquet de mise à jour des ordres.
     *
     * @param orders La liste des ordres mis à jour.
     */
    public PacketUpdateOrders(ArrayList<BaseOrder> orders) {
        this.orders = orders;
    }

    /**
     * Constructeur du paquet de mise à jour des ordres pour la désérialisation.
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public PacketUpdateOrders(ReadHelper readHelper) throws IOException {
        this.orders = Player.deserializeOrders(readHelper);
    }

    /**
     * Sérialise le paquet en un flux de données.
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        Player.serializeOrders(orders, writeHelper);
    }

    /**
     * Récupère la liste des ordres mis à jour.
     *
     * @return La liste des ordres mis à jour.
     */
    public ArrayList<BaseOrder> getOrders() {
        return this.orders;
    }
}
