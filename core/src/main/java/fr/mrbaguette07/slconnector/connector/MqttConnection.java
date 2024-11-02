package fr.mrbaguette07.slconnector.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.slconnector;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class MqttConnection {

    private final slconnector plugin;
    private MqttClient client;

    public MqttConnection(slconnector plugin, String brokerURI, String clientID, String username, String password,
            int keepAlive, BiConsumer<String, Message> onMessage) {
        this.plugin = plugin;

        MqttConnectionOptions conOpts = new MqttConnectionOptions();

        conOpts.setCleanStart(true);

        if (clientID == null || clientID.isEmpty()) {
            clientID = plugin.getName() + "-" + plugin.getServerName() + "-" + Thread.currentThread().getId();
        }

        if (username != null) {
            conOpts.setUserName(username);
        }

        if (password != null) {
            conOpts.setPassword(password.getBytes(StandardCharsets.UTF_8));
        }

        conOpts.setKeepAliveInterval(keepAlive);
        conOpts.setAutomaticReconnect(true);

        try {
            client = new MqttClient(brokerURI, clientID);
            client.connect(conOpts);

            client.subscribe(new MqttSubscription[] { new MqttSubscription(plugin.getMessageChannel()) },
                    new IMqttMessageListener[] { (topic, message) -> {
                        if (!topic.equals(plugin.getMessageChannel())) {
                            return;
                        }
                        if (message.getPayload().length == 0) {
                            plugin.logWarning("Received a message with 0 bytes on " + topic + " MQTT topic? ");
                            return;
                        }

                        ByteArrayDataInput in = ByteStreams.newDataInput(message.getPayload());
                        String group = in.readUTF();

                        String target = in.readUTF();

                        int messageLength = in.readInt();
                        byte[] messageData = new byte[messageLength];
                        in.readFully(messageData);

                        try {
                            onMessage.accept(target, Message.fromByteArray(group, messageData));
                        } catch (IllegalArgumentException e) {
                            plugin.logError("Error while decoding message on " + topic + " MQTT topic! ", e);
                        } catch (VersionMismatchException e) {
                            plugin.logWarning(e.getMessage() + ". Ignoring message!");
                        }
                    } }).waitForCompletion(10000);
        } catch (MqttException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void sendMessage(String senderName, Message message) {
        byte[] messageData = message.writeToByteArray();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getGroup());
        out.writeUTF(senderName != null ? senderName : "");
        out.writeInt(messageData.length);
        out.write(messageData);
        byte[] dataToSend = out.toByteArray();

        plugin.runAsync(() -> {
            try {
                client.publish(plugin.getMessageChannel(), dataToSend, 1, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
