package fr.mrbaguette07.slconnector;

import fr.mrbaguette07.slconnector.connector.ConnectingPlugin;
import fr.mrbaguette07.slconnector.connector.Connector;
import fr.mrbaguette07.slconnector.connector.MessageTarget;

import java.util.Locale;
import java.util.Map;

public interface slconnector<R> extends ConnectingPlugin {

    /**
     * Get the Connector which is used for sending and handling data
     * 
     * @return The Connector
     */
    Connector<? extends slconnector, R> getConnector();

    /**
     * The type of source that this plugin is. {@link MessageTarget.Type#SERVER} or
     * {@link MessageTarget.Type#PROXY}
     * 
     * @return The type of {@link MessageTarget.Type} that this implementation
     *         provides.
     */
    MessageTarget.Type getSourceType();

    default String getMessageChannel() {
        return "bbc:connection";
    }

    void logDebug(String message, Throwable... throwables);

    void logInfo(String message, Throwable... throwables);

    void logWarning(String message, Throwable... throwables);

    void logError(String message, Throwable... throwables);

    String getServerName();

    /**
     * @deprecated Use {@link #getGroup(String)} or {@link #getGlobalGroup()}
     */
    @Deprecated
    default String getGroup() {
        return getGlobalGroup();
    }

    String getGlobalGroup();

    Map<String, String> getGroups();

    /**
     * Get the group that should apply to the plugin (either per-plugin group or
     * global one)
     * 
     * @param pluginName The plugin's name
     * @return The per-plugin group or the global one
     */
    default String getGroup(String pluginName) {
        return getGroups().getOrDefault(pluginName.toLowerCase(Locale.ROOT), getGlobalGroup());
    }

    void runAsync(Runnable runnable);
}
