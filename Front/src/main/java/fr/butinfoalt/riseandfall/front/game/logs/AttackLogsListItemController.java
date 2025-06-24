package fr.butinfoalt.riseandfall.front.game.logs;

import fr.butinfoalt.riseandfall.front.components.TitleLabel;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackResult;
import fr.butinfoalt.riseandfall.gamelogic.data.NamedItem;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Contrôleur pour un élément de la liste des journaux d'attaques.
 * Affiche les résultats des attaques et les joueurs éliminés pour un tour spécifique.
 */
public class AttackLogsListItemController {
    /**
     * Le label de titre pour l'élément de la liste.
     */
    @FXML
    public TitleLabel title;

    /**
     * Le conteneur pour les journaux des attaques.
     */
    @FXML
    public VBox attacksLogs;

    /**
     * Le conteneur pour les journaux des joueurs éliminés.
     */
    @FXML
    public VBox eliminatedPlayersLogs;

    private <T extends NamedItem> void displayAttackResultPart(StringBuilder builder, ObjectIntMap<T> map, String messageEmpty, String sectionTitle) {
        if (map.isEmpty()) {
            builder.append(messageEmpty).append("\n");
        } else {
            builder.append(sectionTitle).append(" :\n");
            for (ObjectIntMap.Entry<T> entry : map) {
                if (entry.getValue() > 0) {
                    builder.append("- ").append(entry.getValue()).append(" ").append(entry.getKey().getName()).append("\n");
                }
            }
        }
    }

    /**
     * Initialise l'élément de la liste avec les résultats des attaques pour un tour spécifique.
     *
     * @param currentPlayer Le joueur actuel, utilisé pour déterminer si l'attaque provient du joueur ou non.
     * @param attackResults La liste des résultats des attaques impliquant le joueur durant ce tour.
     */
    private void initAttackResults(CurrentClientPlayer currentPlayer, List<AttackResult> attackResults) {
        ObservableList<Node> attacksLogsChildren = this.attacksLogs.getChildren();
        if (attackResults.isEmpty()) {
            attacksLogsChildren.add(new Label("Vous n'avez été impliqué dans aucune attaque durant ce tour."));
            return;
        }
        for (AttackResult attackResult : attackResults) {
            TitleLabel title;
            boolean attackFromCurrentPlayer = attackResult.getAttacker() == currentPlayer;
            if (attackFromCurrentPlayer) {
                title = new TitleLabel("Vous avez attaqué " + ((OtherClientPlayer) attackResult.getTarget()).getName());
            } else {
                title = new TitleLabel("Vous avez été attaqué par " + ((OtherClientPlayer) attackResult.getAttacker()).getName());
            }
            title.setStyle("-fx-font-size: 80%;");
            attacksLogsChildren.add(title);

            StringBuilder builder = new StringBuilder();
            this.displayAttackResultPart(builder, attackResult.getDestroyedUnits(), "Aucune unité n'a été détruite", "Unités détruites");
            this.displayAttackResultPart(builder, attackResult.getDestroyedBuildings(), "Aucun bâtiment n'a été détruit", "Bâtiments détruits");
            if (attackFromCurrentPlayer) {
                this.displayAttackResultPart(builder, attackResult.getLostUnits(), "Aucune unité n'a été perdue", "Unités perdues");
            }
            attacksLogsChildren.add(new Label(builder.toString()));
        }

    }

    /**
     * Initialise l'élément de la liste avec les joueurs éliminés durant un tour spécifique.
     *
     * @param eliminatedPlayers La liste des joueurs éliminés durant ce tour.
     */
    private void initEliminatedPlayers(List<Player> eliminatedPlayers) {
        ObservableList<Node> eliminatedPlayersLogsChildren = this.eliminatedPlayersLogs.getChildren();

        if (eliminatedPlayers.isEmpty()) {
            eliminatedPlayersLogsChildren.add(new Label("Aucun joueur n'a été éliminé durant ce tour."));
            return;
        }
        eliminatedPlayersLogsChildren.add(new TitleLabel(eliminatedPlayers.size() + " joueur(s) éliminés :"));
        StringBuilder builder = new StringBuilder();
        for (Player player : eliminatedPlayers) {
            if (player instanceof OtherClientPlayer otherPlayer) {
                builder.append("- ").append(otherPlayer.getName()).append("\n");
            } else {
                Label eliminatedLabel = new Label("Vous avez été éliminé.");
                eliminatedLabel.setTextFill(Color.RED);
                eliminatedPlayersLogsChildren.add(eliminatedLabel);
            }
        }
        eliminatedPlayersLogsChildren.add(new Label(builder.toString()));
    }

    /**
     * Initialise l'élément de la liste avec les résultats des attaques et les joueurs éliminés pour un tour spécifique.
     *
     * @param turn              Le numéro du tour.
     * @param currentPlayer     Le joueur actuel.
     * @param attackResults     La liste des résultats des attaques impliquant le joueur durant ce tour.
     * @param eliminatedPlayers La liste des joueurs éliminés durant ce tour.
     */
    public void init(int turn, CurrentClientPlayer currentPlayer, List<AttackResult> attackResults, List<Player> eliminatedPlayers) {
        this.title.setText("Actions du tour n°" + turn);
        this.initAttackResults(currentPlayer, attackResults);
        this.initEliminatedPlayers(eliminatedPlayers);
    }
}
