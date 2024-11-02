package fr.mrbaguette07.slconnector.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.mrbaguette07.slconnector.slconnector;
import fr.mrbaguette07.slconnector.velocity.commands.ConnectorCommand;
import fr.mrbaguette07.slconnector.velocity.connector.VelocityConnector;
import fr.mrbaguette07.slconnector.velocity.connector.MqttConnector;
import fr.mrbaguette07.slconnector.velocity.connector.PluginMessageConnector;
import fr.mrbaguette07.slconnector.velocity.connector.RedisConnector;
import fr.mrbaguette07.slconnector.connector.MessageTarget;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static fr.mrbaguette07.slconnector.connector.Connector.PROXY_ID_PREFIX;

public final class Velocityslconnector implements slconnector<Player> {

    private final ProxyServer proxy;
    private final Logger logger;
    private final File dataFolder;
    private PluginConfig config;
    private VelocityConnector connector;
    private Bridge bridge;
    private boolean debug = true;
    private String serverId;

    @Inject
    public Velocityslconnector(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataFolder = dataFolder.toFile();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new PluginConfig(this, new File(dataFolder, "config.yml"), "velocity-config.yml");
        try {
            config.createDefaultConfig();
        } catch (IOException e) {
            logger.error("Could not created default config! " + e.getMessage());
            return;
        }
        if (!config.load()) {
            return;
        }

        debug = getConfig().getBoolean("debug");
        serverId = getConfig().getString("server-id");

        String messengerType = getConfig().getString("messenger-type", "plugin_messages").toLowerCase(Locale.ROOT);
        switch (messengerType) {
            default:
                logger.warn(
                        "Messenger type '" + messengerType + "' is not supported, falling back to plugin messages!");
            case "plugin_messages":
                connector = new PluginMessageConnector(this);
                logger.warn("Using plugin messages as the messenger type will come with" +
                        " some caveats like sending to servers without players or to" +
                        " other proxies not working!");
                logger.warn("Please consider using one of the other messenger types!");
                break;
            case "redis":
                connector = new RedisConnector(this);
                break;
            case "mqtt":
                connector = new MqttConnector(this);
                break;
        }

        ConnectorCommand command = new ConnectorCommand(this);
        getProxy().getCommandManager().register(command, command);

        bridge = new Bridge(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyShutdownEvent event) {
        connector.close();
    }

    @Override
    public VelocityConnector getConnector() {
        return connector;
    }

    /**
     * Get the bridge helper class for executing certain actions on other servers
     * 
     * @return The bridge helper
     */
    public Bridge getBridge() {
        return bridge;
    }

    @Override
    public void runAsync(Runnable runnable) {
        getProxy().getScheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public MessageTarget.Type getSourceType() {
        return MessageTarget.Type.PROXY;
    }

    @Override
    public void logDebug(String message, Throwable... throwables) {
        if (debug) {
            logger.info("[DEBUG] " + message, throwables.length > 0 ? throwables[0] : null);
        }
    }

    @Override
    public void logInfo(String message, Throwable... throwables) {
        logger.info(message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public void logWarning(String message, Throwable... throwables) {
        logger.warn(message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public void logError(String message, Throwable... throwables) {
        logger.error(message, throwables.length > 0 ? throwables[0] : null);
    }

    @Override
    public String getServerName() {
        return PROXY_ID_PREFIX + serverId;
    }

    @Override
    public String getGlobalGroup() {
        return "";
    }

    @Override
    public Map<String, String> getGroups() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return "slconnector";
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public PluginConfig getConfig() {
        return config;
    }

    public InputStream getResourceAsStream(String file) {
        return getClass().getClassLoader().getResourceAsStream(file);
    }
}
