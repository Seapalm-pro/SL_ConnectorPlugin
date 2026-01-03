package fr.mrbaguette07.slconnector.bukkit.commands;



import fr.mrbaguette07.slconnector.LocationInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

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
        if (!isValidPlayerName(playerName)) {
            sender.sendMessage(ChatColor.RED + "Nom de joueur invalide !");
            return true;
        }

        String serverName = args[1];
        if (!isValidServerName(serverName)) {
            sender.sendMessage(ChatColor.RED + "Nom de serveur invalide !");
            return true;
        }

        if (args.length == 2) {
            plugin.getBridge().sendToServer(playerName, serverName, sender::sendMessage).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + playerName + " connecté avec succès à " + serverName);
                } else {
                    sender.sendMessage(ChatColor.RED + "Erreur lors de l'envoi de " + playerName + " vers " + serverName);
                }
            });
            return true;
        }

        if (args.length == 3) {
            if (!isValidWorldName(args[2])) {
                sender.sendMessage(ChatColor.RED + "Nom de monde invalide !");
                return true;
            }
            
            plugin.getBridge().teleport(playerName, serverName, args[2], sender::sendMessage)
                    .thenAccept(success -> {
                        if (!success) {
                            sender.sendMessage(ChatColor.RED + "Erreur lors de la téléportation...");
                        }
                    });
            return true;
        }

        if (args.length < 6) {
            return false;
        }

        try {
            if (!isValidWorldName(args[2])) {
                sender.sendMessage(ChatColor.RED + "Nom de monde invalide !");
                return true;
            }
            
            double x = Double.parseDouble(args[3]);
            double y = Double.parseDouble(args[4]);
            double z = Double.parseDouble(args[5]);
            
            if (!isValidCoordinate(x) || !isValidCoordinate(y) || !isValidCoordinate(z)) {
                sender.sendMessage(ChatColor.RED + "Coordonnées invalides ! Doivent être entre -30000000 et 30000000");
                return true;
            }
            
            float yaw = args.length > 6 ? Float.parseFloat(args[6]) : 0;
            float pitch = args.length > 7 ? Float.parseFloat(args[7]) : 0;
            
            if (!isValidAngle(yaw) || !isValidAngle(pitch)) {
                sender.sendMessage(ChatColor.RED + "Angles invalides ! Yaw : -180 à 180, Pitch : -90 à 90");
                return true;
            }

            LocationInfo location = new LocationInfo(serverName, args[2], x, y, z, yaw, pitch);

            sender.sendMessage("Envoi de la demande de téléportation pour " + playerName);
            plugin.getBridge().teleport(playerName, location, sender::sendMessage)
                    .thenAccept(success -> {
                        if (!success) {
                            sender.sendMessage(ChatColor.RED + "Erreur lors de la téléportation...");
                        }
                    });
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Erreur lors de l'analyse de l'entrée ! " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
    
    private boolean isValidPlayerName(String playerName) {
        return playerName != null && playerName.matches("^[a-zA-Z0-9_]{1,16}$");
    }
    
    private boolean isValidServerName(String serverName) {
        return serverName != null && serverName.matches("^[a-zA-Z0-9_-]{1,64}$");
    }
    
    private boolean isValidWorldName(String worldName) {
        return worldName != null && worldName.matches("^[a-zA-Z0-9_-]{1,64}$") 
            && !worldName.contains("..") && !worldName.contains("/") && !worldName.contains("\\");
    }
    
    private boolean isValidCoordinate(double coord) {
        return !Double.isNaN(coord) && !Double.isInfinite(coord) 
            && coord >= -30000000 && coord <= 30000000;
    }
    
    private boolean isValidAngle(float angle) {
        return !Float.isNaN(angle) && !Float.isInfinite(angle) 
            && angle >= -360 && angle <= 360;
    }
}
