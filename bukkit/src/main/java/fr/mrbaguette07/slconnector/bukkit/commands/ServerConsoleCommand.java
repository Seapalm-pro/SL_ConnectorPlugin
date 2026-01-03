package fr.mrbaguette07.slconnector.bukkit.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ServerConsoleCommand extends SubCommand {

    public ServerConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "servercommand <servername|p:player> <command...>",
                parent.getPermission() + ".servercommand", "serverconsole", "serverconsolecommand", "server", "scc");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String serverName = args[0];
        if (!isValidServerName(serverName)) {
            sender.sendMessage(ChatColor.RED + "Nom de serveur invalide ! Seuls les caractères alphanumériques, tirets et underscores sont autorisés.");
            return true;
        }

        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        
        if (!isCommandAllowed(commandString)) {
            sender.sendMessage(ChatColor.RED + "Cette commande n'est pas autorisée pour des raisons de sécurité.");
            plugin.getLogger().warning("Le joueur " + sender.getName() + " a tenté d'exécuter une commande interdite : " + commandString);
            return true;
        }
        
        sender.sendMessage(ChatColor.GRAY + "Exécution de '" + sanitizeForDisplay(commandString) + "' sur le serveur '" + serverName + "'");
        plugin.getBridge().runServerConsoleCommand(serverName, commandString, sender::sendMessage)
                .thenAccept(success -> sender.sendMessage(
                        success ? "Commande exécutée avec succès !" : "Erreur lors de l'exécution de la commande."));
        return true;
    }
    
    private boolean isValidServerName(String serverName) {
        return serverName != null && serverName.matches("^[a-zA-Z0-9_-]+$") && serverName.length() <= 64;
    }
    
    private boolean isCommandAllowed(String commandString) {
        String lowerCmd = commandString.toLowerCase(Locale.ROOT).trim();
        
        String[] forbiddenCommands = {
            "stop", "restart", "reload", "op", "deop", "permissions", "perm", "lp",
            "pardon", "ban-ip", "execute", "scoreboard", "data",
            "fill", "setblock", "clone", "give @", "clear @"
        };
        
        for (String forbidden : forbiddenCommands) {
            if (lowerCmd.startsWith(forbidden + " ") || lowerCmd.equals(forbidden)) {
                return false;
            }
        }
        
        if (lowerCmd.contains("../") || lowerCmd.contains("..\\") || 
            lowerCmd.contains(";") || lowerCmd.contains("|") || lowerCmd.contains("`")) {
            return false;
        }
        
        return true;
    }
    
    private String sanitizeForDisplay(String input) {
        if (input == null) return "";
        return input.replaceAll("§", "");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            PluginCommand pluginCommand = plugin.getServer().getPluginCommand(args[1]);
            if (pluginCommand != null) {
                return pluginCommand.tabComplete(sender, args[1], Arrays.copyOfRange(args, 2, args.length));
            }
        }
        return null;
    }
}
