package fr.butinfoalt.riseandfall.front.description;

import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.*;
import fr.butinfoalt.riseandfall.util.Iterables;
import fr.butinfoalt.riseandfall.util.function.ToFloatFunction;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.function.Function;

/**
 * Contrôleur pour la vue de description.
 */
public class DescriptionController {
    /**
     * Champ pour le composant de l'image de fond.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant de la barre de défilement.
     */
    @FXML
    public ScrollPane textScrollPane;

    /**
     * Champ pour le composant de la liste des races
     */
    @FXML
    public TextFlow racesList;

    /**
     * Champ pour le composant de la liste des bâtiments
     */
    @FXML
    public TextFlow buildingsList;

    /**
     * Champ pour le composant de la liste des unités
     */
    @FXML
    public TextFlow unitsList;

    /**
     * Retourne une map contenant les valeurs numériques par défaut à afficher pour les objets achetables.
     *
     * @param <T> Type des objets achetables
     * @return Map des valeurs numériques par défaut
     */
    private static <T extends PurchasableItem> Map<String, NumericItemDetail<T>> getDefaultNumericValuesGetter() {
        return Map.of(
                "Prix", new NumericItemDetail<>(PurchasableItem::getPrice, GenreNombre.MASCULIN_SINGULIER),
                "Intelligence requise", new NumericItemDetail<>(PurchasableItem::getRequiredIntelligence, GenreNombre.FEMININ_SINGULIER)
        );
    }

    /**
     * Constructeur de texte pour les objets achetables.
     *
     * @param items               Liste des objets achetables
     * @param numericValuesGetter Map des valeurs numériques spécifiques pour ces objets
     * @param detailsAdder        Fonction pour ajouter des détails supplémentaires à chaque objet
     * @param universalTitle      Titre à afficher pour les objets non spécifiques à une race
     * @param raceTitleTemplate   Modèle de titre pour les objets spécifiques à une race
     * @param textFlow            Composant TextFlow où le texte sera ajouté
     * @param <T>                 Type des objets achetables
     */
    private static <T extends PurchasableItem> void constructPurchasableItemText(List<T> items, Map<String, NumericItemDetail<T>> numericValuesGetter, Function<T, List<String>> detailsAdder, String universalTitle, String raceTitleTemplate, TextFlow textFlow) {
        Map<String, NumericItemDetail<T>> defaultNumericValuesGetter = getDefaultNumericValuesGetter();
        Iterable<Map.Entry<String, NumericItemDetail<T>>> allNumericValuesGetter = Iterables.concat(defaultNumericValuesGetter.entrySet(), numericValuesGetter.entrySet());
        Map<String, Float> maxValues = new HashMap<>();
        for (T item : items) {
            for (Map.Entry<String, NumericItemDetail<T>> entry : allNumericValuesGetter) {
                maxValues.merge(entry.getKey(), entry.getValue().valueGetter().applyAsFloat(item), Math::max);
            }
        }

        Map<Race, StringBuilder> textMap = new HashMap<>();
        for (T item : items) {
            StringBuilder builder = textMap.computeIfAbsent(item.getAccessibleByRace(), k -> new StringBuilder());
            builder.append(item.getName()).append("\n");
            List<String> details = new ArrayList<>(Collections.singletonList(
                    item.getDescription()
            ));
            for (Map.Entry<String, NumericItemDetail<T>> entry : allNumericValuesGetter) {
                String key = entry.getKey();
                NumericItemDetail<T> detail = entry.getValue();
                float value = detail.valueGetter().applyAsFloat(item);
                if (value > 0) {
                    String ratio = displayRatio(value, maxValues.get(key), detail.genreNombre());
                    details.add(String.format("%s : %s (%s)", key, ratio, UIUtils.displayOptimisedFloat(value)));
                }
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

    /**
     * Formate un modificateur bonus/malus et l'ajoute à la liste des modificateurs.
     *
     * @param modifiersList Liste des modificateurs à laquelle ajouter le formaté
     * @param value         Valeur du modificateur (1.0 pour aucun bonus/malus, >1.0 pour bonus, <1.0 pour malus)
     * @param unit          Unité du modificateur (par exemple, "d'or", "d'intelligence", etc.)
     */
    private static void formatModifier(List<String> modifiersList, float value, String unit) {
        if (value > 1) {
            float percentage = (value - 1) * 100;
            modifiersList.addFirst(String.format("+%s%% %s", UIUtils.displayOptimisedFloat(percentage), unit));
        } else if (value < 1 && value >= 0) {
            float percentage = (1 - value) * 100;
            modifiersList.add(String.format("-%s%% %s", UIUtils.displayOptimisedFloat(percentage), unit));
        }
    }

    /**
     * Affiche le ratio d'une valeur par rapport à un maximum, en utilisant les genres et noms appropriés.
     *
     * @param value       La valeur à afficher
     * @param max         La valeur maximale pour le ratio
     * @param genreNombre Le genre et nom à utiliser pour l'affichage
     * @return Une chaîne de caractères représentant le ratio
     */
    private static String displayRatio(float value, float max, GenreNombre genreNombre) {
        float ratio = value / max;
        if (ratio < 0.1f) {
            return genreNombre.veryLow;
        } else if (ratio < 0.3f) {
            return genreNombre.low;
        } else if (ratio < 0.6f) {
            return genreNombre.medium;
        } else if (ratio < 0.9f) {
            return genreNombre.high;
        } else {
            return genreNombre.veryHigh;
        }
    }

    /**
     * Configure la scène avec l'image de fond et les composants de texte.
     *
     * @param scene La scène à configurer
     */
    public void setupScene(Scene scene) {
        UIUtils.setBackgroundImage("images/map.jpg", scene, this.backgroundImageView);
    }

    /**
     * Méthode d'initialisation appelée après le chargement du fichier FXML.
     * Elle construit la liste des races, bâtiments et unités à partir des données du serveur.
     */
    @FXML
    public void initialize() {
        for (Race race : ServerData.getRaces()) {
            Text titleText = new Text(race.getName() + " :\n");
            titleText.setStyle("-fx-font-size: 120%");
            this.racesList.getChildren().add(titleText);
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
            this.racesList.getChildren().add(contentText);
        }

        constructPurchasableItemText(ServerData.getBuildingTypes(), Map.of(
                "Production d'or", new NumericItemDetail<>(BuildingType::getGoldProduction, GenreNombre.FEMININ_SINGULIER),
                "Production d'intelligence", new NumericItemDetail<>(BuildingType::getIntelligenceProduction, GenreNombre.FEMININ_SINGULIER),
                "Résistance", new NumericItemDetail<>(BuildingType::getResistance, GenreNombre.FEMININ_SINGULIER),
                "Capacité de formation d'unités", new NumericItemDetail<>(BuildingType::getMaxUnits, GenreNombre.FEMININ_SINGULIER)
        ), buildingType -> {
            List<String> details = new ArrayList<>();
            if (buildingType.getInitialAmount() > 0) {
                details.add("Chaque joueur en possède " + buildingType.getInitialAmount() + " au début.");
            }
            if (buildingType.isDefensive()) {
                details.add("Première défense contre les attaques ennemies");
            }
            return details;
        }, "Bâtiments universels", "Bâtiments de la race %s", this.buildingsList);

        constructPurchasableItemText(ServerData.getUnitTypes(), Map.of(
                "Dégâts", new NumericItemDetail<>(UnitType::getDamage, GenreNombre.MASCULIN_PLURIEL),
                "Vie", new NumericItemDetail<>(UnitType::getHealth, GenreNombre.FEMININ_SINGULIER)
        ), unitType -> Collections.emptyList(), "Unités universelles", "Unités de la race %s", this.unitsList);
    }

    /**
     * Représentation d'un détail numérique pour un objet achetable.
     *
     * @param valueGetter Fonction pour obtenir la valeur numérique de l'objet
     * @param genreNombre Genre et nombre à utiliser pour l'affichage
     * @param <T>
     */
    private record NumericItemDetail<T extends PurchasableItem>(
            ToFloatFunction<T> valueGetter, GenreNombre genreNombre) {
    }

    /**
     * Enumération pour les genres et noms utilisés dans l'affichage des ratios.
     * Chaque genre et nombre a des noms spécifiques pour les valeurs très faibles, faibles, moyennes, élevées et très élevées.
     */
    private enum GenreNombre {
        MASCULIN_SINGULIER("très faible", "faible", "moyen", "élevé", "très élevé"),
        FEMININ_SINGULIER("très faible", "faible", "moyenne", "élevée", "très élevée"),
        MASCULIN_PLURIEL("très faibles", "faibles", "moyens", "élevés", "très élevés"),
        FEMININ_PLURIEL("très faibles", "faibles", "moyennes", "élevées", "très élevées");

        private final String veryLow;
        private final String low;
        private final String medium;
        private final String high;
        private final String veryHigh;

        GenreNombre(String veryLow, String low, String medium, String high, String veryHigh) {
            this.veryLow = veryLow;
            this.low = low;
            this.medium = medium;
            this.high = high;
            this.veryHigh = veryHigh;
        }
    }
}
