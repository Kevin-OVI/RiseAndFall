package fr.butinfoalt.riseandfall.util;

import fr.butinfoalt.riseandfall.network.common.ISerializable;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Représente une version de l'application sous la forme d'un tableau d'entiers.
 * Chaque entier représente une partie de la version (majeur, mineur, correctif, build, révision).
 * Elle implémente l'interface Comparable pour permettre la comparaison entre différentes versions.
 * Elle implémente également l'interface ISerializable pour permettre la sérialisation de la version.
 */
public class Version implements Comparable<Version>, ISerializable {
    /**
     * Version actuelle de l'application. (pas encore utilisée)
     */
    public static final Version CURRENT_VERSION = new Version(0, 0, 0);

    /**
     * Version de l'application.
     * La version est représentée par un tableau d'entiers, où chaque entier représente une partie de la version.
     */
    private final int[] version;

    /**
     * Constructeur de la classe Version.
     *
     * @param version Un tableau d'entiers représentant la version de l'application.
     */
    public Version(int... version) {
        if (version.length > 5) {
            throw new IllegalArgumentException("Version array cannot be longer than 5");
        }
        if (Arrays.stream(version).anyMatch(i -> i < 0)) {
            throw new IllegalArgumentException("Version parts cannot be negative");
        }
        if (version.length < 1) {
            throw new IllegalArgumentException("Version array cannot be empty");
        }
        int requiredLength = 1;
        for (int i = 1; i < version.length; i++) {
            if (version[i] != 0) {
                requiredLength = i + 1;
            }
        }
        if (version.length > requiredLength) {
            this.version = Arrays.copyOf(version, requiredLength);
        } else {
            this.version = version;
        }
    }

    /**
     * Constructeur de la classe Version à partir d'une chaîne de caractères.
     *
     * @param version La chaîne de caractères représentant la version de l'application.
     */
    public Version(String version) {
        this(Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray());
    }

    /**
     * Constructeur de la classe Version pour le protocole réseau.
     *
     * @param readHelper L'instance de ReadHelper utilisée pour lire les données de la version.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des données.
     */
    public Version(ReadHelper readHelper) throws IOException {
        this.version = readHelper.readIntArray();
    }

    /**
     * Récupère la partie de la version à l'index spécifié.
     *
     * @param index L'index de la partie de la version à récupérer.
     * @return La partie de la version à l'index spécifié.
     */
    private int getPart(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative");
        }
        if (index >= version.length) {
            return 0;
        }
        return version[index];
    }

    /**
     * Récupère la version majeure.
     *
     * @return La version majeure.
     */
    public int getMajor() {
        return this.getPart(0);
    }

    /**
     * Récupère la version mineure.
     *
     * @return La version mineure.
     */
    public int getMinor() {
        return this.getPart(1);
    }

    /**
     * Récupère la version corrective.
     *
     * @return La version corrective.
     */
    public int getPatch() {
        return this.getPart(2);
    }

    /**
     * Récupère la version de build.
     *
     * @return La version de build.
     */
    public int getBuild() {
        return this.getPart(3);
    }

    /**
     * Récupère la version de révision.
     *
     * @return La version de révision.
     */
    public int getRevision() {
        return this.getPart(4);
    }

    /**
     * Sérialise la version en utilisant l'instance de WriteHelper fournie.
     *
     * @param writeHelper L'instance de WriteHelper utilisée pour écrire les données de la version.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'écriture des données.
     */
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeIntArray(this.version);
    }

    /**
     * Compare cette version à une autre version.
     *
     * @param o L'autre version à comparer.
     * @return -1 si cette version est inférieure à l'autre version, 0 si elles sont égales, et 1 si cette version est supérieure à l'autre version.
     */
    @Override
    public int compareTo(Version o) {
        int length = Math.max(this.version.length, o.version.length);
        for (int i = 0; i < length; i++) {
            int compare = Integer.compare(this.getPart(i), o.getPart(i));
            if (compare != 0) {
                return compare;
            }
        }
        return Integer.compare(this.version.length, o.version.length);
    }

    /**
     * Vérifie si cette version est égale à une autre version.
     *
     * @param o L'autre objet à comparer.
     * @return true si les deux versions sont égales, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version1 = (Version) o;
        return Objects.deepEquals(this.version, version1.version);
    }

    /**
     * Calcule le code de hachage de cette version.
     *
     * @return Le code de hachage de cette version.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.version);
    }

    /**
     * Représente la version sous forme de chaîne de caractères.
     *
     * @return La version sous forme de chaîne de caractères.
     */
    @Override
    public String toString() {
        return Arrays.stream(this.version).mapToObj(String::valueOf).collect(Collectors.joining("."));
    }
}
