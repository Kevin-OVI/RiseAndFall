package fr.butinfoalt.riseandfall.server.orders;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackResult;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.server.ServerPlayer;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Le contexte d'exécution des attaques entre les joueurs.
 */
public class AttacksExecutionContext {
    /**
     * Unités de défense restantes pour chaque joueur.
     * La clé est le joueur, et la valeur est une association des types d'unités avec leur quantité restante.
     */
    private final Map<Player, ObjectIntMap<UnitType>> defenseUnits = new HashMap<>();

    /**
     * Attaques effectuées par les joueurs vers d'autres joueurs.
     * La clé est le joueur cible, et la valeur est une association des attaquants avec les unités utilisées pour l'attaque.
     */
    private final Map<Player, Map<ServerPlayer, ObjectIntMap<UnitType>>> attacksTowards = new HashMap<>();

    /**
     * Résultats des attaques effectuées par les joueurs.
     */
    private final ArrayList<AttackResult> attackResults = new ArrayList<>();

    /**
     * Constructeur de l'exécution des ordres.
     * Initialise le contexte d'exécution avec la partie et les unités de défense pour chaque joueur.
     *
     * @param game La partie sur laquelle les ordres sont exécutés.
     */
    public AttacksExecutionContext(ServerGame game) {
        for (ServerPlayer player : game.getPlayers()) {
            this.defenseUnits.put(player, player.getUnitMap().clone());
        }
    }

    /**
     * Ajoute une attaque entre deux joueurs avec les unités utilisées.
     * Met à jour les unités de défense restantes pour l'attaquant et les unités utilisées pour l'attaque.
     *
     * @param attacker   Le joueur attaquant
     * @param target     Le joueur cible de l'attaque
     * @param usingUnits Les unités utilisées pour l'attaque, association de leur type d'unité à leur quantité.
     */
    public void addAttack(ServerPlayer attacker, Player target, ObjectIntMap<UnitType> usingUnits) {
        ObjectIntMap<UnitType> remaining = this.defenseUnits.get(attacker);
        ObjectIntMap<UnitType> attackUnits = this.attacksTowards.computeIfAbsent(target, p -> new HashMap<>()).computeIfAbsent(attacker, p -> p.getUnitMap().createEmptyClone());
        remaining.decrement(usingUnits);
        attackUnits.increment(usingUnits);
    }

    /**
     * Exécute toutes les attaques enregistrées entre les joueurs.
     * On exécute les attaques par ordre de cible, puis pour chaque attaquant vers cette cible.
     */
    public void executeAttacks() {
        // On parcourt les attaques enregistrées par cible.
        for (Map.Entry<Player, Map<ServerPlayer, ObjectIntMap<UnitType>>> entry : this.attacksTowards.entrySet()) {
            Player target = entry.getKey();
            ObjectIntMap<UnitType> defenseUnits = this.defenseUnits.get(target);
            float targetHealthMultiplier = target.getRace().getHealthMultiplier();
            float totalDefenseDamage = calculateUnitsAttackDamage(defenseUnits, target.getRace());

            // On calcule la quantité totale d'unités utilisées pour l'attaque, ainsi que le nombre d'unités utilisées par chaque attaquant.
            int totalUsingUnitsCount = 0;
            HashMap<Player, Integer> usingUnitsCountByPlayer = new HashMap<>();
            for (Map.Entry<ServerPlayer, ObjectIntMap<UnitType>> attackerEntry : entry.getValue().entrySet()) {
                int usingUnitsCount = attackerEntry.getValue().getValues().stream().mapToInt(Integer::intValue).sum();
                totalUsingUnitsCount += usingUnitsCount;
                usingUnitsCountByPlayer.put(attackerEntry.getKey(), usingUnitsCount);
            }

            // On trie les bâtiments de la cible en deux catégories : défensifs et non défensifs.
            ArrayList<BuildingType> defensiveBuildings = new ArrayList<>();
            ArrayList<BuildingType> nonDefensiveBuildings = new ArrayList<>();
            for (BuildingType buildingType : target.getBuildingMap().getKeys()) {
                if (buildingType.isDefensive()) {
                    defensiveBuildings.add(buildingType);
                } else {
                    nonDefensiveBuildings.add(buildingType);
                }
            }
            // On initialise les DamageAppliers pour les unités et bâtiments de la cible, qui ne changent pas pour toutes les attaques envers elle.
            DamageApplier<BuildingType> targetDefensiveBuildingsDamageApplier = new DamageApplier<>(target.getBuildingMap().clone(defensiveBuildings), BuildingType::getResistance);
            DamageApplier<UnitType> targetUnitsDamageApplier = new DamageApplier<>(defenseUnits, unitType -> unitType.getHealth() * targetHealthMultiplier);
            DamageApplier<BuildingType> targetBuildingsDamageApplier = new DamageApplier<>(target.getBuildingMap().clone(nonDefensiveBuildings), BuildingType::getResistance);

            // On exécute chacune des attaques envers cette cible.
            for (Map.Entry<ServerPlayer, ObjectIntMap<UnitType>> attackerEntry : entry.getValue().entrySet()) {
                float defenseDamage = (totalDefenseDamage * usingUnitsCountByPlayer.get(attackerEntry.getKey())) / totalUsingUnitsCount;
                this.executeAttack(attackerEntry.getKey(), target, targetDefensiveBuildingsDamageApplier, targetUnitsDamageApplier, targetBuildingsDamageApplier, attackerEntry.getValue(), defenseDamage);
            }
        }
    }

    /**
     * Exécute une attaque d'un joueur vers un autre joueur.
     * Applique les dégâts sur les unités et bâtiments de la cible, et met à jour les unités perdues par l'attaquant.
     *
     * @param attacker                              Le joueur attaquant
     * @param target                                Le joueur cible de l'attaque
     * @param targetDefensiveBuildingsDamageApplier L'appliqueur de dégâts pour les bâtiments de défense de la cible
     * @param targetUnitsDamageApplier              L'appliqueur de dégâts pour les unités de la cible
     * @param targetBuildingsDamageApplier          L'appliqueur de dégâts pour les bâtiments de la cible
     * @param usingUnits                            Les unités utilisées pour l'attaque, association de leur type d'unité à leur quantité.
     * @param defenseDamage                         La quantité de dégâts de défense infligée par les unités attaquantes, proportionnelle à leur nombre.
     */
    private void executeAttack(ServerPlayer attacker, Player target, DamageApplier<BuildingType> targetDefensiveBuildingsDamageApplier, DamageApplier<UnitType> targetUnitsDamageApplier, DamageApplier<BuildingType> targetBuildingsDamageApplier, ObjectIntMap<UnitType> usingUnits, float defenseDamage) {
        float attackerHealthMultiplier = attacker.getRace().getHealthMultiplier();
        float damage = calculateUnitsAttackDamage(usingUnits, attacker.getRace());

        // On applique les dégâts sur les unités et bâtiments de la cible.
        DamageApplier.DamageApplyResult<BuildingType> defensiveBuildingsAttackResult = targetDefensiveBuildingsDamageApplier.applyDamage(damage);
        DamageApplier.DamageApplyResult<UnitType> unitsAttackResult = targetUnitsDamageApplier.applyDamage(defensiveBuildingsAttackResult.getRemainingDamage());
        DamageApplier.DamageApplyResult<BuildingType> buildingsAttackResult = targetBuildingsDamageApplier.applyDamage(unitsAttackResult.getRemainingDamage());

        // On applique les dégâts de défense des unités attaquantes.
        DamageApplier<UnitType> attackingUnitsDamageApplier = new DamageApplier<>(usingUnits, unitType -> unitType.getHealth() * attackerHealthMultiplier);
        DamageApplier.DamageApplyResult<UnitType> attackerUnitsAttackResult = attackingUnitsDamageApplier.applyDamage(defenseDamage);

        // On récupère les unités et bâtiments détruits
        ObjectIntMap<BuildingType> destroyedBuildings = target.getBuildingMap().createEmptyClone();
        destroyedBuildings.increment(defensiveBuildingsAttackResult.getDestroyedElements());
        ObjectIntMap<UnitType> destroyedUnits = unitsAttackResult.getDestroyedElements();
        destroyedBuildings.increment(buildingsAttackResult.getDestroyedElements());
        ObjectIntMap<UnitType> lostUnits = attackerUnitsAttackResult.getDestroyedElements();

        // On met à jour les unités et bâtiments des joueurs.
        target.getUnitMap().decrement(destroyedUnits);
        target.getBuildingMap().decrement(destroyedBuildings);
        attacker.getUnitMap().decrement(lostUnits);

        // On sauvegarde le résultat de l'attaque.
        this.attackResults.add(new AttackResult(attacker, target, destroyedBuildings, destroyedUnits, lostUnits));
    }

    /**
     * Calcule les dégâts totaux infligés par des unités et de la race du joueur.
     *
     * @param units      Liste des unités et leur nombre.
     * @param playerRace La race du joueur attaquant, utilisée pour appliquer les multiplicateurs de dégâts.
     * @return La somme des dégâts infligés par toutes les unités.
     */
    private static float calculateUnitsAttackDamage(ObjectIntMap<UnitType> units, Race playerRace) {
        float totalDamage = 0;
        for (ObjectIntMap.Entry<UnitType> entry : units) {
            totalDamage += entry.getKey().getDamage() * entry.getValue();
        }
        return totalDamage * playerRace.getDamageMultiplier();
    }

    /**
     * Retourne les résultats des attaques effectuées par les joueurs.
     *
     * @return La liste des résultats d'attaques.
     */
    public ArrayList<AttackResult> getAttackResults() {
        return this.attackResults;
    }
}
