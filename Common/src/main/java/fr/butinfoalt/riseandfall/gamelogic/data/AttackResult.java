package fr.butinfoalt.riseandfall.gamelogic.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;

/**
 * Représente les résultats d'une attaque effectuée par un joueur contre un autre joueur.
 * Contient les informations sur les bâtiments et unités détruits, ainsi que les unités perdues par l'attaquant.
 */
public class AttackResult implements ISerializable {
    /**
     * Le joueur qui a effectué l'attaque.
     */
    private final Player attacker;
    /**
     * Le joueur qui a été attaqué.
     */
    private final Player target;

    /**
     * Les bâtiments détruits par l'attaquant.
     * La clé est le type de bâtiment, et la valeur est la quantité détruite.
     */
    private final ObjectIntMap<BuildingType> destroyedBuildings;
    /**
     * Les unités détruites par l'attaquant.
     * La clé est le type d'unité, et la valeur est la quantité détruite.
     */
    private final ObjectIntMap<UnitType> destroyedUnits;
    /**
     * Les unités perdues par l'attaquant.
     * La clé est le type d'unité, et la valeur est la quantité perdue.
     */
    private final ObjectIntMap<UnitType> lostUnits;

    /**
     * Constructeur des résultats d'attaque.
     *
     * @param attacker           Le joueur qui a effectué l'attaque.
     * @param target             Le joueur qui a été attaqué.
     * @param destroyedBuildings Les bâtiments détruits par l'attaquant, association de leur type à leur quantité.
     * @param destroyedUnits     Les unités détruites par l'attaquant, association de leur type à leur quantité.
     * @param lostUnits          Les unités perdues par l'attaquant, association de leur type à leur quantité.
     */
    public AttackResult(Player attacker, Player target, ObjectIntMap<BuildingType> destroyedBuildings, ObjectIntMap<UnitType> destroyedUnits, ObjectIntMap<UnitType> lostUnits) {
        this.attacker = attacker;
        this.target = target;
        this.destroyedBuildings = destroyedBuildings;
        this.destroyedUnits = destroyedUnits;
        this.lostUnits = lostUnits;
    }

    /**
     * Constructeur des résultats d'attaque pour la désérialisation.
     *
     * @param readHelper       L'outil de lecture pour lire les données du paquet.
     * @param dataDeserializer Le désérialiseur de données pour obtenir les joueurs par ID.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public AttackResult(ReadHelper readHelper, DataDeserializer dataDeserializer) throws IOException {
        this.attacker = dataDeserializer.getPlayerById(readHelper.readInt());
        this.target = dataDeserializer.getPlayerById(readHelper.readInt());
        ObjectIntMap.deserialize(this.destroyedBuildings = this.target.getBuildingMap().createEmptyClone(), readHelper, value -> Identifiable.getById(ServerData.getBuildingTypes(), value));
        ObjectIntMap.deserialize(this.destroyedUnits = this.target.getUnitMap().createEmptyClone(), readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
        ObjectIntMap.deserialize(this.lostUnits = this.attacker.getUnitMap().createEmptyClone(), readHelper, value -> Identifiable.getById(ServerData.getUnitTypes(), value));
    }

    /**
     * Sérialise les résultats de l'attaque en un flux de données.
     *
     * @param writeHelper L'outil d'écriture pour écrire les données du paquet.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.attacker.getId());
        writeHelper.writeInt(this.target.getId());
        ObjectIntMap.serialize(this.destroyedBuildings, writeHelper);
        ObjectIntMap.serialize(this.destroyedUnits, writeHelper);
        ObjectIntMap.serialize(this.lostUnits, writeHelper);
    }

    /**
     * Obtient le joueur qui a effectué l'attaque.
     *
     * @return Le joueur attaquant.
     */
    public Player getAttacker() {
        return this.attacker;
    }

    /**
     * Obtient le joueur qui a été attaqué.
     *
     * @return Le joueur cible de l'attaque.
     */
    public Player getTarget() {
        return this.target;
    }

    /**
     * Obtient les bâtiments détruits par l'attaquant.
     *
     * @return Les bâtiments détruits, association de leur type à leur quantité.
     */
    public ObjectIntMap<BuildingType> getDestroyedBuildings() {
        return this.destroyedBuildings;
    }

    /**
     * Obtient les unités détruites par l'attaquant.
     *
     * @return Les unités détruites, association de leur type à leur quantité.
     */
    public ObjectIntMap<UnitType> getDestroyedUnits() {
        return this.destroyedUnits;
    }

    /**
     * Obtient les unités perdues par l'attaquant.
     *
     * @return Les unités perdues, association de leur type à leur quantité.
     */
    public ObjectIntMap<UnitType> getLostUnits() {
        return this.lostUnits;
    }
}
