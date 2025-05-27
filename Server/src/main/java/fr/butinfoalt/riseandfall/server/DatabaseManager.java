package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.util.logging.LogManager;
import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static fr.butinfoalt.riseandfall.server.Environment.*;
import static fr.butinfoalt.riseandfall.server.Environment.DB_PASSWORD;
import static fr.butinfoalt.riseandfall.server.Environment.DB_USER;

/**
 * Classe qui gère la connexion à la base de données.
 * Elle permet de se connecter à la base de données, de vérifier la validité de la connexion
 * et de fermer la connexion lorsque nécessaire.
 */
public class DatabaseManager implements AutoCloseable {
    /**
     * La connexion à la base de données.
     */
    private Connection db;

    /**
     * Constructeur de la classe DatabaseManager.
     * Il charge le driver MySQL et établit une connexion à la base de données.
     */
    public DatabaseManager() {
        loadMysqlDriver();
        this.db = connectToDatabase();
    }

    /**
     * Méthode pour charger le driver MySQL.
     * Elle lève une exception si le driver n'est pas trouvé.
     */
    private static void loadMysqlDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable.", e);
        }
    }

    /**
     * Méthode pour établir une connexion à la base de données.
     *
     * @return La connexion à la base de données.
     */
    private static Connection connectToDatabase() {
        String url = "jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";

        try {
            Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            if (conn.isValid(2)) {
                LogManager.logMessage("Connexion à la base de données établie.");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base.", e);
        }
    }

    /**
     * Méthode pour vérifier si la connexion à la base de données est toujours valide.
     *
     * @return true si la connexion est valide, false sinon.
     */
    private boolean checkDatabaseConnection() {
        if (this.db == null) {
            return false;
        }
        try {
            return this.db.isValid(2);
        } catch (SQLException e) {
            LogManager.logError("Erreur lors de la vérification de la connexion à la base de données.", e);
            return false;
        }
    }

    /**
     * Méthode pour obtenir la connexion à la base de données, et la recréer si besoin.
     *
     * @return La connexion à la base de données.
     */
    public Connection getDb() {
        if (this.checkDatabaseConnection()) {
            return this.db;
        }
        LogManager.logMessage("Connexion à la base de données invalide, tentative de reconnexion...");
        return this.db = connectToDatabase();
    }

    /**
     * Méthode pour fermer la connexion à la base de données.
     */
    public void close() {
        if (this.db != null) {
            try {
                this.db.close();
            } catch (SQLException e) {
                LogManager.logError("Erreur lors de la fermeture de la connexion à la base de données.", e);
            }
        }
    }
}
