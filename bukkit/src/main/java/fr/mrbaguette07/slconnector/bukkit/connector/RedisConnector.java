package fr.mrbaguette07.slconnector.bukkit.connector;


import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.RedisConnection;

public class RedisConnector extends BukkitConnector {
    private final RedisConnection connection;
    private final ProxyEventHandler eventHandler;

    public RedisConnector(Bukkitslconnector plugin) {
        super(plugin, false);
        this.eventHandler = new ProxyEventHandler(plugin);
        connection = new RedisConnection(
                plugin,
                plugin.getConfig().getString("redis.uri"),
                plugin.getConfig().getString("redis.host"),
                plugin.getConfig().getInt("redis.port"),
                plugin.getConfig().getInt("redis.db"),
                plugin.getConfig().getString("redis.password"),
                plugin.getConfig().getLong("redis.timeout"),
                (receiver, message) -> plugin.runSync(() -> handle(receiver, message)));
        
        registerMessageHandler(plugin, "ProxyEvent", (player, message) -> eventHandler.handleMessage(player, message));
    }

    @Override
    protected void sendDataImplementation(String targetData, Message message) {
        connection.sendMessage(targetData, message);
    }

    @Override
    public void close() {
        connection.close();
    }
}
