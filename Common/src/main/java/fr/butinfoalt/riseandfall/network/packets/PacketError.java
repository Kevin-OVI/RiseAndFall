package fr.butinfoalt.riseandfall.network.packets;

import fr.butinfoalt.riseandfall.network.common.IPacket;
import fr.butinfoalt.riseandfall.network.common.ReadHelper;
import fr.butinfoalt.riseandfall.network.common.WriteHelper;

import java.io.IOException;

/**
 * Packet envoyé au client pour envoyer un message d'erreur
 */
public class PacketError implements IPacket {
    private final ErrorType errorType;


    public PacketError(ErrorType errorType) {
        this.errorType = errorType;
    }

    /**
     * Constructeur du paquet d'authentification pour la désérialisation
     *
     * @param readHelper Le helper de lecture pour lire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la désérialisation
     */
    public PacketError(ReadHelper readHelper) throws IOException {
        this.errorType = ErrorType.values()[readHelper.readInt()];
    }

    /**
     * Sérialise le paquet en un flux de données
     *
     * @param writeHelper Le helper d'écriture pour écrire les données du paquet
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sérialisation
     */
    @Override
    public void toBytes(WriteHelper writeHelper) throws IOException {
        writeHelper.writeInt(this.errorType.ordinal());
    }

    /**
     * Récupère le type d'erreur
     *
     * @return Le type d'erreur
     */
    public ErrorType getErrorType() {
        return this.errorType;
    }

    /**
     * Enumération des types d'erreurs
     */
    public enum ErrorType {
        LOGIN_GENERIC_ERROR("Une erreur est survenue lors de la connexion, veuillez réessayer ou redémarrer le jeu si le problème persiste."),
        LOGIN_INVALID_CREDENTIALS("Nom d'utilisateur ou mot de passe incorrect, veuillez réessayer."),
        LOGIN_INVALID_SESSION("Votre session a expiré, veuillez vous reconnecter."),

        REGISTER_GENERIC_ERROR("Une erreur est survenue lors de l'inscription, veuillez réessayer ou redémarrer le jeu si le problème persiste."),
        REGISTER_USERNAME_TAKEN("Le nom d'utilisateur est déjà pris, veuillez en choisir un autre."),

        JOINING_GAME_GAME_NOT_FOUND("La partie n'existe pas.")

        ;

        /**
         * Message d'erreur
         */
        private final String message;

        /**
         * Constructeur de l'énumération ErrorType
         *
         * @param message Le message d'erreur
         */
        ErrorType(String message) {
            this.message = message;
        }

        /**
         * Récupère le message d'erreur
         *
         * @return Le message d'erreur
         */
        public String getMessage() {
            return this.message;
        }

        @Override
        public String toString() {
            return "ErrorType{message='%s'}".formatted(getMessage());
        }
    }
}
