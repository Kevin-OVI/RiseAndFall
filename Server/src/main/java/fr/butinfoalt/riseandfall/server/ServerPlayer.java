package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.server.orders.OrderExecutionContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.ToStringFormatter;

/**
 * Représente un joueur dans le serveur.
 * Hérite de la classe Player commune. Il est associé à un utilisateur et à une partie.
 */
public class ServerPlayer extends Player {
    /**
     * Utilisateur associé au joueur.
     */
    private final User user;
    /**
     * Partie associée au joueur.
     */
    private final ServerGame game;

    /**
     * Constructeur de la classe Player.
     *
     * @param id   Identifiant du joueur dans la base de données.
     * @param user L'utilisateur associé au joueur.
     * @param race La race choisie par le joueur.
     */
    public ServerPlayer(int id, User user, ServerGame game, Race race) {
        super(id, race);
        this.user = user;
        this.game = game;
    }

    /**
     * Exécute les ordres en attente pour le joueur.
     */
    public void executeOrders(OrderExecutionContext context) {
        float addGold = 0, addIntelligence = 0;
        for (ObjectIntMap.Entry<BuildingType> entry : this.buildingMap) {
            addGold += entry.getValue() * entry.getKey().getGoldProduction();
            addIntelligence += entry.getValue() * entry.getKey().getIntelligenceProduction();
        }

        this.addGoldAmount(addGold * this.getRace().getGoldMultiplier());
        this.addIntelligence(addIntelligence * this.getRace().getIntelligenceMultiplier());

        for (BaseOrder order : this.pendingOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this, context);
                this.removeGoldAmount(order.getPrice());
            }
        }
        this.pendingOrders.clear();
    }

    /**
     * Récupère l'utilisateur associé au joueur.
     *
     * @return L'utilisateur associé au joueur.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Récupère la partie associée au joueur.
     *
     * @return La partie associée au joueur.
     */
    public ServerGame getGame() {
        return this.game;
    }

    @Override
    public ToStringFormatter toStringFormatter() {
        return super.toStringFormatter()
                .add("user.id", this.user.getId())
                .add("game.id", this.game.getId());
    }
}
