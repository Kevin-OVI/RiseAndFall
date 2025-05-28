package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au serveur pour effectuer une action simple dans le jeu.
 */
public class PacketGameAction implements IPacket {
    /**
     * L'action à effectuer
     */
    private final Action action;

    /**
     * Constructeur du paquet d'action de jeu
     *
     * @param action L'action à effectuer
     */
    public PacketGameAction(Action action) {
        this.action = action;
    }

    /**
     * Constructeur du paquet d'action de jeu pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketGameAction(ReadHelper readHelper) throws IOException {
        this.action = Action.values()[readHelper.readInt()];
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.action.ordinal());
    }

    /**
     * Récupère l'action à effectuer
     *
     * @return L'action à effectuer
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Enumération des actions possibles
     */
    public enum Action {
        QUIT_GAME, LOG_OUT, NEXT_TURN;
    }
}
