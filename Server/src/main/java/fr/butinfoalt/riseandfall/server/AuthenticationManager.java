package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.gamelogic.Game;
import fr.butinfoalt.riseandfall.gamelogic.Player;
import fr.butinfoalt.riseandfall.gamelogic.data.Race;
import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Classe responsable de la gestion de l'authentification des clients.
 * Elle traite les paquets d'authentification reçus du client et gère les tokens.
 */
public class AuthenticationManager {
    /**
     * Instance du serveur.
     * Utilisée pour accéder aux fonctionnalités du serveur.
     */
    private final RiseAndFallServer server;

    /**
     * Map des connexions des utilisateurs.
     * Utilisée pour associer un socket à un utilisateur.
     */
    private final Map<SocketWrapper, User> userConnections = new HashMap<>();

    /**
     * Constructeur de la classe AuthenticationManager.
     *
     * @param server Instance du serveur.
     */
    public AuthenticationManager(RiseAndFallServer server) {
        this.server = server;
    }

    /**
     * Fonction pour hacher un mot de passe.
     * @param password
     * @return
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fonction pour generer un token d'authentification.
     */
    public String generateTokenToUser(User user) {
        StringBuilder token = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            int randomChar = random.nextInt(26) + 'a';
            token.append((char) randomChar);
        }
        try (PreparedStatement statement = server.getDb().prepareStatement("INSERT INTO user_token (user_id, token) VALUES (?, ?)")) {
            statement.setInt(1, user.getId());
            statement.setString(2, token.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token.toString();
    }

    /**
     * Fonction pour recuperer la liste des utilisateurs et leurs passwords.
     */
    public int isValidUser(String username, String password) {

        String hashedPassword = hashPassword(password);
        System.out.println("Mot de passe haché : " + hashedPassword);
        try {
            try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM user WHERE username = ? AND password_hash = ?")) {
                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Méthode appelée lors de la réception d'un paquet d'authentification.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet d'authentification reçu.
     */
    public synchronized void onAuthentification(SocketWrapper sender, PacketAuthentification packet) {
        if (this.userConnections.containsKey(sender)) {
            try {
                sender.sendPacket(new PacketError("Une erreur est survenu, redémarrez votre application", "Authentification"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        String username = packet.getUsername();
        String password = packet.getPasswordHash();
        int userId = isValidUser(username, password);
        if (userId != -1)
        {
            User user = server.getUserManager().getUser(userId);

            this.userConnections.put(sender, user);
            try {
                sender.sendPacket(new PacketToken(generateTokenToUser(user)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sendGamePacket(sender);
        } else {
            try {
                sender.sendPacket(new PacketError("Identifiant ou Mot de passe incorrect", "Authentification"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Fonction pour recuperer un utilisateur à partir d'un token.
     */
    public User getUserFromToken(String token) {
        try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM user JOIN user_token ON user.id = user_token.user_id WHERE token = ?")) {
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return server.getUserManager().getUser(resultSet.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Méthode appelée lors de la réception d'un paquet de token.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de token reçu.
     */
    public void onTokenAuthentification(SocketWrapper sender, PacketToken packet) {
        if (this.userConnections.containsKey(sender)) {
            try {
                sender.sendPacket(new PacketError("Une erreur est survenu, redémarrez votre application", "Authentification"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

        String token = packet.getToken();
        User user = getUserFromToken(token);
        if (user == null) {
            try {
                sender.sendPacket(new PacketError("Identifiant ou Mot de passe incorrect", "Authentification"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return;
        }
        this.userConnections.put(sender, user);
        try {
            sender.sendPacket(packet);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        sendGamePacket(sender);
    }

    public boolean isUserExist(String username) {
        try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM user WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserFromUsername(String username) {
        try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM user WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return server.getUserManager().getUser(resultSet.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getIDFromUsername(String username) {
        try (PreparedStatement statement = server.getDb().prepareStatement("SELECT * FROM user WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onRegister(SocketWrapper sender, PacketRegister packetRegister) {
        if (this.userConnections.containsKey(sender)) {
            try {
                sender.sendPacket(new PacketError("Une erreur est survenu, redémarrez votre application", "Register"));
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String username = packetRegister.getUsername();
        String password = packetRegister.getPasswordHash();

        if (isUserExist(username)) {
            try {
                sender.sendPacket(new PacketError("Ce nom d'utilisateur existe déjà", "Register"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        String hashedPassword = hashPassword(password);

        try (PreparedStatement statement = server.getDb().prepareStatement("INSERT INTO user (username, password_hash) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.getUserManager().addUser(new User(getIDFromUsername(username), username));

        User user = getUserFromUsername(username);
        if (user == null) {
            try {
                sender.sendPacket(new PacketError("Une erreur est survenu, redémarrez votre application", "Register"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        this.userConnections.put(sender, user);
        try {
            sender.sendPacket(new PacketToken(generateTokenToUser(user)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendGamePacket(sender);
    }

    /**
     * Méthode pour obtenir l'utilisateur associé à un socket.
     *
     * @param sender Le socket du client.
     * @return L'utilisateur associé au socket, ou null si aucun utilisateur n'est trouvé.
     */
    public synchronized User getUser(SocketWrapper sender) {
        return this.userConnections.get(sender);
    }

    /**
     * Méthode pour obtenir la liste des connexions associées à un utilisateur.
     *
     * @param user L'utilisateur dont on veut obtenir les connexions.
     * @return La liste des connexions associées à l'utilisateur.
     */
    public synchronized List<SocketWrapper> getConnectionsFor(User user) {
        List<SocketWrapper> connections = new ArrayList<>();
        for (Map.Entry<SocketWrapper, User> entry : this.userConnections.entrySet()) {
            if (entry.getValue().equals(user)) {
                connections.add(entry.getKey());
            }
        }
        return connections;
    }

    /**
     * Méthode appelée lorsque la connexion d'un client est perdue ou qu'il se déconnecte.
     *
     * @param client Le wrapper de socket qui se déconnecte.
     */
    public synchronized void onClientDisconnected(SocketWrapper client) {
        this.userConnections.remove(client);
    }

    public ServerPlayer getPlayerFromUser(User user) {
        ServerGame[] games = server.getGameManager().getGames();
        for (ServerGame game : games) {
            Collection<ServerPlayer> players = game.getPlayers();
            for (ServerPlayer player : players) {
                if (player.getUser().equals(user)) {
                    return player;
                }
            }
        }
        return null;
    }

    public Game getGameFromUser(User user) {
        ServerGame[] games = server.getGameManager().getGames();
        for (ServerGame game : games) {
            Collection<ServerPlayer> players = game.getPlayers();
            for (ServerPlayer player : players) {
                if (player.getUser().equals(user)) {
                    return game;
                }
            }
        }
        return null;
    }

    public void sendGamePacket(SocketWrapper sender) {
        User user = this.userConnections.get(sender);
        if (user != null) {
            ServerPlayer player = getPlayerFromUser(user);
            Game game = getGameFromUser(user);
            if (player != null) {
                try {
                    sender.sendPacket(new PacketInitialGameData<>(game, player));
                    sender.sendPacket(new PacketUpdateGameData(player.getGame(), player));
                    server.getGameManager().addConnectionToGame(player, sender);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                sender.sendPacket(new PacketError("Une erreur est survenu, redémarrez votre application", "Authentification"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
