package fr.mrbaguette07.slconnector.velocity.connector;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.velocity.Velocityslconnector;
import fr.mrbaguette07.slconnector.connector.Connector;

public abstract class VelocityConnector extends Connector<Velocityslconnector, Player> {

    public VelocityConnector(Velocityslconnector plugin, boolean requiresPlayer) {
        super(plugin, requiresPlayer);
    }

    protected Player getReceiverImplementation(String name) {
        return plugin.getProxy().getPlayer(name).orElse(null);
    }

    protected RegisteredServer getTargetServer(String target) {
        if (target.startsWith(SERVER_PREFIX)) {
            return plugin.getProxy().getServer(target.substring(SERVER_PREFIX.length())).orElse(null);
        } else if (target.startsWith(PLAYER_PREFIX)) {
            Player player = getReceiver(target.substring(PLAYER_PREFIX.length()));
            if (player != null && player.getCurrentServer().isPresent()) {
                return player.getCurrentServer().get().getServer();
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
                : (targetData instanceof Player
                        ? PLAYER_PREFIX + ((Player) targetData).getUsername()
                        : ""),
                message);
    }

    protected abstract void sendDataImplementation(String targetData, Message message);
}
