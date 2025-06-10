package fr.butinfoalt.riseandfall.network.packets.data;

import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.DataDeserializer;

/**
 * Classe de données pour le contexte de désérialisation des ordres.
 * Contient le joueur actuel et le désérialiseur de données.
 */
public record OrderDeserializationContext(Player currentPlayer, DataDeserializer dataDeserializer) {
}
