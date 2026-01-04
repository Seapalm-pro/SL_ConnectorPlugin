package fr.mrbaguette07.slconnector.event;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur se déconnecte du proxy
 */
public class ProxyPlayerDisconnectEvent extends ProxyEvent {
    private final String playerName;
    private final UUID playerId;

    public ProxyPlayerDisconnectEvent(String playerName, UUID playerId) {
        this.playerName = playerName;
        this.playerId = playerId;
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
}
