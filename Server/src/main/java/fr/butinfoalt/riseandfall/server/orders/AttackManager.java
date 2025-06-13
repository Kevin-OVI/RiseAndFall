package fr.butinfoalt.riseandfall.server.orders;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.server.ServerPlayer;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.util.HashMap;
import java.util.Map;

import static fr.butinfoalt.riseandfall.util.MathUtils.sumFloats;

/**
 * Classe qui gère les attaques entre joueurs.
 * Chaque instance gère les attaques dirigées vers un joueur cible.
 * Elle calcule les dégâts infligés par les attaquants et la résistance des défenses de la cible.
 */
public class AttackManager {
    /**
     * Joueur cible de l'attaque.
     */
    private final Player targetPlayer;
    /**
     * Unités de défense de la cible, associées à leur nombre.
     */
    private final ObjectIntMap<UnitType> targetDefenseUnits;
    /**
     * Attaques dirigées vers la cible, association entre les joueurs attaquants et les types d'unités utilisées par chacun d'eux.
     */
    private final Map<ServerPlayer, ObjectIntMap<UnitType>> attacksTowardsTarget;

    /**
     * Dégâts infligés par chaque attaquant
     */
    private final Map<ServerPlayer, Float> attackersDamage;

    /**
     * Santé de chaque attaquant
     */
    private final Map<ServerPlayer, Float> attackersHealth;

    /**
     * Dégâts totaux infligés par tous les attaquants
     */
    private final float totalAttackersDamage;

    /**
     * Santé totale des unités des attaquants
     */
    private final float totalAttackersUnitsHealth;

    /**
     * Dégâts totaux infligés par les unités en défense de la cible
     */
    private final float totalTargetDamage;

    /**
     * Santé totale des unités en défense de la cible
     */
    private final float totalTargetUnitsHealth;

    /**
     * Résistance totale des bâtiments de la cible
     */
    private final float totalTargetBuildingsResistance;

    /**
     * Dégâts totaux infligés par les attaquants aux unités de la cible
     */
    private final float totalAttackersDamageToUnits;

    /**
     * Dégâts totaux infligés par les attaquants aux bâtiments de la cible
     */
    private final float totalAttackersDamageToBuildings;

    /**
     * Indique si la cible a été éliminée par l'attaque.
     */
    private final boolean targetEliminated;

    /**
     * Constructeur de la classe AttackManager.
     * Il initialise les données nécessaires pour gérer les attaques vers un joueur.
     *
     * @param targetPlayer         Joueur cible de l'attaque.
     * @param targetDefenseUnits   Unités de défense de la cible, associées à leur nombre.
     * @param attacksTowardsTarget Attaques dirigées vers la cible, association entre les joueurs attaquants et les types d'unités utilisées par chacun d'eux.
     */
    public AttackManager(Player targetPlayer, ObjectIntMap<UnitType> targetDefenseUnits, Map<ServerPlayer, ObjectIntMap<UnitType>> attacksTowardsTarget) {
        this.targetPlayer = targetPlayer;
        this.targetDefenseUnits = targetDefenseUnits;
        this.attacksTowardsTarget = attacksTowardsTarget;

        this.attackersDamage = this.calculateAttackersDamage();
        this.attackersHealth = this.calculateAttackersHealth();

        this.totalAttackersDamage = sumFloats(this.attackersDamage.values());
        this.totalAttackersUnitsHealth = sumFloats(this.attackersHealth.values());

        this.totalTargetDamage = calculateUnitsAttackDamage(this.targetDefenseUnits, this.targetPlayer.getRace());
        this.totalTargetUnitsHealth = calculateUnitsHealth(this.targetDefenseUnits, this.targetPlayer.getRace());
        this.totalTargetBuildingsResistance = calculateBuildingsResistance(this.targetPlayer.getBuildingMap());

        float targetHealthAfterAttack = this.totalTargetUnitsHealth - this.totalAttackersDamage;
        if (targetHealthAfterAttack < 0) {
            this.totalAttackersDamageToUnits = this.totalTargetUnitsHealth;
            if (-targetHealthAfterAttack >= this.totalTargetBuildingsResistance) {
                this.targetEliminated = true;
                this.totalAttackersDamageToBuildings = this.totalTargetBuildingsResistance;
            } else {
                this.targetEliminated = false;
                this.totalAttackersDamageToBuildings = -targetHealthAfterAttack;
            }
        } else {
            this.totalAttackersDamageToUnits = this.totalAttackersDamage;
            this.totalAttackersDamageToBuildings = 0;
            this.targetEliminated = false;
        }
    }

    /**
     * Calcule les dégâts infligés par chaque attaquant.
     *
     * @return Une map associant chaque attaquant à ses dégâts totaux.
     */
    private Map<ServerPlayer, Float> calculateAttackersDamage() {
        Map<ServerPlayer, Float> attackersDamage = new HashMap<>();
        for (Map.Entry<ServerPlayer, ObjectIntMap<UnitType>> entry : this.attacksTowardsTarget.entrySet()) {
            ServerPlayer attacker = entry.getKey();
            attackersDamage.put(attacker, calculateUnitsAttackDamage(entry.getValue(), attacker.getRace()));
        }
        return attackersDamage;
    }

    /**
     * Calcule la santé totale des unités de chaque attaquant.
     *
     * @return Une map associant chaque attaquant à sa santé totale.
     */
    private Map<ServerPlayer, Float> calculateAttackersHealth() {
        Map<ServerPlayer, Float> attackersHealth = new HashMap<>();
        for (Map.Entry<ServerPlayer, ObjectIntMap<UnitType>> entry : this.attacksTowardsTarget.entrySet()) {
            ServerPlayer attacker = entry.getKey();
            attackersHealth.put(attacker, calculateUnitsHealth(entry.getValue(), attacker.getRace()));
        }
        return attackersHealth;
    }

    /**
     * Vérifie si la cible a été éliminée par l'attaque.
     *
     * @return true si la cible a été éliminée, false sinon.
     */
    boolean isTargetEliminated() {
        return this.targetEliminated;
    }

    /**
     * Applique les dégâts infligés par les attaquants sur les unités de la cible.
     * Les dégâts sont répartis en fonction de la vie des unités en défense de la cible et des dégâts totaux infligés par les attaquants.
     */
    void applyDamageOnTargetUnits() {
        if (this.totalAttackersDamageToUnits <= 0) {
            return;
        }
        for (ObjectIntMap.Entry<UnitType> entry : this.targetPlayer.getUnitMap()) {
            entry.decrement(crossProduct(this.totalAttackersDamageToUnits, this.totalTargetUnitsHealth, this.targetDefenseUnits.get(entry.getKey())));
        }
    }

    /**
     * Applique les dégâts infligés par les attaquants sur les bâtiments de la cible.
     * Les dégâts sont répartis en fonction de la résistance des bâtiments et des dégâts totaux infligés par les attaquants.
     */
    void applyDamageOnBuildings() {
        if (this.totalAttackersDamageToBuildings <= 0) {
            return;
        }
        for (ObjectIntMap.Entry<BuildingType> entry : this.targetPlayer.getBuildingMap()) {
            entry.decrement(crossProduct(this.totalAttackersDamageToBuildings, this.totalTargetBuildingsResistance, entry.getValue()));
        }
    }

    /**
     * Applique les pertes subies par les attaquants en fonction des dégâts infligés à la cible.
     * Les pertes sont calculées en fonction de la santé totale des unités des attaquants et des
     * dégâts totaux infligés par les unités en défense de la cible.
     */
    void applyAttackersLosses() {
        // Si aucun dégât n'est infligé par les unités de la cible, il n'y a pas de pertes à appliquer.
        if (this.totalTargetDamage <= 0) return;
        for (Map.Entry<ServerPlayer, Float> entry : this.attackersHealth.entrySet()) {
            ServerPlayer attacker = entry.getKey();
            float attackerUnitsHealth = entry.getValue();
            float damageTowardsAttacker = crossProduct(attackerUnitsHealth, this.totalAttackersUnitsHealth, this.totalTargetDamage);
            if (damageTowardsAttacker > entry.getValue()) {
                damageTowardsAttacker = entry.getValue();
            }
            ObjectIntMap<UnitType> attackerUnits = attacker.getUnitMap();
            for (ObjectIntMap.Entry<UnitType> attackingUnitEntry : this.attacksTowardsTarget.get(attacker)) {
                UnitType unitType = attackingUnitEntry.getKey();
                attackerUnits.decrement(unitType, crossProduct(damageTowardsAttacker, attackerUnitsHealth, attackingUnitEntry.getValue()));
            }
        }
    }

    /**
     * Calcule les dégâts totaux infligés par des unités et de la race du joueur.
     *
     * @param units      Liste des unités et leur nombre.
     * @param playerRace La race du joueur attaquant, utilisée pour appliquer les multiplicateurs de dégâts.
     * @return La somme des dégâts infligés par toutes les unités.
     */
    private static float calculateUnitsAttackDamage(ObjectIntMap<UnitType> units, Race playerRace) {
        float damageMultiplier = playerRace.getDamageMultiplier();
        float totalDamage = 0;
        for (ObjectIntMap.Entry<UnitType> entry : units) {
            totalDamage += entry.getKey().getDamage() * entry.getValue() * damageMultiplier;
        }
        return totalDamage;
    }

    /**
     * Calcule la santé totale des unités d'un joueur, en tenant compte de la race du joueur.
     *
     * @param units      Liste des unités et leur nombre.
     * @param playerRace La race du joueur, utilisée pour appliquer les multiplicateurs de santé.
     * @return La somme des points de vie de toutes les unités
     */
    private static float calculateUnitsHealth(ObjectIntMap<UnitType> units, Race playerRace) {
        float healthMultiplier = playerRace.getHealthMultiplier();
        float totalHealth = 0;
        for (ObjectIntMap.Entry<UnitType> entry : units) {
            totalHealth += entry.getKey().getHealth() * entry.getValue() * healthMultiplier;
        }
        return totalHealth;
    }

    /**
     * Calcule la résistance totale des bâtiments d'un joueur.
     *
     * @param buildings Liste des bâtiments et leur nombre.
     * @return La somme des résistances de tous les bâtiments.
     */
    private static float calculateBuildingsResistance(ObjectIntMap<BuildingType> buildings) {
        float totalResistance = 0;
        for (ObjectIntMap.Entry<BuildingType> entry : buildings) {
            totalResistance += entry.getKey().getResistance() * entry.getValue();
        }
        return totalResistance;
    }

    /**
     * Calcule le produit en croix d'une partie, d'un total et d'une valeur.
     *
     * @param part  La partie à utiliser dans le calcul.
     * @param total Le total à utiliser dans le calcul.
     * @param value La valeur à multiplier par le ratio part/total.
     * @return Le résultat du produit en croix.
     */
    public static float crossProduct(float part, float total, float value) {
        return (part / total) * value;
    }

    /**
     * Calcule le produit en croix d'une partie, d'un total et d'une valeur, en arrondissant à l'entier supérieur.
     *
     * @param part  La partie à utiliser dans le calcul.
     * @param total Le total à utiliser dans le calcul.
     * @param value La valeur à multiplier par le ratio part/total.
     * @return Le résultat du produit en croix, arrondi à l'entier supérieur.
     */
    public static int crossProduct(float part, float total, int value) {
        return (int) Math.ceil((part / total) * value);
    }
}