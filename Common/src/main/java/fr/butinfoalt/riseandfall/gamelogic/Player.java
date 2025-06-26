package fr.butinfoalt.riseandfall.gamelogic;

import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.ObjectIntMap.Entry;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Représente un joueur dans le jeu.
 * Chaque joueur a une race, une quantité d'or, une quantité d'intelligence, des bâtiments et des unités.
 * Il peut également donner des ordres pour créer des bâtiments ou des unités.
 */
public abstract class Player implements Identifiable, ISerializable {
    /**
     * Identifiant du joueur dans la base de données.
     */
    private final int id;

    /**
     * Association entre les types de bâtiments et le nombre de bâtiments de chaque type.
     */
    private final ObjectIntMap<BuildingType> buildingMap;
    /**
     * Association entre les types d'unités et le nombre d'unités de chaque type.
     */
    private final ObjectIntMap<UnitType> unitMap;
    /**
     * Ordres de création de bâtiments en attente.
     */
    private ObjectIntMap<BuildingType> pendingBuildingsCreation;
    /**
     * Ordres de créations d'unités en attente.
     */
    private ObjectIntMap<UnitType> pendingUnitsCreation;
    /**
     * Ordres d'attaque en attente.
     */
    private Collection<AttackPlayerOrderData> pendingAttacks;
    /**
     * Race du joueur (non final car elle peut être modifiée coté client)
     */
    protected Race race;
    /**
     * Quantité d'or que possède le joueur.
     * Initialisé à 50 pièces d'or au début de la partie.
     */
    private float goldAmount = 50;
    /**
     * Quantité d'intelligence que possède le joueur.
     */
    private float intelligence = 0;

    /**
     * Tour d'élimination du joueur.
     * -1 si le joueur n'est pas éliminé, sinon le tour où il a été éliminé.
     */
    private int eliminationTurn = -1;

    /**
     * Constructeur de la classe Player.
     *
     * @param race La race choisie par le joueur.
     */
    public Player(int id, Race race) {
        this.id = id;
        this.race = race;
        this.buildingMap = new ObjectIntMap<>(
                ServerData.getBuildingTypes().stream()
                        .filter(buildingType -> buildingType.getAccessibleByRace() == null || buildingType.getAccessibleByRace() == this.race)
                        .collect(Collectors.toList())
        );
        this.unitMap = new ObjectIntMap<>(
                ServerData.getUnitTypes().stream()
                        .filter(unitType -> unitType.getAccessibleByRace() == null || unitType.getAccessibleByRace() == this.race)
                        .collect(Collectors.toList())
        );
        this.pendingBuildingsCreation = this.buildingMap.createEmptyClone();
        this.pendingUnitsCreation = this.unitMap.createEmptyClone();
        this.pendingAttacks = new ArrayList<>();

        for (Entry<BuildingType> entry : this.buildingMap) {
            entry.setValue(entry.getKey().getInitialAmount());
        }
    }

    /**
     * Méthode pour obtenir l'identifiant du joueur.
     *
     * @return L'identifiant du joueur.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Méthode pour obtenir la race du joueur.
     *
     * @return La race du joueur.
     */
    public Race getRace() {
        return this.race;
    }

    /**
     * Méthode pour obtenir la quantité d'or actuelle du joueur.
     *
     * @return La quantité d'or actuelle du joueur.
     */
    public float getGoldAmount() {
        return this.goldAmount;
    }

    /**
     * Méthode pour définir la quantité d'or du joueur.
     *
     * @param goldAmount La nouvelle quantité d'or à définir.
     */
    public void setGoldAmount(float goldAmount) {
        this.goldAmount = goldAmount;
    }

    /**
     * Méthode pour ajouter une certaine quantité d'or au joueur.
     *
     * @param goldAmount La quantité d'or à ajouter.
     */
    public void addGoldAmount(float goldAmount) {
        this.goldAmount += goldAmount;
    }

    /**
     * Méthode pour retirer une certaine quantité d'or au joueur.
     *
     * @param goldAmount La quantité d'or à retirer.
     */
    public void removeGoldAmount(float goldAmount) {
        this.goldAmount -= goldAmount;
    }

    /**
     * Méthode pour obtenir la quantité d'intelligence actuelle du joueur.
     *
     * @return La quantité d'intelligence actuelle du joueur.
     */
    public float getIntelligence() {
        return this.intelligence;
    }

    /**
     * Méthode pour définir la quantité d'intelligence du joueur.
     *
     * @param intelligence La nouvelle quantité d'intelligence à définir.
     */
    public void setIntelligence(float intelligence) {
        this.intelligence = intelligence;
    }

    public void addIntelligence(float valeur) {
        this.intelligence += valeur;
    }

    /**
     * Méthode pour obtenir le tour d'élimination du joueur.
     * Si le joueur n'est pas éliminé, retourne -1.
     *
     * @return Le tour d'élimination du joueur, ou -1 s'il n'est pas éliminé.
     */
    public int getEliminationTurn() {
        return eliminationTurn;
    }

    /**
     * Méthode pour définir le tour d'élimination du joueur.
     * Utilisé pour marquer le tour où le joueur a été éliminé.
     *
     * @param eliminationTurn Le tour d'élimination à définir, ou -1 si le joueur n'est pas éliminé.
     */
    public void setEliminationTurn(int eliminationTurn) {
        this.eliminationTurn = eliminationTurn;
    }

    /**
     * Méthode pour obtenir le nombre maximum d'unités que le joueur peut avoir.
     * On additionne le nombre maximum d'unités de chaque type d'unité multiplié par le nombre d'unités de ce type.
     *
     * @return Le nombre maximum d'unités que le joueur peut avoir.
     */
    public int getAllowedUnitCount() {
        int allowedCount = 0;
        for (Entry<BuildingType> entry : this.buildingMap) {
            allowedCount += entry.getValue() * entry.getKey().getMaxUnits();
        }

        return allowedCount;
    }

    /**
     * Méthode pour obtenir la liste des bâtiments du joueur.
     *
     * @return La liste des bâtiments du joueur.
     */
    public ObjectIntMap<BuildingType> getBuildingMap() {
        return this.buildingMap;
    }

    /**
     * Méthode pour obtenir la liste des unités du joueur.
     *
     * @return La liste des unités du joueur.
     */
    public ObjectIntMap<UnitType> getUnitMap() {
        return this.unitMap;
    }

    /**
     * Méthode pour obtenir les ordres de création d'unités en attente.
     *
     * @return L'association des types d'unités avec le nombre d'unités à créer.
     */
    public ObjectIntMap<UnitType> getPendingUnitsCreation() {
        return pendingUnitsCreation;
    }

    /**
     * Méthode pour définir les ordres de création d'unités en attente.
     *
     * @param pendingUnitsCreation L'association des types d'unités avec le nombre d'unités à créer.
     */
    public void setPendingUnitsCreation(ObjectIntMap<UnitType> pendingUnitsCreation) {
        this.pendingUnitsCreation = pendingUnitsCreation;
    }

    /**
     * Méthode pour obtenir les ordres de création de bâtiments en attente.
     *
     * @return L'association des types de bâtiments avec le nombre de bâtiments à créer.
     */
    public ObjectIntMap<BuildingType> getPendingBuildingsCreation() {
        return pendingBuildingsCreation;
    }

    /**
     * Méthode pour définir les ordres de création de bâtiments en attente.
     *
     * @param pendingBuildingsCreation L'association des types de bâtiments avec le nombre de bâtiments à créer.
     */
    public void setPendingBuildingsCreation(ObjectIntMap<BuildingType> pendingBuildingsCreation) {
        this.pendingBuildingsCreation = pendingBuildingsCreation;
    }

    /**
     * Méthode pour obtenir les ordres d'attaque en attente.
     *
     * @return La collection des ordres d'attaque en attente.
     */
    public Collection<AttackPlayerOrderData> getPendingAttacks() {
        return pendingAttacks;
    }

    /**
     * Méthode pour définir les ordres d'attaque en attente.
     *
     * @param pendingAttacks La collection des ordres d'attaque à définir.
     */
    public void setPendingAttacks(Collection<AttackPlayerOrderData> pendingAttacks) {
        this.pendingAttacks = pendingAttacks;
    }

    /**
     * Méthode pour vérifier si le joueur est éliminé.
     * Un joueur est considéré comme éliminé s'il n'a plus d'unités et pas de bâtiments.
     *
     * @return true si le joueur est éliminé, false sinon.
     */
    public boolean isEliminated() {
        return this.buildingMap.isEmpty();
    }

    /**
     * Méthode pour sérialiser les données modifiables du joueur dans un flux de données.
     * On écrit d'abord la quantité d'or, puis la quantité d'intelligence,
     * ensuite on écrit chaque bâtiment et son nombre, puis chaque unité et son nombre.
     * Enfin, on sérialise les ordres en attente.
     *
     * @param writeHelper L'outil d'écriture pour sérialiser les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    public void serializeModifiableData(WriteHelper writeHelper) throws IOException {
        writeHelper.writeFloat(this.goldAmount);
        writeHelper.writeFloat(this.intelligence);
        writeHelper.writeInt(this.eliminationTurn);
        ObjectIntMap.serialize(this.buildingMap, writeHelper);
        ObjectIntMap.serialize(this.unitMap, writeHelper);
        ObjectIntMap.serialize(this.pendingUnitsCreation, writeHelper);
        ObjectIntMap.serialize(this.pendingBuildingsCreation, writeHelper);
        writeHelper.writeInt(this.pendingAttacks.size());
        for (AttackPlayerOrderData attack : this.pendingAttacks) {
            attack.toBytes(writeHelper);
        }
    }

    /**
     * Méthode pour sérialiser les données du joueur dans un flux de données.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.id);
        writeHelper.writeInt(this.race.getId());
        this.serializeModifiableData(writeHelper);
    }

    protected ToStringFormatter toStringFormatter() {
        return new ToStringFormatter(this.getClass().getSimpleName())
                .add("id", this.id)
                .add("race", this.race)
                .add("goldAmount", this.goldAmount)
                .add("intelligence", this.intelligence)
                .add("eliminationTurn", this.eliminationTurn)
                .add("buildingMap", this.buildingMap)
                .add("unitMap", this.unitMap)
                .add("pendingUnitsCreation", this.pendingUnitsCreation)
                .add("pendingBuildingsCreation", this.pendingBuildingsCreation)
                .add("pendingAttacks", this.pendingAttacks);
    }

    @Override
    public String toString() {
        return this.toStringFormatter().build();
    }
}
