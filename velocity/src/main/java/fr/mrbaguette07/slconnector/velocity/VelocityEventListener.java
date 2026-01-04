package fr.mrbaguette07.slconnector.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import fr.mrbaguette07.slconnector.connector.MessageTarget;
import fr.mrbaguette07.slconnector.event.*;

import java.io.IOException;

/**
 * Listener pour les événements Velocity qui déclenche les événements SLConnector
 * et les propage à tous les serveurs
 */
public class VelocityEventListener {
    private final Velocityslconnector plugin;
    private final VelocityEventBroadcaster broadcaster;

    public VelocityEventListener(Velocityslconnector plugin) {
        this.plugin = plugin;
        this.broadcaster = new VelocityEventBroadcaster(plugin);
    }
    
    /**
     * Déclenché lorsqu'un joueur se connecte au proxy (avant le join)
     */
    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent event) {
        ProxyPlayerConnectEvent connectEvent = new ProxyPlayerConnectEvent(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId()
        );
        plugin.getEventManager().callEvent(connectEvent);
        
        // Propager aux serveurs Bukkit
        broadcaster.broadcastProxyPlayerConnect(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId()
        );
    }

    /**
     * Déclenché lorsqu'un joueur rejoint un serveur
     */
    @Subscribe(order = PostOrder.FIRST)
    public void onServerConnected(ServerConnectedEvent event) {
        ProxyPlayerJoinEvent joinEvent = new ProxyPlayerJoinEvent(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId(),
                event.getServer().getServerInfo().getName()
        );
        plugin.getEventManager().callEvent(joinEvent);
        
        // Propager aux serveurs Bukkit
        broadcaster.broadcastProxyPlayerJoin(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId(),
                event.getServer().getServerInfo().getName()
        );
    }

    /**
     * Déclenché lorsqu'un joueur change de serveur
     */
    @Subscribe(order = PostOrder.FIRST)
    public void onServerSwitch(ServerPostConnectEvent event) {
        if (event.getPreviousServer() != null) {
            String toServer = event.getPlayer().getCurrentServer()
                    .map(server -> server.getServerInfo().getName())
                    .orElse("unknown");
            
            ProxyPlayerServerSwitchEvent switchEvent = new ProxyPlayerServerSwitchEvent(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getUniqueId(),
                    event.getPreviousServer().getServerInfo().getName(),
                    toServer
            );
            plugin.getEventManager().callEvent(switchEvent);
            
            // Propager aux serveurs Bukkit
            broadcaster.broadcastProxyPlayerServerSwitch(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getUniqueId(),
                    event.getPreviousServer().getServerInfo().getName(),
                    toServer
            );
        }
    }

    /**
     * Déclenché lorsqu'un joueur se déconnecte
     */
    @Subscribe(order = PostOrder.LAST)
    public void onDisconnect(DisconnectEvent event) {
        ProxyPlayerDisconnectEvent disconnectEvent = new ProxyPlayerDisconnectEvent(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId()
        );
        plugin.getEventManager().callEvent(disconnectEvent);
        
        // Propager aux serveurs Bukkit
        broadcaster.broadcastProxyPlayerDisconnect(
                event.getPlayer().getUsername(),
                event.getPlayer().getUniqueId()
        );
    }
}
