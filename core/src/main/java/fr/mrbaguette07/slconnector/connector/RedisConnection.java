package fr.mrbaguette07.slconnector.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.slconnector;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.function.BiConsumer;

public class RedisConnection {

    private final slconnector plugin;

    private final RedisClient client;
    private StatefulRedisConnection<String, byte[]> connection;

    public RedisConnection(slconnector plugin, String uriString, String host, int port, int db, String password,
            long timeout, BiConsumer<String, Message> onMessage) {
        this.plugin = plugin;
        RedisURI uri;
        if (uriString != null && !uriString.isEmpty()) {
            uri = RedisURI.create(uriString);
        } else {
            uri = new RedisURI();
            if (host != null && !host.isEmpty()) {
                uri.setHost(host);
            }
            if (port > 0) {
                uri.setPort(port);
            }
            if (db > -1) {
                uri.setDatabase(db);
            }
            if (password != null && !password.isEmpty()) {
                uri.setPassword(password);
            }
            if (timeout > 0) {
                uri.setTimeout(Duration.ofSeconds(timeout));
            }
        }
        client = RedisClient.create(uri);

        StatefulRedisPubSubConnection<String, byte[]> connection = client.connectPubSub(new StringByteArrayCodec());
        connection.addListener(new RedisPubSubListener<String, byte[]>() {
            @Override
            public void message(String channel, byte[] data) {
                if (!channel.equals(plugin.getMessageChannel())) {
                    return;
                }
                if (data.length == 0) {
                    plugin.logWarning("Received a message with 0 bytes on " + channel + " redis channel? ");
                    return;
                }

                ByteArrayDataInput in = ByteStreams.newDataInput(data);
                String group = in.readUTF();

                String target = in.readUTF();

                int messageLength = in.readInt();
                byte[] messageData = new byte[messageLength];
                in.readFully(messageData);

                try {
                    onMessage.accept(target, Message.fromByteArray(group, messageData));
                } catch (IllegalArgumentException e) {
                    plugin.logError("Error while decoding message on " + channel + " redis channel! ", e);
                } catch (VersionMismatchException e) {
                    plugin.logWarning(e.getMessage() + ". Ignoring message!");
                }
            }

            @Override
            public void message(String pattern, String channel, byte[] message) {
            }

            @Override
            public void subscribed(String channel, long count) {
            }

            @Override
            public void psubscribed(String pattern, long count) {
            }

            @Override
            public void unsubscribed(String channel, long count) {
            }

            @Override
            public void punsubscribed(String pattern, long count) {
            }
        });

        connection.async().subscribe(plugin.getMessageChannel());
    }

    public void sendMessage(String targetData, Message message) {
        if (connection == null || !connection.isOpen()) {
            connection = client.connect(new StringByteArrayCodec());
        }
        byte[] messageData = message.writeToByteArray();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(message.getGroup());
        out.writeUTF(targetData != null ? targetData : "");
        out.writeInt(messageData.length);
        out.write(messageData);
        byte[] dataToSend = out.toByteArray();

        connection.async().publish(plugin.getMessageChannel(), dataToSend);
    }

    public void close() {
        client.shutdown();
    }

    private class StringByteArrayCodec implements RedisCodec<String, byte[]> {

        private final StringCodec stringCodec = new StringCodec();
        private final ByteArrayCodec byteArrayCodec = new ByteArrayCodec();

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return stringCodec.decodeKey(bytes);
        }

        @Override
        public byte[] decodeValue(ByteBuffer bytes) {
            return byteArrayCodec.decodeValue(bytes);
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return stringCodec.encodeKey(key);
        }

        @Override
        public ByteBuffer encodeValue(byte[] value) {
            return byteArrayCodec.encodeValue(value);
        }
    }
}
