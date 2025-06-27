package fr.butinfoalt.riseandfall.front;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Properties;

public class Environment {
    public static int SERVER_PORT = 15868;
    public static InetAddress SERVER_HOST;
    public static Path AUTH_TOKEN_FILE;
    public static boolean DEBUG_MODE;

    private static String getNextArg(String[] args, int index) {
        if (index + 1 < args.length) {
            return args[index + 1];
        } else {
            throw new IllegalArgumentException("Missing value for argument: " + args[index]);
        }
    }

    private static File getAppDataPath() {
        File file;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            file = new File(System.getProperty("user.home"), "AppData\\Roaming\\RiseAndFall");
        } else if (os.contains("mac")) {
            file = new File(System.getProperty("user.home"), "Library/Application Support/RiseAndFall");
        } else {
            file = new File(System.getProperty("user.home"), "RiseAndFall");
        }
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("Could not create app data directory: " + file.getAbsolutePath());
        }
        return file;
    }

    public static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--auth-token-file" -> {
                    AUTH_TOKEN_FILE = Path.of(getNextArg(args, i));
                    i++;
                }
                case "--host" -> {
                    try {
                        SERVER_HOST = InetAddress.getByName(getNextArg(args, i));
                    } catch (UnknownHostException e) {
                        throw new IllegalArgumentException("Invalid server host: " + args[i + 1], e);
                    }
                    i++;
                }
                case "--port" -> {
                    try {
                        SERVER_PORT = Integer.parseInt(getNextArg(args, i));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid port number: " + args[i + 1], e);
                    }
                    i++;
                }
                case "--debug" -> DEBUG_MODE = true;
                default -> throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }
    }

    static {
        File appDataPath = getAppDataPath();
        Properties prop = new Properties();
        for (File file : new File[]{
                new File(appDataPath, "riseandfall.properties"),
                new File("riseandfall.properties")
        }) {
            try (FileReader reader = new FileReader(file)) {
                prop.load(reader);
            } catch (IOException ignored) {
            }
        }
        try {
            SERVER_HOST = InetAddress.getByName(prop.getProperty("server.host", "10.22.27.1"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        SERVER_PORT = Integer.parseInt(prop.getProperty("server.port", "15868"));
        String authTokenFile = prop.getProperty("auth.token.file");
        if (authTokenFile == null || authTokenFile.isEmpty()) {
            AUTH_TOKEN_FILE = new File(appDataPath, "auth_token.txt").toPath();
        } else {
            AUTH_TOKEN_FILE = new File(authTokenFile).toPath();
        }
        DEBUG_MODE = Boolean.parseBoolean(prop.getProperty("debug.mode", "false"));
    }
}
