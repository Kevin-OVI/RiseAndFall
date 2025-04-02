package fr.butinfoalt1.riseandfall.gamelogic.order;

import fr.butinfoalt1.riseandfall.gamelogic.Player;

public interface BaseOrder {
    void execute(Player player);

    int getPrice();
}
