package fr.mrbaguette07.slconnector.bukkit.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.bukkit.events.*;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.event.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Gestionnaire des événements proxy reçus via le réseau
 */
public class ProxyEventHandler {
    private final Bukkitslconnector plugin;

    public ProxyEventHandler(Bukkitslconnector plugin) {
        this.plugin = plugin;
    }

    /**
     * Gère les messages d'événements proxy reçus
     */
    public void handleMessage(Player receiver, Message message) {
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message.getData());
            String eventType = in.readUTF();
            
            switch (eventType) {
                case "ProxyPlayerConnect":
                    handleProxyPlayerConnect(in);
                    break;
                case "ProxyPlayerJoin":
                    handleProxyPlayerJoin(in);
                    break;
                case "ProxyPlayerServerSwitch":
                    handleProxyPlayerServerSwitch(in);
                    break;
                case "ProxyPlayerDisconnect":
                    handleProxyPlayerDisconnect(in);
                    break;
                default:
                    plugin.logDebug("Type d'événement proxy inconnu : " + eventType);
            }
        } catch (Exception e) {
            plugin.logError("Erreur lors du traitement d'un événement proxy", e);
        }
    }

    private void handleProxyPlayerConnect(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        UUID playerId = new UUID(in.readLong(), in.readLong());

        ProxyPlayerConnectEvent coreEvent = new ProxyPlayerConnectEvent(playerName, playerId);
        plugin.getEventManager().callEvent(coreEvent);

        SLProxyPlayerConnectEvent bukkitEvent = new SLProxyPlayerConnectEvent(playerName, playerId);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        
        plugin.logDebug("ProxyPlayerConnect: " + playerName);
    }

    private void handleProxyPlayerJoin(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        UUID playerId = new UUID(in.readLong(), in.readLong());
        String serverName = in.readUTF();

        ProxyPlayerJoinEvent coreEvent = new ProxyPlayerJoinEvent(playerName, playerId, serverName);
        plugin.getEventManager().callEvent(coreEvent);

        SLProxyPlayerJoinEvent bukkitEvent = new SLProxyPlayerJoinEvent(playerName, playerId, serverName);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        
        plugin.logDebug("ProxyPlayerJoin: " + playerName + " -> " + serverName);
    }

    private void handleProxyPlayerServerSwitch(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        UUID playerId = new UUID(in.readLong(), in.readLong());
        String fromServer = in.readUTF();
        String toServer = in.readUTF();

        ProxyPlayerServerSwitchEvent coreEvent = new ProxyPlayerServerSwitchEvent(
                playerName, playerId, fromServer, toServer);
        plugin.getEventManager().callEvent(coreEvent);

        SLProxyPlayerServerSwitchEvent bukkitEvent = new SLProxyPlayerServerSwitchEvent(
                playerName, playerId, fromServer, toServer);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        
        plugin.logDebug("ProxyPlayerServerSwitch: " + playerName + " de " + fromServer + " vers " + toServer);
    }

    private void handleProxyPlayerDisconnect(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        UUID playerId = new UUID(in.readLong(), in.readLong());

        ProxyPlayerDisconnectEvent coreEvent = new ProxyPlayerDisconnectEvent(playerName, playerId);
        plugin.getEventManager().callEvent(coreEvent);

        SLProxyPlayerDisconnectEvent bukkitEvent = new SLProxyPlayerDisconnectEvent(playerName, playerId);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        
        plugin.logDebug("ProxyPlayerDisconnect: " + playerName);
    }
}
