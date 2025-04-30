package fr.butinfoalt.riseandfall.server;

import com.mysql.cj.jdbc.Driver;
import fr.butinfoalt.riseandfall.network.server.BaseSocketServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static fr.butinfoalt.riseandfall.server.Environment.*;

public class RiseAndFallServer extends BaseSocketServer {
    private final Connection db;

    /**
     * Constructeur de la classe BaseSocketServer.
     * Initialise le serveur socket sur le port spécifié.
     *
     * @param port       Le port sur lequel le serveur écoute les connexions des clients.
     * @param db
     * @throws IOException Si une erreur se produit lors de la création du serveur socket.
     */
    public RiseAndFallServer(int port, Connection db) throws IOException {
        super(port);
        this.db = db;
    }

    private static void loadMysqlDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable.", e);
        }
    }

    private static Connection connectToDatabase() {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";

        try {
            Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            if (conn.isValid(2)) {
                System.out.println("Connexion à la base réussie !");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base.", e);
        }
    }

    public static void main(String[] args) {
        loadMysqlDriver();

        Connection db = connectToDatabase();

        try (RiseAndFallServer server = new RiseAndFallServer(SERVER_PORT, db)) {
            server.start();
            System.out.println("Server started");
            try {
                server.join();
            } catch (InterruptedException e) {
                System.out.println("Server interrupted");
                server.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (!db.isClosed()) {
                    db.close();
                    System.out.println("Connexion fermée proprement.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
