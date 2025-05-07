package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au client lorsqu'il rejoint une partie, ou lorsqu'il s'authentifie pour l'informer des parties qu'il a déjà rejointes.
 */
public class PacketInitialGameData<G extends Game, P extends Player> implements IPacket {
    private final G game;
    private final P player;

    /**
     * Constructeur du paquet de données initiales de la partie
     *
     * @param game   La partie
     * @param player Le joueur
     */
    public PacketInitialGameData(G game, P player) {
        this.game = game;
        this.player = player;
    }

    /**
     * Sérialise le paquet en un flux de données.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        this.game.toBytes(writeHelper);
        this.player.toBytes(writeHelper);
    }

    /**
     * Récupère la partie
     *
     * @return La partie
     */
    public G getGame() {
        return this.game;
    }

    /**
     * Récupère le joueur
     *
     * @return Le joueur
     */
    public P getPlayer() {
        return this.player;
    }
}
