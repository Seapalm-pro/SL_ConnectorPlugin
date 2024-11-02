package fr.mrbaguette07.slconnector.velocity.commands;

import fr.mrbaguette07.slconnector.velocity.Velocityslconnector;

public class ConnectorCommand extends SubCommand {

    public ConnectorCommand(Velocityslconnector plugin) {
        super(plugin, "slconnectorvelocity", "slconnector.command", "connectorvelocity", "connectorcommandvelocity",
                "connpluginvelocity", "cpv");
        registerSubCommand(new TeleportCommand(this));
        registerSubCommand(new TeleportToPlayerCommand(this));
        registerSubCommand(new ServerConsoleCommand(this));
        registerSubCommand(new ServerPlayerCommand(this));
        registerSubCommand(new ProxyConsoleCommand(this));
    }
}
