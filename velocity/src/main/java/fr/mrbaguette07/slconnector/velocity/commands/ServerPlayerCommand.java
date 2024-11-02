package fr.mrbaguette07.slconnector.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
    public boolean run(CommandSource sender, String alias, String[] args) {
        if (args.length < 2) {
            return false;
        }

        Player player = plugin.getProxy().getPlayer(args[0]).orElse(null);
        if (player == null) {
            sender.sendMessage(Component.text("No player with the name " + args[0] + " is online on this server!"));
            return true;
        }
        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        sender.sendMessage(Component
                .text("Executing '" + commandString + "' on the server for player '" + player.getUsername() + "'")
                .color(NamedTextColor.GRAY));
        plugin.getBridge().runServerPlayerCommand(player, commandString).thenAccept(success -> sender.sendMessage(
                Component.text(success ? "Successfully executed command!" : "Error while executing the command.")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args) {
        if (!hasPermission(sender)) {
            return Collections.emptyList();
        }
        if (args.length == 0) {
            return plugin.getProxy().getAllPlayers().stream().map(Player::getUsername)
                    .sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        } else if (args.length == 1) {
            return plugin.getProxy().getAllPlayers().stream().map(Player::getUsername)
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
