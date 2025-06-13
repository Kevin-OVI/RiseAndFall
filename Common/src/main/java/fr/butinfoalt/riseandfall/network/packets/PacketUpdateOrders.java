package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;
import java.util.Collection;

/**
 * Paquet envoyé au serveur pour mettre à jour les ordres d'un joueur.
 * Il contient les unités et bâtiments en attente de création, ainsi que les ordres d'attaque en attente.
 * Chaque champ est facultatif, et peut être null pour indiquer qu'il n'y a pas de données à mettre à jour.
 */
public class PacketUpdateOrders implements IPacket {
    /**
     * Les unités à créer lors du prochain tour.
     */
    private final ObjectIntMap<UnitType> pendingUnitsCreation;
    /**
     * Les bâtiments à créer lors du prochain tour.
     */
    private final ObjectIntMap<BuildingType> pendingBuildingsCreation;
    /**
     * Les attaques à effectuer lors du prochain tour.
     */
    private final Collection<AttackPlayerOrderData> pendingAttacks;

    /**
     * Constructeur du paquet de mise à jour des ordres.
     * Chaque paramètre peut être null pour indiquer qu'il n'y a pas de données à mettre à jour.
     *
     * @param pendingUnitsCreation     Les unités à créer lors du prochain tour.
     * @param pendingBuildingsCreation Les bâtiments à créer lors du prochain tour.
     * @param pendingAttacks           Les attaques à effectuer lors du prochain tour.
     */
    public PacketUpdateOrders(ObjectIntMap<UnitType> pendingUnitsCreation, ObjectIntMap<BuildingType> pendingBuildingsCreation, Collection<AttackPlayerOrderData> pendingAttacks) {
        this.pendingUnitsCreation = pendingUnitsCreation;
        this.pendingBuildingsCreation = pendingBuildingsCreation;
        this.pendingAttacks = pendingAttacks;
    }

    /**
     * Constructeur du paquet de mise à jour des ordres pour la désérialisation.
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet.
     * @param context    Le contexte de désérialisation des ordres.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public PacketUpdateOrders(ReadHelper readHelper, OrderDeserializationContext context) throws IOException {
        boolean[] presentData = readHelper.readBooleanArray(3);

        if (presentData[0]) {
            this.pendingUnitsCreation = context.currentPlayer().getUnitMap().createEmptyClone();
            ObjectIntMap.deserialize(this.pendingUnitsCreation, readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
        } else this.pendingUnitsCreation = null;

        if (presentData[1]) {
            this.pendingBuildingsCreation = context.currentPlayer().getBuildingMap().createEmptyClone();
            ObjectIntMap.deserialize(this.pendingBuildingsCreation, readHelper, value -> Identifiable.getById(ServerData.getBuildingTypes(), value));
        } else this.pendingBuildingsCreation = null;

        if (presentData[2]) {
            this.pendingAttacks = readHelper.readSerializableList(AttackPlayerOrderData::new, context);
        } else this.pendingAttacks = null;
    }

    /**
     * Sérialise le paquet en un flux de données.
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        boolean[] presentData = new boolean[3];

        if (this.pendingUnitsCreation != null) presentData[0] = true;
        if (this.pendingBuildingsCreation != null) presentData[1] = true;
        if (this.pendingAttacks != null) presentData[2] = true;

        writeHelper.writeBooleanArray(presentData);

        if (this.pendingUnitsCreation != null) ObjectIntMap.serialize(this.pendingUnitsCreation, writeHelper);
        if (this.pendingBuildingsCreation != null) ObjectIntMap.serialize(this.pendingBuildingsCreation, writeHelper);
        if (this.pendingAttacks != null) writeHelper.writeSerializableList(this.pendingAttacks);
    }

    /**
     * Obtient les unités en attente de création.
     *
     * @return Les unités à créer lors du prochain tour, ou null si elles ne doivent pas être mises à jour.
     */
    public ObjectIntMap<UnitType> getPendingUnitsCreation() {
        return this.pendingUnitsCreation;
    }

    /**
     * Obtient les bâtiments en attente de création.
     *
     * @return Les bâtiments à créer lors du prochain tour, ou null s'ils ne doivent pas être mis à jour.
     */
    public ObjectIntMap<BuildingType> getPendingBuildingsCreation() {
        return this.pendingBuildingsCreation;
    }

    /**
     * Obtient les attaques en attente.
     *
     * @return Les attaques à effectuer lors du prochain tour, ou null si elles ne doivent pas être mises à jour.
     */
    public Collection<AttackPlayerOrderData> getPendingAttacks() {
        return this.pendingAttacks;
    }
}
