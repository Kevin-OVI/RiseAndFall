package fr.butinfoalt.riseandfall.server.orders;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.server.ServerPlayer;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

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
     * Exécute les attaques entre les joueurs.
     * Pour chaque joueur, applique les pertes des attaquants, vérifie si la cible est éliminée,
     * et applique les dégâts sur les unités et bâtiments de la cible.
     */
    public void executeAttacks() {
        for (Map.Entry<Player, Map<ServerPlayer, ObjectIntMap<UnitType>>> entry : this.attacksTowards.entrySet()) {
            Player target = entry.getKey();
            Map<ServerPlayer, ObjectIntMap<UnitType>> attacksTowardsTarget = entry.getValue();
            ObjectIntMap<UnitType> defenseUnits = this.defenseUnits.get(target);
            AttackManager attackManager = new AttackManager(target, defenseUnits, attacksTowardsTarget);
            attackManager.applyAttackersLosses();
            if (attackManager.isTargetEliminated()) {
                // Si le joueur est éliminé, on supprime ses bâtiments et unités
                target.getBuildingMap().reset();
                target.getUnitMap().reset();
                continue;
            }
            attackManager.applyDamageOnTargetUnits();
            attackManager.applyDamageOnBuildings();
        }
    }
}
