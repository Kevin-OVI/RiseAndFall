package fr.butinfoalt.riseandfall.gamelogic;

/**
 * Représente les différentes races disponibles dans le jeu.
 * Chaque race possède des caractéristiques et avantages spécifiques.
 */
public enum Race {
    /**
     * Race humaine, équilibrée et polyvalente.
     */
    HUMAIN("Humain", "Race équilibrée avec des compétences militaires et économiques"),

    /**
     * Race des morts-vivants, spécialisée dans la résilience et les attaques magiques.
     */
    MORT_VIVANT("Mort-vivant", "Race focalisée sur la magie noire et la résurrection");

    private final String displayName;
    private final String description;

    Race(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Renvoie le nom d'affichage de la race.
     *
     * @return Le nom d'affichage de la race.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Renvoie la description de la race.
     *
     * @return La description de la race.
     */
    public String getDescription() {
        return description;
    }
}
