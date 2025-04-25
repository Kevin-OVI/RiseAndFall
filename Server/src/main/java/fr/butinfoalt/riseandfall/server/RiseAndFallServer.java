package fr.butinfoalt.riseandfall.server;

import com.mysql.cj.jdbc.Driver;

public class RiseAndFallServer {
    /**
     * Charge le driver MySQL pour la connexion à la base de données.
     * Cette méthode doit être appelée avant d'utiliser la base de données.
     */
    private static void loadMysqlDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        loadMysqlDriver();
        // Code à implémenter en temps voulu
    }
}
