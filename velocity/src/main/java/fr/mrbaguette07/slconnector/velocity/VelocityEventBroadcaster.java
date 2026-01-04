package fr.mrbaguette07.slconnector.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.MessageTarget;
import fr.mrbaguette07.slconnector.event.*;

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
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ProxyPlayerConnect");
        out.writeUTF(playerName);
        out.writeLong(playerId.getMostSignificantBits());
        out.writeLong(playerId.getLeastSignificantBits());
        
        broadcastEvent(out.toByteArray());
    }

    /**
     * Diffuse un événement ProxyPlayerJoin
     */
    public void broadcastProxyPlayerJoin(String playerName, UUID playerId, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ProxyPlayerJoin");
        out.writeUTF(playerName);
        out.writeLong(playerId.getMostSignificantBits());
        out.writeLong(playerId.getLeastSignificantBits());
        out.writeUTF(serverName);
        
        broadcastEvent(out.toByteArray());
    }

    /**
     * Diffuse un événement ProxyPlayerServerSwitch
     */
    public void broadcastProxyPlayerServerSwitch(String playerName, UUID playerId, String fromServer, String toServer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ProxyPlayerServerSwitch");
        out.writeUTF(playerName);
        out.writeLong(playerId.getMostSignificantBits());
        out.writeLong(playerId.getLeastSignificantBits());
        out.writeUTF(fromServer);
        out.writeUTF(toServer);
        
        broadcastEvent(out.toByteArray());
    }

    /**
     * Diffuse un événement ProxyPlayerDisconnect
     */
    public void broadcastProxyPlayerDisconnect(String playerName, UUID playerId) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ProxyPlayerDisconnect");
        out.writeUTF(playerName);
        out.writeLong(playerId.getMostSignificantBits());
        out.writeLong(playerId.getLeastSignificantBits());
        
        broadcastEvent(out.toByteArray());
    }

    /**
     * Envoie les données d'événement à tous les serveurs
     */
    private void broadcastEvent(byte[] data) {
        try {
            plugin.getConnector().sendData(plugin, "", MessageTarget.ALL_QUEUE, data);
        } catch (Exception e) {
            plugin.logError("Erreur lors de la diffusion d'un événement proxy", e);
        }
    }
}
