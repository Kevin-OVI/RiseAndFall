package fr.butinfoalt.riseandfall.front;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Environment {
    public static final int SERVER_PORT;
    public static final InetAddress SERVER_HOST;
    public static String authTokenFile = "auth_token.txt";

    static {
        Dotenv dotenv = Dotenv.load();

        try {
            SERVER_HOST = InetAddress.getByName(dotenv.get("SERVER_HOST"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        SERVER_PORT = Integer.parseInt(dotenv.get("SERVER_PORT"));
    }
}
