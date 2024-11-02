package fr.mrbaguette07.slconnector.bukkit.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerConsoleCommand extends SubCommand {

    public ServerConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "servercommand <servername|p:player> <command...>",
                parent.getPermission() + ".servercommand", "serverconsole", "serverconsolecommand", "server", "scc");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String serverName = args[0];
        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        sender.sendMessage(ChatColor.GRAY + "Executing '" + commandString + "' on server '" + serverName + "'");
        plugin.getBridge().runServerConsoleCommand(serverName, commandString, sender::sendMessage)
                .thenAccept(success -> sender.sendMessage(
                        success ? "Successfully executed command!" : "Error while executing the command."));
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
