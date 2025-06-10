package fr.butinfoalt.riseandfall.gamelogic.order;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;
import fr.butinfoalt.riseandfall.gamelogic.data.ServerData;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;
import fr.butinfoalt.riseandfall.network.packets.data.OrderDeserializationContext;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;

import java.io.IOException;

/**
 * Représente un ordre d'attaque d'un joueur contre un autre joueur.
 * Cet ordre est exécuté envers un joueur cible avec les unités spécifiées.
 */
public class OrderAttackPlayer implements BaseOrder {
    /**
     * Joueur cible de l'attaque.
     */
    private final Player targetPlayer;

    /**
     * Unités utilisées pour l'attaque, avec leur nombre.
     * La clé est le type d'unité, et la valeur est le nombre d'unités de ce type mise en œuvre.
     */
    private final ObjectIntMap<UnitType> usingUnits;

    /**
     * Constructeur de l'ordre d'attaque d'un joueur.
     *
     * @param targetPlayer Joueur cible de l'attaque.
     * @param usingUnits   Unités utilisées pour l'attaque, avec leur nombre.
     */
    public OrderAttackPlayer(Player targetPlayer, ObjectIntMap<UnitType> usingUnits) {
        this.targetPlayer = targetPlayer;
        this.usingUnits = usingUnits;
    }

    /**
     * Constructeur de l'ordre d'attaque d'un joueur à partir d'un flux de données.
     * On lit d'abord l'identifiant du joueur cible, puis les unités utilisées pour l'attaque.
     *
     * @param readHelper Outil de lecture pour désérialiser les données.
     * @param context    Contexte de désérialisation pour cet ordre.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation.
     */
    public OrderAttackPlayer(ReadHelper readHelper, OrderDeserializationContext context) throws IOException {
        this(context.dataDeserializer().getPlayerById(readHelper.readInt()), context.currentPlayer().getUnitMap().createEmptyClone());
        int size = this.usingUnits.size();
        for (int i = 0; i < size; i++) {
            UnitType unitType = Identifiable.getById(ServerData.getUnitTypes(), readHelper.readInt());
            this.usingUnits.set(unitType, readHelper.readInt());
        }
    }

    /**
     * Exécute l'ordre d'attaque sur le joueur cible.
     * La méthode appelle le contexte d'exécution pour ajouter l'attaque avec les unités utilisées,
     * car il s'agit d'une intéraction entre joueurs qui nécessite une gestion spécifique dans le contexte de la partie.
     *
     * @param player  Le joueur sur lequel exécuter l'ordre.
     * @param context Le contexte d'exécution de l'ordre, globalement utilité pour ce tour pour toute la partie.
     */
    @Override
    public void execute(Player player, IOrderExecutionContext context) {
        context.addAttack(player, this.targetPlayer, this.usingUnits);
    }

    /**
     * Obtient le prix de l'ordre en or.
     * Pour cet ordre, le prix est toujours 0 car il s'agit d'une attaque, qui ne nécessite pas de coût direct en or.
     *
     * @return Le prix de l'ordre en or (0)
     */
    @Override
    public float getPrice() {
        return 0;
    }

    /**
     * Sérialise l'ordre d'attaque en écrivant les données dans le flux.
     *
     * @param writeHelper L'outil d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.targetPlayer.getId());
        for (ObjectIntMap.Entry<UnitType> entry : this.usingUnits) {
            writeHelper.writeInt(entry.getKey().getId());
            writeHelper.writeInt(entry.getValue());
        }
    }

    /**
     * Obtient le joueur cible de l'attaque.
     *
     * @return Le joueur cible de l'attaque.
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * Obtient les unités utilisées pour l'attaque, avec leur nombre.
     * La clé est le type d'unité, et la valeur est le nombre d'unités de ce type mise en œuvre.
     *
     * @return Unités utilisées pour l'attaque.
     */
    public ObjectIntMap<UnitType> getUsingUnits() {
        return usingUnits;
    }
}
