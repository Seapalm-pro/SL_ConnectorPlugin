package fr.mrbaguette07.slconnector.bungee.commands;

import fr.mrbaguette07.bungeeplugin.PluginCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ProxyConsoleCommand extends SubCommand {

    public ProxyConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "proxycommand <command...>", parent.getPermission() + ".proxycommand", "proxyconsole",
                "proxyconsolecommand", "proxy", "pcc");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String commandString = String.join(" ", args);
        sender.sendMessage(ChatColor.GRAY + "Executing '" + commandString + "' on other proxies");
        plugin.getBridge().runProxyConsoleCommand(commandString, sender::sendMessage).thenAccept(success -> sender
                .sendMessage(success ? "Successfully executed command!" : "Error while executing the command."));
        return true;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return plugin.getProxy().getPluginManager().getCommands().stream()
                    .map(Map.Entry::getValue)
                    .filter(e -> e instanceof PluginCommand ? ((PluginCommand<?>) e).hasCommandPermission(sender)
                            : e.hasPermission(sender))
                    .map(Command::getName)
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        } else if (args.length == 1) {
            return plugin.getProxy().getPluginManager().getCommands().stream()
                    .map(Map.Entry::getValue)
                    .filter(e -> e instanceof PluginCommand ? ((PluginCommand<?>) e).hasCommandPermission(sender)
                            : e.hasPermission(sender))
                    .map(Command::getName)
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptySet();
    }
}
