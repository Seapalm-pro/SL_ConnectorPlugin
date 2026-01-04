package fr.mrbaguette07.slconnector.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Événement Bukkit déclenché lorsqu'un joueur se connecte au proxy (avant le join)
 * Compatible avec Skript et d'autres plugins Bukkit
 */
public class SLProxyPlayerConnectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private final String playerName;
    private final UUID playerId;

    public SLProxyPlayerConnectEvent(String playerName, UUID playerId) {
        super(true); // async
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
