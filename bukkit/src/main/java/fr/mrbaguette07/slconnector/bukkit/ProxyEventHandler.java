package fr.mrbaguette07.slconnector.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.event.ProxyEvent;
import fr.mrbaguette07.slconnector.event.ProxyEventSerializer;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Handler pour recevoir et traiter les événements proxy sur les serveurs Bukkit
 */
public class ProxyEventHandler {
    private final Bukkitslconnector plugin;

    public ProxyEventHandler(Bukkitslconnector plugin) {
        this.plugin = plugin;
    }

    /**
     * Traite un message proxy reçu
     * @param player Le joueur qui a reçu le message (peut être null)
     * @param message Le message reçu
     */
    public void handleMessage(Player player, Message message) {
        try {
            handleProxyEvent(message.getData());
        } catch (Exception e) {
            plugin.logError("Erreur lors du traitement d'un message proxy", e);
        }
    }

    /**
     * Traite un événement proxy reçu
     */
    private void handleProxyEvent(byte[] eventData) {
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(eventData);

            String eventType = in.readUTF();
            if (!"ProxyEvent".equals(eventType)) {
                plugin.logDebug("Type d'événement inconnu: " + eventType);
                return;
            }

            int dataSize = in.readInt();
            byte[] serializedEvent = new byte[dataSize];
            in.readFully(serializedEvent);

            ProxyEvent event = ProxyEventSerializer.deserialize(serializedEvent);
            plugin.getEventManager().callEvent(event);
            
            plugin.logDebug("Événement proxy reçu et déclenché: " + event.getClass().getSimpleName());
            
        } catch (IOException e) {
            plugin.logError("Erreur lors de la désérialisation de l'événement proxy", e);
        }
    }
}
