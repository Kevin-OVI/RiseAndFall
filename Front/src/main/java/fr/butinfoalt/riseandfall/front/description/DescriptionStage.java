package fr.butinfoalt.riseandfall.front.description;

import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.*;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Function;

/**
 * Classe représentant la scène de description du jeu.
 * Elle affiche une image de fond et un texte descriptif.
 */
public class DescriptionStage extends Stage {
    public static final DescriptionStage INSTANCE = new DescriptionStage();

    /**
     * Constructeur de la scène de description.
     * Il initialise la taille minimale de la fenêtre, défini le titre et la scène de contenu.
     */
    private DescriptionStage() {
        this.setMinWidth(256);
        this.setMinHeight(192);
        this.setTitle(View.DESCRIPTION.getWindowTitle());
        this.setScene(View.DESCRIPTION.getScene(1024, 768, this::setupScene));
    }

    private static <T extends PurchasableItem> void constructPurchasableItemText(List<T> items, Function<T, List<String>> detailsAdder, String universalTitle, String raceTitleTemplate, TextFlow textFlow) {
        Map<Race, StringBuilder> textMap = new HashMap<>();
        for (T item : items) {
            StringBuilder builder = textMap.computeIfAbsent(item.getAccessibleByRace(), k -> new StringBuilder());
            builder.append(item.getName()).append("\n");
            List<String> details = new ArrayList<>(Arrays.asList(
                    item.getDescription(),
                    "Prix : " + UIUtils.displayOptimisedFloat(item.getPrice())
            ));
            if (item.getRequiredIntelligence() > 0) {
                details.add("Intelligence requise : " + UIUtils.displayOptimisedFloat(item.getRequiredIntelligence()));
            }
            details.addAll(detailsAdder.apply(item));

            for (String detail : details) {
                builder.append("  • ").append(detail).append("\n");
            }
            builder.append("\n");
        }

        textMap.entrySet().stream().sorted((o1, o2) -> {
            if (o1.getKey() == null) {
                return -1; // Universel en premier
            } else if (o2.getKey() == null) {
                return 1; // Universel en dernier
            } else {
                return Integer.compare(o1.getKey().getId(), o2.getKey().getId());
            }
        }).forEach(entry -> {
            String title = (entry.getKey() == null ? universalTitle : String.format(raceTitleTemplate, entry.getKey().getName())) + " :\n";
            Text titleText = new Text(title);
            titleText.setStyle("-fx-font-size: 150%");
            textFlow.getChildren().add(titleText);
            Text contentText = new Text(entry.getValue().toString());
            contentText.setStyle("-fx-font-size: 20px;");
            textFlow.getChildren().add(contentText);
        });
    }

    private static void formatModifier(List<String> modifiersList, float value, String unit) {
        if (value > 1) {
            float percentage = (value - 1) * 100;
            modifiersList.addFirst(String.format("+%s %s", UIUtils.displayOptimisedFloat(percentage), unit));
        } else if (value < 1 && value >= 0) {
            float percentage = (1 - value) * 100;
            modifiersList.add(String.format("-%s %s", UIUtils.displayOptimisedFloat(percentage), unit));
        }
    }

    /**
     * Méthode pour configurer la scène de description.
     * Elle définit l'image de fond, adapte la taille de l'image à la fenêtre,
     * et centre le texte dans le ScrollPane.
     *
     * @param scene La scène à configurer.
     */
    private void setupScene(Scene scene) {
        DescriptionController controller = View.DESCRIPTION.getController();

        UIUtils.setBackgroundImage("images/map.jpg", scene, controller.backgroundImageView);


        for (Race race : ServerData.getRaces()) {
            Text titleText = new Text(race.getName() + " :\n");
            titleText.setStyle("-fx-font-size: 120%");
            controller.racesList.getChildren().add(titleText);
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("  • ").append(race.getDescription()).append("\n");
            ArrayList<String> modifiers = new ArrayList<>();
            formatModifier(modifiers, race.getGoldMultiplier(), "d'or");
            formatModifier(modifiers, race.getIntelligenceMultiplier(), "d'intelligence");
            formatModifier(modifiers, race.getDamageMultiplier(), "de dégâts");
            formatModifier(modifiers, race.getHealthMultiplier(), "de vie");
            if (!modifiers.isEmpty()) {
                contentBuilder.append("  • ").append(String.join(", ", modifiers)).append("\n");
            }
            Text contentText = new Text(contentBuilder.toString());
            contentText.setStyle("-fx-font-size: 20px;");
            controller.racesList.getChildren().add(contentText);
        }

        constructPurchasableItemText(ServerData.getBuildingTypes(), buildingType -> {
            List<String> details = new ArrayList<>();

            if (buildingType.getGoldProduction() > 0) {
                details.add("Production d'or : " + UIUtils.displayOptimisedFloat(buildingType.getGoldProduction()));
            }
            if (buildingType.getIntelligenceProduction() > 0) {
                details.add("Production d'intelligence : " + UIUtils.displayOptimisedFloat(buildingType.getIntelligenceProduction()));
            }
            if (buildingType.getResistance() > 0) {
                details.add("Résistance : " + UIUtils.displayOptimisedFloat(buildingType.getResistance()));
            }
            if (buildingType.getMaxUnits() > 0) {
                details.add("Capacité de formation d'unités : " + buildingType.getMaxUnits());
            }
            if (buildingType.getInitialAmount() > 0) {
                details.add("Chaque joueur en possède " + buildingType.getInitialAmount() + " au début.");
            }
            if (buildingType.isDefensive()) {
                details.add("Première défense contre les attaques ennemies");
            }
            return details;
        }, "Bâtiments universels", "Bâtiments de la race %s", controller.buildingsList);

        constructPurchasableItemText(ServerData.getUnitTypes(), unitType -> {
            List<String> details = new ArrayList<>();
            if (unitType.getDamage() > 0) {
                details.add("Dégâts : " + UIUtils.displayOptimisedFloat(unitType.getDamage()));
            }
            if (unitType.getHealth() > 0) {
                details.add("Vie : " + UIUtils.displayOptimisedFloat(unitType.getHealth()));
            }
            return details;
        }, "Unités universelles", "Unités de la race %s", controller.unitsList);
    }
}
