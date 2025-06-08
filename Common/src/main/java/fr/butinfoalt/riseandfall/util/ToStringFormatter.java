package fr.butinfoalt.riseandfall.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe utilitaire pour formater une chaîne de caractères représentant un objet.
 * La représentation est de la forme "ClassName{field1=value1, field2=value2, ...}".
 */
public class ToStringFormatter {
    /**
     * Nom de la classe de l'objet.
     */
    private final String className;
    /**
     * Map pour stocker les noms de champs et leurs valeurs.
     * Utilise LinkedHashMap pour maintenir l'ordre d'insertion.
     */
    private final Map<String, String> fields = new LinkedHashMap<>();

    /**
     * Constructeur de la classe ToStringFormatter.
     *
     * @param className Nom de la classe de l'objet.
     */
    public ToStringFormatter(String className) {
        this.className = className;
    }

    /**
     * Ajoute un champ de type boolean à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, boolean value) {
        this.fields.put(fieldName, Boolean.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type byte à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, byte value) {
        this.fields.put(fieldName, Byte.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type short à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, short value) {
        this.fields.put(fieldName, Short.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type int à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, int value) {
        this.fields.put(fieldName, Integer.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type long à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, long value) {
        this.fields.put(fieldName, Long.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type float à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, float value) {
        this.fields.put(fieldName, Float.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type double à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, double value) {
        this.fields.put(fieldName, Double.toString(value));
        return this;
    }

    /**
     * Ajoute un champ de type char à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, char value) {
        this.fields.put(fieldName, Character.toString(value));
        return this;
    }

    /**
     * Ajoute un champ d'un autre type à la représentation de l'objet.
     *
     * @param fieldName Nom du champ.
     * @param value     Valeur du champ.
     * @return Instance courante de ToStringFormatter.
     */
    public ToStringFormatter add(String fieldName, Object value) {
        String stringValue;
        if (value == null) {
            stringValue = "null";
        } else if (value instanceof String s) {
            stringValue = '"' + s + '"';
        } else if (value.getClass().isArray()) {
            stringValue = Arrays.deepToString((Object[]) value);
        } else {
            stringValue = value.toString();
        }

        this.fields.put(fieldName, stringValue);
        return this;
    }

    /**
     * Construit la chaîne de caractères représentant l'objet.
     * La chaîne est de la forme "ClassName{field1=value1, field2=value2, ...}".
     *
     * @return La chaîne de caractères représentant l'objet.
     */
    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.className).append("{");
        if (!this.fields.isEmpty()) {
            for (Map.Entry<String, String> entry : this.fields.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
            sb.setLength(sb.length() - 2); // Retire la dernière virgule et l'espace
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return new ToStringFormatter("ToStringFormatter")
                .add("className", this.className)
                .add("fields", this.fields)
                .build();
    }
}
