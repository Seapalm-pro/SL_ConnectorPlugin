package fr.mrbaguette07.slconnector.bungee.commands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class ServerConsoleCommand extends SubCommand {

    public ServerConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "servercommand <servername|p:player> <command...>",
                parent.getPermission() + ".servercommand", "serverconsole", "serverconsolecommand", "server", "scc");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String serverName = args[0];
        if (serverName.startsWith("p:")) {
            ProxiedPlayer player = plugin.getProxy().getPlayer(serverName.substring(2));
            if (player != null) {
                if (player.getServer() != null) {
                    serverName = player.getServer().getInfo().getName();
                } else {
                    sender.sendMessage(
                            ChatColor.RED + "Player '" + player.getName() + "' is not connected to any server?");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player '" + serverName.substring(2) + "' is not online?");
                return false;
            }
        } else if (plugin.getProxy().getServerInfo(serverName) == null) {
            sender.sendMessage(ChatColor.GRAY + "There is no server with the name of '" + serverName
                    + "' on the proxy. Trying to send command anyways...");
        }
        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        sender.sendMessage(ChatColor.GRAY + "Executing '" + commandString + "' on server '" + serverName + "'");
        plugin.getBridge().runServerConsoleCommand(serverName, commandString, sender::sendMessage)
                .thenAccept(success -> sender.sendMessage(
                        success ? "Successfully executed command!" : "Error while executing the command."));
        return true;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) {
            return Collections.emptySet();
        }
        if (args.length == 0) {
            return plugin.getProxy().getServers().keySet();
        } else if (args.length == 1) {
            return plugin.getProxy().getServers().keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptySet();
    }
}
