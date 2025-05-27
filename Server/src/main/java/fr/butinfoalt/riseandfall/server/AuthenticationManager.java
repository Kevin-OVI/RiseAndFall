package fr.butinfoalt.riseandfall.server;

import fr.butinfoalt.riseandfall.network.common.SocketWrapper;
import fr.butinfoalt.riseandfall.network.packets.*;
import fr.butinfoalt.riseandfall.network.packets.PacketError.ErrorType;
import fr.butinfoalt.riseandfall.server.data.ServerGame;
import fr.butinfoalt.riseandfall.server.data.User;
import fr.butinfoalt.riseandfall.util.logging.LogManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Classe responsable de la gestion de l'authentification des clients.
 * Elle traite les paquets d'authentification reçus du client et gère les tokens.
 */
public class AuthenticationManager {
    /**
     * Alphabet utilisé pour générer les tokens d'authentification.
     */
    private static final char[] TOKEN_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /**
     * Générateur de nombres aléatoires sécurisé pour la génération de tokens.
     */
    private static final SecureRandom SRNG = new SecureRandom();

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
     *
     * @param password Mot de passe à hacher.
     * @return Le mot de passe haché.
     */
    private static String hashPassword(String password) {
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
     * Génère un token d'authentification pour un utilisateur et l'enregistre dans la base de données.
     *
     * @param user L'utilisateur pour lequel le token est généré.
     * @return Le token généré.
     */
    private String generateTokenToUser(User user) {
        StringBuilder tokenBuilder = new StringBuilder(32);
        for (int i = 0; i < 32; i++) {
            tokenBuilder.append(TOKEN_ALPHABET[SRNG.nextInt(TOKEN_ALPHABET.length)]);
        }
        String token = tokenBuilder.toString();
        try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO user_token (user_id, token) VALUES (?, ?)")) {
            statement.setInt(1, user.getId());
            statement.setString(2, token);
            statement.executeUpdate();
        } catch (Exception e) {
            LogManager.logError("Erreur lors de la sauvegarde du token en base de données", e);
        }
        return token;
    }

    /**
     * Fonction pour récupérer un utilisateur à partir de son nom d'utilisateur et de son mot de passe.
     *
     * @param username Nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'utilisateur associé, ou null si aucun utilisateur n'est trouvé.
     */
    private User getUserFromCredentials(String username, String password) {
        String hashedPassword = hashPassword(password);
        System.out.println("Mot de passe haché : " + hashedPassword);
        try {
            try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT id FROM user WHERE username = ? AND password_hash = ?")) {
                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new User(resultSet.getInt("id"), username);
                }
            }
        } catch (Exception e) {
            LogManager.logError("Erreur lors de la récupération de l'utilisateur", e);
        }
        return null;
    }

    /**
     * Fonction pour recuperer un utilisateur à partir d'un token.
     *
     * @param token Le token d'authentification.
     * @return L'utilisateur associé au token, ou null si aucun utilisateur n'est trouvé.
     */
    private User getUserFromToken(String token) {
        try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT user.id, user.username FROM user JOIN user_token ON user.id = user_token.user_id WHERE token = ?")) {
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("user.id"), resultSet.getString("user.username"));
            }
        } catch (Exception e) {
            LogManager.logError("Erreur lors de la récupération de l'utilisateur à partir du token", e);
        }
        return null;
    }

    /**
     * Vérifie si un nom d'utilisateur est déjà utilisé.
     *
     * @param username Le nom d'utilisateur à vérifier.
     * @return true si le nom d'utilisateur est déjà utilisé, false sinon.
     */
    private boolean isUsernameInUse(String username) {
        try (PreparedStatement statement = this.server.getDb().prepareStatement("SELECT * FROM user WHERE username = ? LIMIT 1")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            LogManager.logError("Erreur lors de la vérification du nom d'utilisateur", e);
        }
        return false;
    }

    /**
     * Fonction pour créer un nouvel utilisateur.
     *
     * @param username Nom d'utilisateur.
     * @param password Mot de passe.
     * @return L'utilisateur créé, ou null si une erreur survient.
     */
    private User createUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        try (PreparedStatement statement = this.server.getDb().prepareStatement("INSERT INTO user(username, password_hash) VALUES (?, ?) RETURNING id")) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                System.out.println(resultSet.getInt("id"));
                return new User(resultSet.getInt("id"), username);
            }
            LogManager.logError("Erreur lors de la création de l'utilisateur, aucun id retourné");
        } catch (Exception e) {
            LogManager.logError("Erreur lors de la création de l'utilisateur", e);
        }
        return null;
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
                sender.sendPacket(new PacketError(ErrorType.LOGIN_GENERIC_ERROR));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message d'erreur", e);
            }
            return;
        }
        String username = packet.getUsername();
        String password = packet.getPasswordHash();
        User user = getUserFromCredentials(username, password);
        if (user == null) {
            try {
                sender.sendPacket(new PacketError(ErrorType.LOGIN_INVALID_CREDENTIALS));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message d'erreur", e);
            }
            return;
        }
        this.userConnections.put(sender, user);
        try {
            sender.sendPacket(new PacketToken(generateTokenToUser(user)));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du token", e);
        }
        sendGamePacket(sender, user);
    }

    /**
     * Méthode appelée lors de la réception d'un paquet de token.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet de token reçu.
     */
    public void onTokenAuthentification(SocketWrapper sender, PacketToken packet) {
        String token = packet.getToken();
        User user = getUserFromToken(token);
        if (user == null) {
            try {
                sender.sendPacket(new PacketError(ErrorType.LOGIN_INVALID_SESSION));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message d'erreur", e);
            }
            return;
        }
        this.userConnections.put(sender, user);
        try {
            sender.sendPacket(packet);
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du token", e);
        }
        sendGamePacket(sender, user);
    }

    /**
     * Méthode appelée lors de la réception d'un paquet d'enregistrement.
     *
     * @param sender Le socket du client qui a envoyé le paquet.
     * @param packet Le paquet d'enregistrement reçu.
     */
    public void onRegister(SocketWrapper sender, PacketRegister packet) {
        String username = packet.getUsername();
        String password = packet.getPasswordHash();

        if (isUsernameInUse(username)) {
            try {
                sender.sendPacket(new PacketError(ErrorType.REGISTER_USERNAME_TAKEN));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message d'erreur", e);
            }
            return;
        }

        User user = createUser(username, password);
        if (user == null) {
            try {
                sender.sendPacket(new PacketError(ErrorType.REGISTER_GENERIC_ERROR));
            } catch (IOException e) {
                LogManager.logError("Erreur lors de l'envoi du message d'erreur", e);
            }
            return;
        }
        server.getUserManager().addUser(user);
        this.userConnections.put(sender, user);
        try {
            sender.sendPacket(new PacketToken(generateTokenToUser(user)));
        } catch (IOException e) {
            LogManager.logError("Erreur lors de l'envoi du token", e);
        }
        sendGamePacket(sender, user);
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
                if (player.getUser().getId() == user.getId()) {
                    return player;
                }
            }
        }
        return null;
    }

    public void sendGamePacket(SocketWrapper sender, User user) {
        ServerPlayer player = getPlayerFromUser(user);
        if (player != null) {
            ServerGame game = player.getGame();
            try {
                sender.sendPacket(new PacketInitialGameData<>(game, player));
                sender.sendPacket(new PacketUpdateGameData(game, player));
                server.getGameManager().addConnectionToGame(player, sender);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
