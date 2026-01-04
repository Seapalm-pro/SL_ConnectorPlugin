package fr.mrbaguette07.slconnector.bukkit.connector;

import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;
import fr.mrbaguette07.slconnector.bukkit.events.*;
import fr.mrbaguette07.slconnector.connector.Message;
import fr.mrbaguette07.slconnector.event.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Gestionnaire des événements proxy reçus via le réseau
 */
public class ProxyEventHandler {
    private final Bukkitslconnector plugin;

    public ProxyEventHandler(Bukkitslconnector plugin) {
        this.plugin = plugin;
    }

    /**
     * Gère les messages d'événements proxy reçus
     */
    public void handleMessage(Player receiver, Message message) {
        try {
            ProxyEvent coreEvent = ProxyEventSerializer.deserialize(message.getData());

            plugin.getEventManager().callEvent(coreEvent);

            triggerBukkitEvent(coreEvent);
            
            plugin.logDebug("Événement proxy reçu et déclenché: " + coreEvent.getClass().getSimpleName());
            
        } catch (IOException e) {
            plugin.logError("Erreur lors de la désérialisation d'un événement proxy", e);
        } catch (IllegalArgumentException e) {
            plugin.logError("Type d'événement inconnu reçu", e);
        } catch (Exception e) {
            plugin.logError("Erreur lors du traitement d'un événement proxy", e);
        }
    }

    private void triggerBukkitEvent(ProxyEvent coreEvent) {
        if (coreEvent instanceof ProxyPlayerConnectEvent) {
            ProxyPlayerConnectEvent event = (ProxyPlayerConnectEvent) coreEvent;
            SLProxyPlayerConnectEvent bukkitEvent = new SLProxyPlayerConnectEvent(
                event.getPlayerName(), event.getPlayerId());
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            plugin.logDebug("SLProxyPlayerConnectEvent Bukkit déclenché: " + event.getPlayerName());
            
        } else if (coreEvent instanceof ProxyPlayerJoinEvent) {
            ProxyPlayerJoinEvent event = (ProxyPlayerJoinEvent) coreEvent;
            SLProxyPlayerJoinEvent bukkitEvent = new SLProxyPlayerJoinEvent(
                event.getPlayerName(), event.getPlayerId(), event.getServerName());
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            plugin.logDebug("SLProxyPlayerJoinEvent Bukkit déclenché: " + event.getPlayerName() + " -> " + event.getServerName());
            
        } else if (coreEvent instanceof ProxyPlayerServerSwitchEvent) {
            ProxyPlayerServerSwitchEvent event = (ProxyPlayerServerSwitchEvent) coreEvent;
            SLProxyPlayerServerSwitchEvent bukkitEvent = new SLProxyPlayerServerSwitchEvent(
                event.getPlayerName(), event.getPlayerId(), event.getCurrentServer(), event.getTargetServer());
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            plugin.logDebug("SLProxyPlayerServerSwitchEvent Bukkit déclenché: " + event.getPlayerName() + 
                " de " + event.getCurrentServer() + " vers " + event.getTargetServer());
            
        } else if (coreEvent instanceof ProxyPlayerDisconnectEvent) {
            ProxyPlayerDisconnectEvent event = (ProxyPlayerDisconnectEvent) coreEvent;
            SLProxyPlayerDisconnectEvent bukkitEvent = new SLProxyPlayerDisconnectEvent(
                event.getPlayerName(), event.getPlayerId());
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            plugin.logDebug("SLProxyPlayerDisconnectEvent Bukkit déclenché: " + event.getPlayerName());
        }
    }
}
