package fr.mrbaguette07.slconnector.bukkit.connector;


import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.MqttConnection;

public class MqttConnector extends BukkitConnector {
    private final MqttConnection connection;
    private final ProxyEventHandler eventHandler;

    public MqttConnector(Bukkitslconnector plugin) {
        super(plugin, false);
        this.eventHandler = new ProxyEventHandler(plugin);
        connection = new MqttConnection(
                plugin,
                plugin.getConfig().getString("mqtt.broker-uri"),
                plugin.getConfig().getString("mqtt.client-id", null),
                plugin.getConfig().getString("mqtt.username"),
                plugin.getConfig().getString("mqtt.password"),
                plugin.getConfig().getInt("mqtt.keep-alive"),
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
