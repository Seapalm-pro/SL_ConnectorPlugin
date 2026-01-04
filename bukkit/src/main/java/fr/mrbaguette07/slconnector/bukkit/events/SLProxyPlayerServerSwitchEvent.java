package fr.mrbaguette07.slconnector.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Événement Bukkit déclenché lorsqu'un joueur change de serveur sur le proxy
 * Compatible avec Skript et d'autres plugins Bukkit
 */
public class SLProxyPlayerServerSwitchEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    private final String playerName;
    private final UUID playerId;
    private final String fromServer;
    private final String toServer;

    public SLProxyPlayerServerSwitchEvent(String playerName, UUID playerId, String fromServer, String toServer) {
        super(true); // async
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
     * Obtient le nom du serveur précédent (alias de getCurrentServer)
     * 
     * @return Le nom du serveur précédent
     */
    public String getFromServer() {
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

    /**
     * Obtient le nom du serveur de destination (alias de getTargetServer)
     * 
     * @return Le nom du serveur de destination
     */
    public String getToServer() {
        return toServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
