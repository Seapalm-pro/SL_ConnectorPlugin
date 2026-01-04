package fr.mrbaguette07.slconnector.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TeleportToPlayerCommand extends SubCommand {
    public TeleportToPlayerCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "teleporttoplayer <player> [<target>]", parent.getPermission() + ".teleporttoplayer",
                "teleportplayer");
    }

    @Override
    public boolean run(CommandSource sender, String alias, String[] args) {
        String playerName;
        String targetName;
        if (args.length == 1 && sender instanceof Player) {
            playerName = ((Player) sender).getUsername();
            targetName = args[0];
        } else if (args.length == 2) {
            playerName = args[0];
            targetName = args[1];
        } else {
            return false;
        }

        Player player = plugin.getProxy().getPlayer(playerName).orElse(null);
        if (player == null) {
            sender.sendMessage(
                    Component.text("No player with the name " + playerName + " found!").color(NamedTextColor.RED));
            return true;
        }

        Player target = plugin.getProxy().getPlayer(targetName).orElse(null);
        if (target == null) {
            sender.sendMessage(
                    Component.text("Aucun joueur avec le nom " + targetName + " trouvé !").color(NamedTextColor.RED));
            return true;
        }

        plugin.getBridge()
                .teleport(player.getUsername(), target.getUsername(),
                        m -> sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(m)))
                .thenAccept(success -> {
                    if (!success) {
                        sender.sendMessage(Component.text("Erreur lors de la téléportation...").color(NamedTextColor.RED));
                    }
                });
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
        } else if (args.length == 2) {
            return plugin.getProxy().getAllPlayers().stream().map(Player::getUsername)
                    .filter(s -> s.startsWith(args[1].toLowerCase(Locale.ROOT))).sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
