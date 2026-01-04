package fr.mrbaguette07.slconnector.event;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur rejoint un serveur sur le proxy
 */
public class ProxyPlayerJoinEvent extends ProxyEvent {
    private final String playerName;
    private final UUID playerId;
    private final String serverName;

    public ProxyPlayerJoinEvent(String playerName, UUID playerId, String serverName) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.serverName = serverName;
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
     * Obtient le nom du serveur sur lequel le joueur a rejoint
     * 
     * @return Le nom du serveur
     */
    public String getServerName() {
        return serverName;
    }
}
