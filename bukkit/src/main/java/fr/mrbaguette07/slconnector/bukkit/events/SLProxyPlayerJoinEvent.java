package fr.mrbaguette07.slconnector.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Événement Bukkit déclenché lorsqu'un joueur rejoint un serveur via le proxy
 */
public class SLProxyPlayerJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private final String playerName;
    private final UUID playerId;
    private final String serverName;

    public SLProxyPlayerJoinEvent(String playerName, UUID playerId, String serverName) {
        super(false);
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
