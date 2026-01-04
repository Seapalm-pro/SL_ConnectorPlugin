package fr.mrbaguette07.slconnector.bukkit.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.MessageTarget;
import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.connector.VersionMismatchException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

public class PluginMessageConnector extends BukkitConnector implements PluginMessageListener, Listener {

    private final Deque<byte[]> queue = new ArrayDeque<>();
    private final ProxyEventHandler eventHandler;

    public PluginMessageConnector(Bukkitslconnector plugin) {
        super(plugin, true);
        this.eventHandler = new ProxyEventHandler(plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, plugin.getMessageChannel());
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, plugin.getMessageChannel(), this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        registerMessageHandler(plugin, "ProxyEvent", (player, message) -> eventHandler.handleMessage(player, message));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (!channel.equals(plugin.getMessageChannel())) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String group = in.readUTF();

        String target = in.readUTF();
        if (!isThis(target)) {
            return;
        }

        if (target.startsWith(PLAYER_PREFIX)) {
            String playerName = target.substring(PLAYER_PREFIX.length());
            player = getReceiver(playerName);
            if (player == null) {
                plugin.logError("Le joueur " + playerName + " n'a pas été trouvé en ligne ?");
                return;
            }
        }

        int messageLength = in.readInt();
        byte[] messageData = new byte[messageLength];
        in.readFully(messageData);

        try {
            handle(player, Message.fromByteArray(group, messageData));
        } catch (IllegalArgumentException e) {
            plugin.logError("Cible de message invalide ! " + e.getMessage());
        } catch (VersionMismatchException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage() + ". Message ignoré !");
        }
    }

    @Override
    public void sendDataImplementation(String targetData, Message message) {
        byte[] messageData = message.writeToByteArray();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getGroup());
        out.writeUTF(targetData);
        out.writeInt(messageData.length);
        out.write(messageData);
        byte[] dataToSend = out.toByteArray();

        Player player = null;
        if (!targetData.startsWith("server:")) {
            player = plugin.getServer().getPlayerExact(targetData);
        }
        if (player != null) {
            player.sendPluginMessage(plugin, plugin.getMessageChannel(), dataToSend);
        } else {
            if (plugin.getServer().getOnlinePlayers().isEmpty()) {
                queue.add(dataToSend);
            } else {
                plugin.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(plugin,
                        plugin.getMessageChannel(), dataToSend);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        while (!queue.isEmpty()) {
            event.getPlayer().sendPluginMessage(plugin, plugin.getMessageChannel(), queue.remove());
        }
    }
}
