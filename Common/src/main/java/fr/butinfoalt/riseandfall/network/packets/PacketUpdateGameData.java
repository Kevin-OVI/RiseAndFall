package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Paquet envoyé au client pour mettre à jour les données du joueur.
 * Ce paquet n'est jamais désérialisé en une instance de cette classe, mais directement dans l'objet joueur.
 */
public class PacketUpdateGameData implements IPacket {
    private final Game game;
    private final Player player;

    /**
     * Constructeur du paquet de mise à jour des données du joueur.
     *
     * @param game   La partie à mettre à jour.
     * @param player Le joueur à mettre à jour.
     */
    public PacketUpdateGameData(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    /**
     * Sérialise le paquet en un flux de données.
     *
     * @param writeHelper Le helper d'écriture qui fournit les méthodes pour écrire les données.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation.
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        this.game.serializeModifiableData(writeHelper);
        this.player.serializeModifiableData(writeHelper);
    }
}
