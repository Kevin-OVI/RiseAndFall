package fr.butinfoalt.riseandfall.front;

import fr.butinfoalt.riseandfall.util.logging.LogManager;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Environment {
    public static final int SERVER_PORT;
    public static final InetAddress SERVER_HOST;
    public static String authTokenFile = "auth_token.txt";
    public static final boolean DEBUG_MODE;

    static {
        Dotenv dotenv = Dotenv.load();

        try {
            SERVER_HOST = InetAddress.getByName(dotenv.get("SERVER_HOST"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        SERVER_PORT = Integer.parseInt(dotenv.get("SERVER_PORT"));
        String debugMode = dotenv.get("DEBUG_MODE");
        DEBUG_MODE = debugMode != null && debugMode.equalsIgnoreCase("true");
        if (DEBUG_MODE) {
            LogManager.logMessage("Mode debug activé !");
        }
    }
}
