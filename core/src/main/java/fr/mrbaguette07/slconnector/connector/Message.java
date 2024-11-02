package fr.mrbaguette07.slconnector.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Message {
    private static final int VERSION = 2;
    private final String group;
    private final MessageTarget target;
    private final String sendingServer;
    private final String sendingPlugin;
    private final String action;
    private final byte[] data;

    public Message(String group, MessageTarget target, String sendingServer, String sendingPlugin, String action,
            byte[] data) {
        this.group = group;
        this.target = target;
        this.sendingServer = sendingServer;
        this.sendingPlugin = sendingPlugin;
        this.action = action;
        this.data = data;
    }

    public String getGroup() {
        return group;
    }

    public MessageTarget getTarget() {
        return target;
    }

    public String getSendingServer() {
        return sendingServer;
    }

    public String getSendingPlugin() {
        return sendingPlugin;
    }

    public String getAction() {
        return action;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] writeToByteArray() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(VERSION);
        out.writeUTF(target.name());
        out.writeUTF(sendingServer);
        out.writeUTF(sendingPlugin);
        out.writeUTF(action);
        out.writeInt(data.length);
        out.write(data);
        return out.toByteArray();
    }

    public static Message fromByteArray(String group, byte[] messageData) throws VersionMismatchException {
        ByteArrayDataInput in = ByteStreams.newDataInput(messageData);
        int messageVersion = in.readInt();
        if (messageVersion < VERSION) {
            throw new VersionMismatchException(messageVersion, VERSION, "Received message from an outdated version ("
                    + messageVersion + ", this only supports " + VERSION + ")! Please update the sending plugin!");
        } else if (messageVersion > VERSION) {
            throw new VersionMismatchException(messageVersion, VERSION, "Received message with a newer version ("
                    + messageVersion + ", this only supports " + VERSION + ")! Please update this plugin!");
        }
        MessageTarget target = MessageTarget.valueOf(in.readUTF());
        String senderServer = in.readUTF();
        String senderPlugin = in.readUTF();
        String action = in.readUTF();
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        return new Message(group, target, senderServer, senderPlugin, action, data);
    }

}
