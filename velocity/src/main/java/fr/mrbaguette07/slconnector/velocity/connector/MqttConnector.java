package fr.mrbaguette07.slconnector.velocity.connector;

import fr.mrbaguette07.slconnector.velocity.Velocityslconnector;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.connector.MqttConnection;

public class MqttConnector extends VelocityConnector {
    private final MqttConnection connection;

    public MqttConnector(Velocityslconnector plugin) {
        super(plugin, false);
        connection = new MqttConnection(
                plugin,
                plugin.getConfig().getString("mqtt.broker-uri"),
                plugin.getConfig().getString("mqtt.client-id", null),
                plugin.getConfig().getString("mqtt.username"),
                plugin.getConfig().getString("mqtt.password"),
                plugin.getConfig().getInt("mqtt.keep-alive"),
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
