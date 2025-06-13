package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;

/**
 * Les données d'une attaque d'un joueur contre un autre joueur.
 */
public final class AttackPlayerOrderData implements ISerializable {
    /**
     * Le joueur cible de l'attaque.
     */
    private final Player targetPlayer;
    /**
     * Les unités utilisées pour l'attaque.
     */
    private final ObjectIntMap<UnitType> usingUnits;

    /**
     * Constructeur des données d'attaque d'un joueur.
     *
     * @param targetPlayer Le joueur cible de l'attaque.
     * @param usingUnits   Les unités utilisées pour l'attaque, association de leur type d'unité à leur quantité.
     */
    public AttackPlayerOrderData(Player targetPlayer, ObjectIntMap<UnitType> usingUnits) {
        this.targetPlayer = targetPlayer;
        this.usingUnits = usingUnits;
    }

    /**
     * Constructeur des données d'attaque d'un joueur pour la désérialisation.
     *
     * @param readHelper L'outil de lecture pour lire les données du paquet.
     * @param context    Le contexte de désérialisation des ordres.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public AttackPlayerOrderData(ReadHelper readHelper, OrderDeserializationContext context) throws IOException {
        this.targetPlayer = context.dataDeserializer().getPlayerById(readHelper.readInt());
        this.usingUnits = context.currentPlayer().getUnitMap().createEmptyClone();
        ObjectIntMap.deserialize(this.usingUnits, readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
    }

    /**
     * Sérialise les données d'attaque en un flux de données.
     *
     * @param writeHelper L'outil d'écriture pour écrire les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.targetPlayer.getId());
        ObjectIntMap.serialize(this.usingUnits, writeHelper);
    }

    /**
     * Obtient le joueur cible de l'attaque.
     *
     * @return Le joueur cible de l'attaque.
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * Obtient les unités utilisées pour l'attaque.
     *
     * @return Les unités utilisées pour l'attaque, association de leur type d'unité à leur quantité.
     */
    public ObjectIntMap<UnitType> getUsingUnits() {
        return usingUnits;
    }
}
