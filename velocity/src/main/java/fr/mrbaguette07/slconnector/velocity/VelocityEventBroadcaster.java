package fr.mrbaguette07.slconnector.velocity;

import fr.mrbaguette07.slconnector.connector.MessageTarget;
import fr.mrbaguette07.slconnector.event.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Classe pour diffuser les événements proxy vers tous les serveurs Bukkit
 */
public class VelocityEventBroadcaster {
    private final Velocityslconnector plugin;

    public VelocityEventBroadcaster(Velocityslconnector plugin) {
        this.plugin = plugin;
    }

    /**
     * Diffuse un événement ProxyPlayerConnect
     */
    public void broadcastProxyPlayerConnect(String playerName, UUID playerId) {
        ProxyPlayerConnectEvent event = new ProxyPlayerConnectEvent(playerName, playerId);
        broadcastEvent(event);
    }

    /**
     * Diffuse un événement ProxyPlayerJoin
     */
    public void broadcastProxyPlayerJoin(String playerName, UUID playerId, String serverName) {
        ProxyPlayerJoinEvent event = new ProxyPlayerJoinEvent(playerName, playerId, serverName);
        broadcastEvent(event);
    }

    /**
     * Diffuse un événement ProxyPlayerServerSwitch
     */
    public void broadcastProxyPlayerServerSwitch(String playerName, UUID playerId, String fromServer, String toServer) {
        ProxyPlayerServerSwitchEvent event = new ProxyPlayerServerSwitchEvent(playerName, playerId, fromServer, toServer);
        broadcastEvent(event);
    }

    /**
     * Diffuse un événement ProxyPlayerDisconnect
     */
    public void broadcastProxyPlayerDisconnect(String playerName, UUID playerId) {
        ProxyPlayerDisconnectEvent event = new ProxyPlayerDisconnectEvent(playerName, playerId);
        broadcastEvent(event);
    }

    /**
     * Sérialise et envoie un événement à tous les serveurs
     */
    private void broadcastEvent(ProxyEvent event) {
        try {
            byte[] data = ProxyEventSerializer.serialize(event);
            plugin.getConnector().sendData(plugin, "ProxyEvent", MessageTarget.ALL_QUEUE, data);
            plugin.logDebug("Événement proxy diffusé: " + event.getClass().getSimpleName());
        } catch (IOException e) {
            plugin.logError("Erreur lors de la sérialisation d'un événement proxy", e);
        } catch (Exception e) {
            plugin.logError("Erreur lors de la diffusion d'un événement proxy", e);
        }
    }
}
