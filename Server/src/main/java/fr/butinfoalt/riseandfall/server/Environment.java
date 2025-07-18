package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.util.logging.LogManager;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Classe pour gérer les variables d'environnement de l'application.
 * Utilise la bibliothèque dotenv pour charger les variables d'un fichier .env.
 */
public class Environment {
    public static final int SERVER_PORT, DB_PORT;
    public static final String SERVER_HOST, DB_HOST, DB_NAME, DB_USER, DB_PASSWORD;
    public static final boolean DEBUG_MODE;

    static {
        Dotenv dotenv = Dotenv.load();

        SERVER_HOST = dotenv.get("SERVER_HOST");
        SERVER_PORT = Integer.parseInt(dotenv.get("SERVER_PORT"));
        DB_HOST = dotenv.get("DB_HOST");
        DB_PORT = Integer.parseInt(dotenv.get("DB_PORT"));
        DB_NAME = dotenv.get("DB_NAME");
        DB_USER = dotenv.get("DB_USER");
        DB_PASSWORD = dotenv.get("DB_PASSWORD");
        String debugMode = dotenv.get("DEBUG_MODE");
        DEBUG_MODE = debugMode != null && debugMode.equalsIgnoreCase("true");
        if (DEBUG_MODE) {
            LogManager.logMessage("Mode debug activé !");
        }
    }
}
