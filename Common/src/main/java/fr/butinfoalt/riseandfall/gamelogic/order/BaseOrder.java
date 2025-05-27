package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.ISerializable;

/**
 * Interface de base pour un ordre qui peut être donné à exécuter pour le prochain tour.
 * Chaque ordre doit pouvoir être exécuté sur un joueur, et a un prix en or.
 */
public interface BaseOrder extends ISerializable {
    /**
     * Exécute l'ordre sur le joueur donné.
     *
     * @param player Le joueur sur lequel exécuter l'ordre.
     */
    void execute(Player player);

    /**
     * Obtient le prix de l'ordre en or.
     *
     * @return Le prix de l'ordre en or.
     */
    int getPrice();
}
