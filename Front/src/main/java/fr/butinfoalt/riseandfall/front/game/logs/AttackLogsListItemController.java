package fr.butinfoalt.riseandfall.front.game.logs;

import fr.butinfoalt.riseandfall.front.components.TitleLabel;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackResult;
import fr.butinfoalt.riseandfall.gamelogic.data.BuildingType;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

public class AttackLogsListItemController {
    @FXML
    public TitleLabel title;

    @FXML
    public VBox attacksLogs;

    @FXML
    public VBox eliminatedPlayersLogs;

    public void init(int turn, CurrentClientPlayer currentPlayer, List<AttackResult> attackResults, List<Player> eliminatedPlayers) {
        this.title.setText("Actions du tour n°" + turn);
        ObservableList<Node> attacksLogsChildren = this.attacksLogs.getChildren();
        ObservableList<Node> eliminatedPlayersLogsChildren = this.eliminatedPlayersLogs.getChildren();

        if (attackResults.isEmpty()) {
            attacksLogsChildren.add(new Label("Vous n'avez été impliqué dans aucune attaque durant ce tour."));
        } else {
            for (AttackResult attackResult : attackResults) {
                TitleLabel title;
                if (attackResult.getAttacker() == currentPlayer) {
                    title = new TitleLabel("Vous avez attaqué " + ((OtherClientPlayer) attackResult.getTarget()).getName());
                } else {
                    title = new TitleLabel("Vous avez été attaqué par " + ((OtherClientPlayer) attackResult.getAttacker()).getName());
                }
                title.setStyle("-fx-font-size: 80%;");
                attacksLogsChildren.add(title);

                StringBuilder builder = new StringBuilder();
                if (attackResult.getDestroyedUnits().isEmpty()) {
                    builder.append("Aucune unité n'a été détruite\n");
                } else {
                    builder.append("Unités détruites :\n");
                    for (ObjectIntMap.Entry<UnitType> entry : attackResult.getDestroyedUnits()) {
                        if (entry.getValue() > 0) {
                            builder.append("- ").append(entry.getValue()).append(" ").append(entry.getKey().getName()).append("\n");
                        }
                    }
                }
                if (attackResult.getDestroyedBuildings().isEmpty()) {
                    builder.append("Aucun bâtiment n'a été détruit\n");
                } else {
                    builder.append("Bâtiments détruits :\n");
                    for (ObjectIntMap.Entry<BuildingType> entry : attackResult.getDestroyedBuildings()) {
                        if (entry.getValue() > 0) {
                            builder.append("- ").append(entry.getValue()).append(" ").append(entry.getKey().getName()).append("\n");
                        }
                    }
                }
                if (attackResult.getLostUnits().isEmpty()) {
                    builder.append("Aucune unité n'a été perdue\n");
                } else {
                    builder.append("Unités perdues :\n");
                    for (ObjectIntMap.Entry<UnitType> entry : attackResult.getLostUnits()) {
                        if (entry.getValue() > 0) {
                            builder.append("- ").append(entry.getValue()).append(" ").append(entry.getKey().getName()).append("\n");
                        }
                    }
                }
                attacksLogsChildren.add(new Label(builder.toString()));
            }

        }

        if (eliminatedPlayers.isEmpty()) {
            eliminatedPlayersLogsChildren.add(new Label("Aucun joueur n'a été éliminé durant ce tour."));
        } else {
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
    }
}
