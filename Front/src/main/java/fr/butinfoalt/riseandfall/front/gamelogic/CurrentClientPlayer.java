package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.front.ClientDataDeserializer;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Représente le joueur courant dans le jeu du côté client.
 * Hérite de la classe ClientPlayer pour gérer les données spécifiques du joueur côté client.
 */
public class CurrentClientPlayer extends ClientPlayer {
    /**
     * Constructeur de la classe ClientPlayer.
     * Le joueur coté client est créé à partir des données reçues du serveur.
     *
     * @param readHelper L'outil de lecture pour désérialiser les données du joueur.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public CurrentClientPlayer(ReadHelper readHelper) throws IOException {
        super(readHelper.readInt(), Identifiable.getById(ServerData.getRaces(), readHelper.readInt()));
        this.updateModifiableData(readHelper);
    }

    /**
     * Méthode pour mettre à jour les données modifiables du joueur.
     * Elle est appelée lors de la désérialisation des données du joueur ou lors de la mise à jour des données.
     *
     * @param readHelper L'outil de lecture pour désérialiser les données du joueur.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public void updateModifiableData(ReadHelper readHelper) throws IOException {
        this.setGoldAmount(readHelper.readFloat());
        this.setIntelligence(readHelper.readFloat());
        ObjectIntMap.deserialize(this.getBuildingMap(), readHelper, value -> Identifiable.getById(ServerData.getBuildingTypes(), value));
        ObjectIntMap.deserialize(this.getUnitMap(), readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
        ObjectIntMap.deserialize(this.getPendingUnitsCreation(), readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
        ObjectIntMap.deserialize(this.getPendingBuildingsCreation(), readHelper, value -> Identifiable.getById(ServerData.getBuildingTypes(), value));
        int attacksSize = readHelper.readInt();
        ArrayList<AttackPlayerOrderData> pendingAttacks = new ArrayList<>(attacksSize);
        OrderDeserializationContext orderDeserializationContext = new OrderDeserializationContext(this, ClientDataDeserializer.INSTANCE);
        for (int i = 0; i < attacksSize; i++) {
            pendingAttacks.add(new AttackPlayerOrderData(readHelper, orderDeserializationContext));
        }
        this.setPendingAttacks(pendingAttacks);
    }
}
