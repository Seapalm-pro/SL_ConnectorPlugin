package fr.mrbaguette07.slconnector.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProxyConsoleCommand extends SubCommand {

    public ProxyConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "proxycommand <command...>", parent.getPermission() + ".proxycommand", "proxyconsole",
                "proxyconsolecommand", "proxy", "pcc");
    }

    @Override
    public boolean run(CommandSource sender, String alias, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String commandString = String.join(" ", args);
        sender.sendMessage(
                Component.text("Executing '" + commandString + "' on other proxies").color(NamedTextColor.GRAY));
        plugin.getBridge()
                .runProxyConsoleCommand(commandString,
                        m -> sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(m)))
                .thenAccept(success -> sender.sendMessage(Component
                        .text(success ? "Successfully executed command!" : "Error while executing the command.")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args) {
        if (!hasPermission(sender)) {
            return Collections.emptyList();
        }
        if (args.length == 0) {
            return new ArrayList<>(plugin.getProxy().getCommandManager().getAliases());
        } else if (args.length == 1) {
            return plugin.getProxy().getCommandManager().getAliases().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
