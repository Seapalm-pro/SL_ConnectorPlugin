package fr.mrbaguette07.slconnector.bungee.commands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ServerPlayerCommand extends SubCommand {

    public ServerPlayerCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "serverplayercommand <playername> <command...>",
                parent.getPermission() + ".serverplayercommand", "serverplayer", "player", "spc");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("No player with the name " + args[0] + " is online on this server!");
            return true;
        }
        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        sender.sendMessage(ChatColor.GRAY + "Executing '" + commandString + "' on the server for player '"
                + player.getName() + "'");
        plugin.getBridge().runServerPlayerCommand(player, commandString).thenAccept(success -> sender
                .sendMessage(success ? "Successfully executed command!" : "Error while executing the command."));
        return true;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) {
            return Collections.emptySet();
        }
        if (args.length == 0) {
            return plugin.getProxy().getPlayers().stream().map(ProxiedPlayer::getName)
                    .sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        } else if (args.length == 1) {
            return plugin.getProxy().getPlayers().stream().map(ProxiedPlayer::getName)
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptySet();
    }
}
