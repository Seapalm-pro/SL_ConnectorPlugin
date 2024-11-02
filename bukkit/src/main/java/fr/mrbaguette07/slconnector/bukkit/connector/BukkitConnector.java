package fr.mrbaguette07.slconnector.bukkit.connector;

import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.connector.Connector;
import fr.mrbaguette07.slconnector.connector.Message;
import org.bukkit.entity.Player;

public abstract class BukkitConnector extends Connector<Bukkitslconnector, Player> {

    public BukkitConnector(Bukkitslconnector plugin, boolean requiresPlayer) {
        super(plugin, requiresPlayer);
    }

    protected Player getReceiverImplementation(String name) {
        Player player = plugin.getServer().getPlayerExact(name);
        if (player != null && player.isOnline()) {
            return player;
        }
        return null;
    }

    @Override
    protected void handle(Player receiver, Message message) {
        switch (message.getTarget()) {
            case OTHERS_WITH_PLAYERS:
            case ALL_WITH_PLAYERS:
                if (plugin.getServer().getOnlinePlayers().isEmpty()) {
                    return;
                }
        }
        super.handle(receiver, message);
    }

    @Override
    protected void sendDataImplementation(Object targetData, Message message) {
        sendDataImplementation(targetData instanceof String
                ? (hasPrefix((String) targetData)
                        ? (String) targetData
                        : SERVER_PREFIX + targetData)
                : (targetData instanceof Player
                        ? PLAYER_PREFIX + ((Player) targetData).getName()
                        : ""),
                message);
    }

    protected abstract void sendDataImplementation(String targetData, Message message);

}
