package fr.mrbaguette07.slconnector.bungee.connector;

import fr.mrbaguette07.slconnector.bungee.Bungeeslconnector;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.RedisConnection;

public class RedisConnector extends BungeeConnector {
    private final RedisConnection connection;

    public RedisConnector(Bungeeslconnector plugin) {
        super(plugin, false);
        connection = new RedisConnection(
                plugin,
                plugin.getConfig().getString("redis.uri"),
                plugin.getConfig().getString("redis.host"),
                plugin.getConfig().getInt("redis.port"),
                plugin.getConfig().getInt("redis.db"),
                plugin.getConfig().getString("redis.password"),
                plugin.getConfig().getLong("redis.timeout"),
                this::handle);
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
