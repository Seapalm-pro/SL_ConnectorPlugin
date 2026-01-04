package fr.mrbaguette07.slconnector.event;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.util.UUID;

/**
 * Utilitaire pour sérialiser et désérialiser les événements proxy
 */
public class ProxyEventSerializer {
    public static final byte EVENT_PLAYER_CONNECT = 1;
    public static final byte EVENT_PLAYER_JOIN = 2;
    public static final byte EVENT_PLAYER_SERVER_SWITCH = 3;
    public static final byte EVENT_PLAYER_DISCONNECT = 4;
    
    /**
     * Sérialise un événement en tableau de bytes
     */
    public static byte[] serialize(ProxyEvent event) throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        
        if (event instanceof ProxyPlayerConnectEvent) {
            out.writeByte(EVENT_PLAYER_CONNECT);
            ProxyPlayerConnectEvent e = (ProxyPlayerConnectEvent) event;
            out.writeUTF(e.getPlayerName());
            out.writeLong(e.getPlayerId().getMostSignificantBits());
            out.writeLong(e.getPlayerId().getLeastSignificantBits());
            
        } else if (event instanceof ProxyPlayerJoinEvent) {
            out.writeByte(EVENT_PLAYER_JOIN);
            ProxyPlayerJoinEvent e = (ProxyPlayerJoinEvent) event;
            out.writeUTF(e.getPlayerName());
            out.writeLong(e.getPlayerId().getMostSignificantBits());
            out.writeLong(e.getPlayerId().getLeastSignificantBits());
            out.writeUTF(e.getServerName());
            
        } else if (event instanceof ProxyPlayerServerSwitchEvent) {
            out.writeByte(EVENT_PLAYER_SERVER_SWITCH);
            ProxyPlayerServerSwitchEvent e = (ProxyPlayerServerSwitchEvent) event;
            out.writeUTF(e.getPlayerName());
            out.writeLong(e.getPlayerId().getMostSignificantBits());
            out.writeLong(e.getPlayerId().getLeastSignificantBits());
            out.writeUTF(e.getCurrentServer());
            out.writeUTF(e.getTargetServer());
            
        } else if (event instanceof ProxyPlayerDisconnectEvent) {
            out.writeByte(EVENT_PLAYER_DISCONNECT);
            ProxyPlayerDisconnectEvent e = (ProxyPlayerDisconnectEvent) event;
            out.writeUTF(e.getPlayerName());
            out.writeLong(e.getPlayerId().getMostSignificantBits());
            out.writeLong(e.getPlayerId().getLeastSignificantBits());
            
        } else {
            throw new IllegalArgumentException("Type d'événement non supporté: " + event.getClass().getName());
        }
        
        return out.toByteArray();
    }
    
    /**
     * Désérialise un événement depuis un tableau de bytes
     */
    public static ProxyEvent deserialize(byte[] data) throws IOException {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        
        byte eventType = in.readByte();
        
        switch (eventType) {
            case EVENT_PLAYER_CONNECT: {
                String playerName = in.readUTF();
                UUID playerId = new UUID(in.readLong(), in.readLong());
                return new ProxyPlayerConnectEvent(playerName, playerId);
            }
            
            case EVENT_PLAYER_JOIN: {
                String playerName = in.readUTF();
                UUID playerId = new UUID(in.readLong(), in.readLong());
                String serverName = in.readUTF();
                return new ProxyPlayerJoinEvent(playerName, playerId, serverName);
            }
            
            case EVENT_PLAYER_SERVER_SWITCH: {
                String playerName = in.readUTF();
                UUID playerId = new UUID(in.readLong(), in.readLong());
                String fromServer = in.readUTF();
                String toServer = in.readUTF();
                return new ProxyPlayerServerSwitchEvent(playerName, playerId, fromServer, toServer);
            }
            
            case EVENT_PLAYER_DISCONNECT: {
                String playerName = in.readUTF();
                UUID playerId = new UUID(in.readLong(), in.readLong());
                return new ProxyPlayerDisconnectEvent(playerName, playerId);
            }
            
            default:
                throw new IllegalArgumentException("Type d'événement inconnu: " + eventType);
        }
    }
}
