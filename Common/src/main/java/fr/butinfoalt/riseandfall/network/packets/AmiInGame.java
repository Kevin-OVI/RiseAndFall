package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Un paquet envoyé au client lors de sa connexion au serveur.
 * Il contient les données statiques du serveur, c'est-à-dire les races,
 * les types d'unités et les types de bâtiments.
 */
public class AmiInGame implements IPacket {
    /**
     * La partie de jeu en cours.
     */
    private final boolean isInGame;

    /**
     * Constructeur de la classe Game.
     */
    public AmiInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public AmiInGame(ReadHelper readHelper) throws IOException {
        this.isInGame = readHelper.readBoolean();
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeBoolean(this.isInGame);
    }

    /**
     * Retourne si le joueur est dans une partie de jeu.
     *
     * @return true si le joueur est dans une partie de jeu, false sinon
     */
    public boolean isInGame() {
        return isInGame;
    }
}