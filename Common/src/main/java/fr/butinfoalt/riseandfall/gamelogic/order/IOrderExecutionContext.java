package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

/**
 * Interface pour le contexte d'exécution des ordres, présente de manière commune comme les classes des ordres.
 */
public interface IOrderExecutionContext {
    /**
     * Ajoute une attaque entre deux joueurs avec les unités utilisées.
     *
     * @param attacker   Le joueur attaquant
     * @param target     Le joueur cible de l'attaque
     * @param usingUnits Les unités utilisées pour l'attaque, association de leur type d'unité à leur quantité.
     */
    void addAttack(Player attacker, Player target, ObjectIntMap<UnitType> usingUnits);
}
