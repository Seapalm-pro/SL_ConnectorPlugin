package fr.mrbaguette07.slconnector.bukkit.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyConsoleCommand extends SubCommand {

    public ProxyConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "proxycommand <command...>", parent.getPermission() + ".proxycommand", "proxyconsole",
                "proxyconsolecommand", "proxy", "pcc");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String commandString = String.join(" ", args);
        sender.sendMessage(ChatColor.GRAY + "Executing '" + commandString + "' on proxy");
        plugin.getBridge().runProxyConsoleCommand(commandString, sender::sendMessage).thenAccept(success -> sender
                .sendMessage(success ? "Successfully executed command!" : "Error while executing the command."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            PluginCommand pluginCommand = plugin.getServer().getPluginCommand(args[1]);
            if (pluginCommand != null) {
                return pluginCommand.tabComplete(sender, args[1], Arrays.copyOfRange(args, 2, args.length));
            }
        }
        return null;
    }
}
