package fr.mrbaguette07.slconnector.bukkit.commands;



import fr.mrbaguette07.slconnector.LocationInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TeleportCommand extends SubCommand {
    public TeleportCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "teleport <player> <server> [<world> <x> <y> <z> [<yaw> <pitch>]]",
                parent.getPermission() + ".teleport", "tp", "send");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String playerName = args[0];
        String serverName = args[1];

        if (args.length == 2) {
            plugin.getBridge().sendToServer(playerName, serverName, sender::sendMessage).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully connected " + playerName + " to " + serverName);
                } else {
                    sender.sendMessage(ChatColor.RED + "Error while sending " + playerName + " to " + serverName);
                }
            });
            return true;
        }

        if (args.length == 3) {
            plugin.getBridge().teleport(playerName, serverName, args[2], sender::sendMessage)
                    .thenAccept(success -> {
                        if (!success) {
                            sender.sendMessage(ChatColor.RED + "Error while teleporting...");
                        }
                    });
            return true;
        }

        if (args.length < 6) {
            return false;
        }

        try {
            LocationInfo location = new LocationInfo(
                    serverName,
                    args[2],
                    Double.parseDouble(args[3]),
                    Double.parseDouble(args[4]),
                    Double.parseDouble(args[5]),
                    args.length > 6 ? Float.parseFloat(args[6]) : 0,
                    args.length > 7 ? Float.parseFloat(args[7]) : 0);

            sender.sendMessage("Sending teleport request for " + playerName);
            plugin.getBridge().teleport(playerName, location, sender::sendMessage)
                    .thenAccept(success -> {
                        if (!success) {
                            sender.sendMessage(ChatColor.RED + "Error while teleporting...");
                        }
                    });
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Error while parsing input! " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
