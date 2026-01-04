package fr.mrbaguette07.slconnector.bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.mrbaguette07.slconnector.bukkit.commands.ConnectorCommand;
import fr.mrbaguette07.slconnector.bukkit.connector.BukkitConnector;
import fr.mrbaguette07.slconnector.bukkit.connector.PluginMessageConnector;
import fr.mrbaguette07.slconnector.bukkit.connector.RedisConnector;
import fr.mrbaguette07.slconnector.connector.ConnectingPlugin;
import fr.mrbaguette07.slconnector.connector.MessageTarget;
import fr.mrbaguette07.slconnector.slconnector;
import fr.mrbaguette07.slconnector.event.EventManager;

public final class Bukkitslconnector extends JavaPlugin implements slconnector<Player>, Listener {

    private BukkitConnector connector;
    private Bridge bridge;
    private EventManager eventManager;
    private ProxyEventHandler proxyEventHandler;
    private boolean debug = true;
    private String globalGroup;
    private Map<String, String> pluginGroups;
    private String serverName;

    @Override
    public void onEnable() {
        eventManager = new EventManager();
        proxyEventHandler = new ProxyEventHandler(this);
        
        saveDefaultConfig();
        reloadConfig();

        debug = getConfig().getBoolean("debug");
        globalGroup = getConfig().getString("group", "global");
        pluginGroups = new HashMap<>();
        ConfigurationSection pluginGroupsConfig = getConfig().getConfigurationSection("plugin-groups");
        if (pluginGroupsConfig != null) {
            for (String pluginName : pluginGroupsConfig.getKeys(false)) {
                pluginGroups.put(pluginName.toLowerCase(Locale.ROOT), pluginGroupsConfig.getString(pluginName));
            }
        }
        serverName = getConfig().getString("server-name", "changeme");
        if ("changeme".equals(serverName)) {
            serverName = new File(".").getAbsoluteFile().getParentFile().getName();
            getLogger().log(Level.WARNING,
                    "Le nom du serveur n'est pas configuré ! Veuillez le définir dans la config du plugin ! Utilisation du nom du dossier du serveur à la place : "
                            + serverName);
        }

        String messengerType = getConfig().getString("messenger-type", "plugin_messages").toLowerCase(Locale.ROOT);
        switch (messengerType) {
            default:
                getLogger().log(Level.WARNING,
                        "Le type de messenger '" + messengerType + "' n'est pas supporté, utilisation des messages de plugin par défaut !");
            case "plugin_messages":
                connector = new PluginMessageConnector(this);
                getLogger().log(Level.WARNING, "L'utilisation des messages de plugin comporte " +
                        "certaines limitations comme l'envoi vers des serveurs sans joueurs ou vers " +
                        "d'autres proxies qui ne fonctionnera pas !");
                getLogger().log(Level.WARNING, "Veuillez considérer l'utilisation d'un autre type de messenger !");
                break;
            case "redis":
                connector = new RedisConnector(this);
                logInfo("Utilisation du messenger Redis");
                break;
        }

        getCommand("slconnector").setExecutor(new ConnectorCommand(this));

        bridge = new Bridge(this);
        
        connector.registerMessageHandler(this, "ProxyEvent", proxyEventHandler::handleMessage);
    }

    @Override
    public void onDisable() {
        connector.close();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() instanceof ConnectingPlugin) {
            connector.unregisterMessageHandlers((ConnectingPlugin) event.getPlugin());
        }
    }

    /**
     * Get the bridge helper class for executing certain actions on the proxy and
     * other servers
     * 
     * @return The bridge helper
     */
    public Bridge getBridge() {
        return bridge;
    }

    @Override
    public BukkitConnector getConnector() {
        return connector;
    }

    @Override
    public void runAsync(Runnable runnable) {
        FoliaScheduler.runAsync(this, runnable);
    }

    public void runSync(Runnable runnable) {
        FoliaScheduler.runSync(this, runnable);
    }

    @Override
    public MessageTarget.Type getSourceType() {
        return MessageTarget.Type.SERVER;
    }

    @Override
    public void logDebug(String message, Throwable... throwables) {
        if (debug) {
            getLogger().log(Level.INFO, "[DEBUG] " + message, throwables.length > 0 ? throwables[0] : null);
        }
    }

    @Override
    public void logInfo(String message, Throwable... throwables) {
        getLogger().log(Level.INFO, message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public void logWarning(String message, Throwable... throwables) {
        getLogger().log(Level.WARNING, message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public void logError(String message, Throwable... throwables) {
        getLogger().log(Level.SEVERE, message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public String getGlobalGroup() {
        return globalGroup;
    }

    @Override
    public Map<String, String> getGroups() {
        return pluginGroups;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }
}
