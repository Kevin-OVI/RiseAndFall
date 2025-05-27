package fr.butinfoalt.riseandfall.front.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;

import java.io.IOException;

/**
 * Représente un joueur dans le jeu du coté client.
 * Hérite de la classe Player commune.
 */
public class ClientPlayer extends Player {
    /**
     * Constructeur de la classe ClientPlayer.
     * Le joueur coté client est créé à partir des données reçues du serveur.
     *
     * @param readHelper L'outil de lecture pour désérialiser les données du joueur.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public ClientPlayer(ReadHelper readHelper) throws IOException {
        super(readHelper.readInt(), Identifiable.getById(RiseAndFall.getServerData().races(), readHelper.readInt()));
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
        ServerData<ClientGame> serverData = RiseAndFall.getServerData();
        this.setGoldAmount(readHelper.readInt());
        this.setIntelligence(readHelper.readInt());
        int size = this.buildingMap.size();
        for (int i = 0; i < size; i++) {
            BuildingType buildingType = Identifiable.getById(serverData.buildingTypes(), readHelper.readInt());
            this.buildingMap.set(buildingType, readHelper.readInt());
        }
        size = this.unitMap.size();
        for (int i = 0; i < size; i++) {
            UnitType unitType = Identifiable.getById(serverData.unitTypes(), readHelper.readInt());
            this.unitMap.set(unitType, readHelper.readInt());
        }
        this.updatePendingOrders(deserializeOrders(readHelper));
    }
}
