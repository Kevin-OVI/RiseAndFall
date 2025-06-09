package fr.butinfoalt.riseandfall.server.data;

import java.util.Random;

/**
 * Classe qui génère des noms de jeux aléatoires.
 * Elle utilise des préfixes, des lieux et des suffixes pour créer un nom de jeu unique.
 */
public class GameNameGenerator {
    /**
     * Générateur de nombres aléatoires utilisé pour sélectionner des éléments aléatoires.
     */
    private static final Random RANDOM = new Random();

    /**
     * Préfixes, lieux et suffixes utilisés pour générer des noms de jeux.
     * Ils sont choisis pour évoquer des thèmes épiques et fantastiques.
     */
    private static final String[] prefixes = {"Bataille", "Quête", "Conquête", "Siège", "Invasion", "Croisade", "Expédition", "Campagne", "Guerre"};
    private static final String[] places = {
            "d'Azsar", "de Wyrmhaven", "du Mont Chauve", "de la Rivière Noire", "du Château Fort", "de la Forêt Sombre", "des Montagnes de Glace",
            "de la Vallée des Rois", "de la Cité des Ténèbres", "des Dragons", "des Titans", "des Dieux", "des Héros", "des Monstres", "des Légendes",
            "des Mystères", "des Secrets", "des Énigmes"
    };
    private static final String[] suffixes = {
            "des Ténèbres", "de la Lumière", "du Sang", "de la Gloire", "de l'Ombre", "de la Destruction", "de la Victoire", "de la Défaite",
            "de la Renaissance"
    };

    /**
     * Génère un nom de jeu aléatoire en combinant un préfixe, un lieu et un suffixe.
     *
     * @return Un nom de jeu aléatoire.
     */
    public static String generateGameName() {
        String prefix = prefixes[RANDOM.nextInt(prefixes.length)];
        String place = places[RANDOM.nextInt(places.length)];
        String suffix = suffixes[RANDOM.nextInt(suffixes.length)];

        return prefix + " " + place + " " + suffix;
    }
}
