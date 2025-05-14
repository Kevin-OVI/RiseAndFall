package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Un paquet envoyé au client lors de sa connexion au serveur.
 * Il contient les données statiques du serveur, c'est-à-dire les races,
 * les types d'unités et les types de bâtiments.
 */
public class PacketGameList implements IPacket {
    /**
     * Liste des gemes
     */
    private final Game[] games;

    /**
     * Constructeur de la classe Game.
     */
    public PacketGameList(Game[] games) {
        this.games = games;
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeSerializableArray(this.games);
    }

    /**
     * Récupère la liste des games
     *
     * @return La liste des games
     */
    public Game[] getGames() {
        return this.games;
    }

}