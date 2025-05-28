package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.order.BaseOrder;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

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
     * Quantité de gold
     */
    private int goldAmount;

    /**
     * Quantité d'intelligence
     */
    private int intelligenceAmount;

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
    public void executeOrders() {
        for (ObjectIntMap.Entry<BuildingType> entry : this.buildingMap) {
            this.addGoldAmount(entry.getValue() * entry.getKey().getGoldProduction());
            this.addIntelligence(entry.getValue() * entry.getKey().getIntelligenceProduction());
        }

        for (BaseOrder order : this.pendingOrders) {
            if (this.goldAmount >= order.getPrice()) {
                order.execute(this);
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
}
