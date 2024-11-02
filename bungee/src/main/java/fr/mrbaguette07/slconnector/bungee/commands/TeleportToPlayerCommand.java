package fr.mrbaguette07.slconnector.bungee.commands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class TeleportToPlayerCommand extends SubCommand {
    public TeleportToPlayerCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "teleporttoplayer <player> [<target>]", parent.getPermission() + ".teleporttoplayer",
                "teleportplayer");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        String playerName;
        String targetName;
        if (args.length == 1 && sender instanceof ProxiedPlayer) {
            playerName = sender.getName();
            targetName = args[0];
        } else if (args.length == 2) {
            playerName = args[0];
            targetName = args[1];
        } else {
            return false;
        }

        ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name " + playerName + " found!");
            return true;
        }

        ProxiedPlayer target = plugin.getProxy().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name " + targetName + " found!");
            return true;
        }

        plugin.getBridge().teleport(player.getName(), target.getName(), sender::sendMessage)
                .thenAccept(success -> {
                    if (!success) {
                        sender.sendMessage(ChatColor.RED + "Error while teleporting...");
                    }
                });
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
        } else if (args.length == 2) {
            return plugin.getProxy().getPlayers().stream().map(ProxiedPlayer::getName)
                    .filter(s -> s.startsWith(args[1].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptySet();
    }
}
