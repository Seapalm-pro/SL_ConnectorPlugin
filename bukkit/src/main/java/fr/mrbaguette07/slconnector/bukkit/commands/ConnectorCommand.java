package fr.mrbaguette07.slconnector.bukkit.commands;

import fr.mrbaguette07.slconnector.bukkit.Bukkitslconnector;

public class ConnectorCommand extends SubCommand {

    public ConnectorCommand(Bukkitslconnector plugin) {
        super(plugin, "slconnector", "slconnector.command");
        registerSubCommand(new TeleportCommand(this));
        registerSubCommand(new TeleportToPlayerCommand(this));
        registerSubCommand(new ServerConsoleCommand(this));
        registerSubCommand(new ProxyConsoleCommand(this));
        registerSubCommand(new ProxyPlayerCommand(this));
    }
}
