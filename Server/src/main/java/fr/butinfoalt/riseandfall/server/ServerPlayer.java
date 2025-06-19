package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.server.orders.AttacksExecutionContext;
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
     * Prépare les attaques en attente pour le joueur.
     * Cette méthode est appelée avant l'exécution des attaques pour les ajouter au contexte d'exécution.
     * Les attaques sont exécutées avant le reste des ordres, qui eux sont exécutés dans {@link #executeOrders()}.
     *
     * @param context Le contexte d'exécution des attaques.
     */
    public void prepareAttacks(AttacksExecutionContext context) {
        for (AttackPlayerOrderData attack : this.getPendingAttacks()) {
            context.addAttack(this, attack.getTargetPlayer(), attack.getUsingUnits());
        }
        this.getPendingAttacks().clear();
    }

    /**
     * Exécute les ordres en attente pour le joueur, sauf les attaques qui sont exécutées
     * dans {@link #prepareAttacks(AttacksExecutionContext)}.
     */
    public void executeOrders() {
        float addGold = 0, addIntelligence = 0;
        for (ObjectIntMap.Entry<BuildingType> entry : this.getBuildingMap()) {
            addGold += entry.getValue() * entry.getKey().getGoldProduction();
            addIntelligence += entry.getValue() * entry.getKey().getIntelligenceProduction();
        }

        this.addGoldAmount(addGold * this.getRace().getGoldMultiplier());
        this.addIntelligence(addIntelligence * this.getRace().getIntelligenceMultiplier());

        for (ObjectIntMap.Entry<BuildingType> entry : this.getPendingBuildingsCreation()) {
            this.getBuildingMap().increment(entry.getKey(), entry.getValue());
            this.removeGoldAmount(entry.getKey().getPrice() * entry.getValue());
        }
        this.getPendingBuildingsCreation().reset();

        for (ObjectIntMap.Entry<UnitType> entry : this.getPendingUnitsCreation()) {
            this.getUnitMap().increment(entry.getKey(), entry.getValue());
            this.removeGoldAmount(entry.getKey().getPrice() * entry.getValue());
        }
        this.getPendingUnitsCreation().reset();
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
