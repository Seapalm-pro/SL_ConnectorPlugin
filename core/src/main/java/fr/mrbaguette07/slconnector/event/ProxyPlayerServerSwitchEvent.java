package fr.mrbaguette07.slconnector.event;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur change de serveur sur le proxy
 */
public class ProxyPlayerServerSwitchEvent extends ProxyEvent {
    private final String playerName;
    private final UUID playerId;
    private final String fromServer;
    private final String toServer;

    public ProxyPlayerServerSwitchEvent(String playerName, UUID playerId, String fromServer, String toServer) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.fromServer = fromServer;
        this.toServer = toServer;
    }

    /**
     * Obtient le nom du joueur
     * 
     * @return Le nom du joueur
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Obtient l'UUID du joueur
     * 
     * @return L'UUID du joueur
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Obtient le nom du serveur actuel (d'où le joueur vient)
     * 
     * @return Le nom du serveur actuel
     */
    public String getCurrentServer() {
        return fromServer;
    }

    /**
     * Obtient le nom du serveur de destination
     * 
     * @return Le nom du serveur de destination
     */
    public String getTargetServer() {
        return toServer;
    }
}
