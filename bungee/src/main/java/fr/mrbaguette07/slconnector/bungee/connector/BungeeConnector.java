package fr.mrbaguette07.slconnector.bungee.connector;

import fr.mrbaguette07.slconnector.bungee.Bungeeslconnector;
import fr.mrbaguette07.slconnector.connector.Connector;
import fr.mrbaguette07.slconnector.connector.Message;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BungeeConnector extends Connector<Bungeeslconnector, ProxiedPlayer> {

    public BungeeConnector(Bungeeslconnector plugin, boolean requiresPlayer) {
        super(plugin, requiresPlayer);
    }

    protected ProxiedPlayer getReceiverImplementation(String name) {
        return plugin.getProxy().getPlayer(name);
    }

    protected ServerInfo getTargetServer(String target) {
        if (target.startsWith(SERVER_PREFIX)) {
            return plugin.getProxy().getServerInfo(target.substring(SERVER_PREFIX.length()));
        } else if (target.startsWith(PLAYER_PREFIX)) {
            ProxiedPlayer player = getReceiver(target.substring(PLAYER_PREFIX.length()));
            if (player != null && player.getServer() != null) {
                return player.getServer().getInfo();
            }
        }
        return null;
    }

    @Override
    protected void sendDataImplementation(Object targetData, Message message) {
        sendDataImplementation(targetData instanceof String
                ? (hasPrefix((String) targetData)
                        ? (String) targetData
                        : SERVER_PREFIX + targetData)
                : (targetData instanceof ProxiedPlayer
                        ? PLAYER_PREFIX + ((ProxiedPlayer) targetData).getName()
                        : ""),
                message);
    }

    protected abstract void sendDataImplementation(String targetData, Message message);
}
