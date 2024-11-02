package fr.mrbaguette07.slconnector.bukkit.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportToPlayerCommand extends SubCommand {
    public TeleportToPlayerCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "teleporttoplayer <player> [<target>]", parent.getPermission() + ".teleporttoplayer",
                "teleportplayer");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName;
        String targetName;
        if (args.length == 1 && sender instanceof Player) {
            playerName = sender.getName();
            targetName = args[0];
        } else if (args.length == 2) {
            playerName = args[0];
            targetName = args[1];
        } else {
            return false;
        }

        plugin.getBridge().teleport(playerName, targetName, sender::sendMessage).thenAccept(success -> {
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Successfully teleported " + playerName + " to " + targetName);
            } else {
                sender.sendMessage(ChatColor.RED + "Error while teleporting " + playerName + " to " + targetName);
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
