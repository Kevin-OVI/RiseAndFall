package fr.butinfoalt.riseandfall.network.common;

/**
 * Interface pour les gestionnaires de paquets.
 * @param <T> Le type de paquet à gérer, qui doit implémenter l'interface IPacket.
 */
public interface IPacketHandler<T extends IPacket> {
    void handlePacket(SocketWrapper sender, T packet);
}
