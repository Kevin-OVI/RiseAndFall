package fr.butinfoalt.riseandfall.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    /**
     * Fonction pour hacher des données en utilisant SHA-256.
     *
     * @param data Données à hacher.
     * @return Le hachage des données en tant que tableau de bytes.
     */
    public static byte[] hashData(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fonction pour hacher une chaîne de caractères en utilisant SHA-256.
     *
     * @param password Chaîne de caractères à hacher.
     * @return Le hachage de la chaîne de caractères en tant que tableau de bytes.
     */
    public static byte[] hashString(String password) {
        return hashData(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Fonction pour hacher un mot de passe.
     *
     * @param password Mot de passe à hacher.
     * @return Le mot de passe haché.
     */
    public static String hashToString(byte[] data) {
        byte[] hash = hashData(data);
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
