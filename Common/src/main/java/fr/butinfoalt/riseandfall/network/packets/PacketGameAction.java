package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au serveur pour effectuer une action simple dans le jeu, ou au client pour lui indiquer une action à effectuer.
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
        /**
         * Envoyé au serveur pour quitter la partie en cours, ou au client pour lui indiquer que la partie a été quittée.
         */
        QUIT_GAME,
        /**
         * Envoyé au serveur pour se déconnecter de son compte.
         */
        LOG_OUT,
        /**
         * Envoyé au serveur pour passer au tour suivant.
         */
        NEXT_TURN,

        REQUEST_GAME_LIST,
        ;
    }
}
