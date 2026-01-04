package fr.mrbaguette07.slconnector.velocity.connector;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.velocity.Velocityslconnector;
import fr.mrbaguette07.slconnector.connector.VersionMismatchException;

public class PluginMessageConnector extends VelocityConnector {

    private final ChannelIdentifier messageChannel;
    private final Multimap<String, byte[]> messageQueue = MultimapBuilder.hashKeys().linkedListValues().build();

    public PluginMessageConnector(Velocityslconnector plugin) {
        super(plugin, true);
        messageChannel = MinecraftChannelIdentifier.from(plugin.getMessageChannel());
        plugin.getProxy().getChannelRegistrar().register(messageChannel);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getResult().isAllowed() || !event.getIdentifier().equals(messageChannel)) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());
        if (event.getSource() instanceof Player) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String group = in.readUTF();

        String target = in.readUTF();

        int messageLength = in.readInt();
        byte[] messageData = new byte[messageLength];
        in.readFully(messageData);
        try {
            Message message = Message.fromByteArray(group, messageData);
            switch (message.getTarget()) {
                case ALL_WITH_PLAYERS:
                    sendToAllWithPlayers(event.getData(), null);
                    break;
                case ALL_QUEUE:
                    sendToAllAndQueue(event.getData(), null);
                    break;
                case OTHERS_WITH_PLAYERS:
                    if (((Player) event.getTarget()).getCurrentServer().isPresent()) {
                        sendToAllWithPlayers(event.getData(),
                                ((Player) event.getTarget()).getCurrentServer().get().getServer());
                    } else {
                        sendToAllWithPlayers(event.getData(), null);
                    }
                    break;
                case OTHERS_QUEUE:
                    if (((Player) event.getTarget()).getCurrentServer().isPresent()) {
                        sendToAllAndQueue(event.getData(),
                                ((Player) event.getTarget()).getCurrentServer().get().getServer());
                    } else {
                        sendToAllAndQueue(event.getData(), null);
                    }
                    break;
                case SERVER:
                    if (!target.isEmpty()) {
                        RegisteredServer server = getTargetServer(target);
                        if (server != null) {
                            if (!server.sendPluginMessage(messageChannel, event.getData())) {
                                messageQueue.put(server.getServerInfo().getName(), event.getData());
                            }
                        } else {
                            plugin.logDebug(target + " doesn't exist?");
                        }
                    } else {
                        plugin.logError(message.getTarget() + " la cible du message nécessite une cible explicite !");
                    }
                    break;
                case PROXY:
                case ALL_PROXIES:
                    handle(target, message);
                    break;
                default:
                    plugin.logError("La réception de " + message.getTarget() + " n'est pas supportée !");
            }
        } catch (IllegalArgumentException e) {
            plugin.logError("Cible de message invalide ! " + e.getMessage());
        } catch (VersionMismatchException e) {
            plugin.logWarning(e.getMessage() + ". Message ignoré !");
        }
    }

    private void sendToAllWithPlayers(byte[] data, RegisteredServer excludedServer) {
        sendToAll(data, false, excludedServer);
    }

    private void sendToAllAndQueue(byte[] data, RegisteredServer excludedServer) {
        sendToAll(data, true, excludedServer);
    }

    private void sendToAll(byte[] data, boolean queue, RegisteredServer excludedServer) {
        for (RegisteredServer server : plugin.getProxy().getAllServers()) {
            if (excludedServer == null || excludedServer != server) {
                if (!server.sendPluginMessage(messageChannel, data) && queue) {
                    messageQueue.put(server.getServerInfo().getName(), data);
                }
            }
        }
    }

    @Subscribe
    public void onPlayerServerConnected(ServerPostConnectEvent event) {
        event.getPlayer().getCurrentServer().ifPresent(server -> {
            for (byte[] data : messageQueue.removeAll(server.getServerInfo().getName())) {
                server.sendPluginMessage(messageChannel, data);
            }
        });
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

        RegisteredServer server = getTargetServer(targetData);

        switch (message.getTarget()) {
            case ALL_WITH_PLAYERS:
                sendToAllWithPlayers(dataToSend, null);
                break;
            case ALL_QUEUE:
                sendToAllAndQueue(dataToSend, null);
                break;
            case OTHERS_WITH_PLAYERS:
                sendToAllWithPlayers(dataToSend, server);
                break;
            case OTHERS_QUEUE:
                sendToAllAndQueue(dataToSend, server);
                break;
            case SERVER:
                if (server != null) {
                    server.sendPluginMessage(messageChannel, dataToSend);
                } else {
                    throw new UnsupportedOperationException("Impossible d'envoyer les données vers " + message.getTarget()
                            + " car le serveur cible n'a pas été trouvé depuis " + targetData + " !");
                }
                break;
            default:
                throw new UnsupportedOperationException("L'envoi vers " + message.getTarget() + " n'est pas supporté !");
        }
    }
}
