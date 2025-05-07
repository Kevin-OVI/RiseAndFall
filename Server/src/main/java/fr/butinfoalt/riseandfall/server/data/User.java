package fr.butinfoalt.riseandfall.server.data;

import fr.butinfoalt.riseandfall.gamelogic.data.Identifiable;

/**
 * Classe représentant un utilisateur.
 * Elle implémente l'interface Identifiable pour fournir un identifiant unique à chaque utilisateur.
 */
public class User implements Identifiable {
    /**
     * Identifiant de l'utilisateur dans la base de données.
     */
    private final int id;
    /**
     * Nom d'utilisateur.
     */
    private final String username;

    /**
     * Constructeur de la classe User.
     *
     * @param id       Identifiant de l'utilisateur dans la base de données.
     * @param username Nom d'utilisateur.
     */
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Récupère l'identifiant de l'utilisateur dans la base de données.
     *
     * @return L'identifiant de l'utilisateur dans la base de données.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Récupère le nom d'utilisateur.
     *
     * @return Le nom d'utilisateur.
     */
    public String getUsername() {
        return this.username;
    }
}
