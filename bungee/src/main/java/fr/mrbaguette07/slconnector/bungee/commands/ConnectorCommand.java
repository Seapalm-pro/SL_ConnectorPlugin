package fr.mrbaguette07.slconnector.bungee.commands;


import fr.mrbaguette07.slconnector.bungee.Bungeeslconnector;

public class ConnectorCommand extends SubCommand {

    public ConnectorCommand(Bungeeslconnector plugin) {
        super(plugin, "slconnectorbungee");
        registerSubCommand(new TeleportCommand(this));
        registerSubCommand(new TeleportToPlayerCommand(this));
        registerSubCommand(new ServerConsoleCommand(this));
        registerSubCommand(new ServerPlayerCommand(this));
        registerSubCommand(new ProxyConsoleCommand(this));
    }
}
