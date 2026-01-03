package fr.mrbaguette07.slconnector.bukkit.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProxyConsoleCommand extends SubCommand {

    public ProxyConsoleCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "proxycommand <command...>", parent.getPermission() + ".proxycommand", "proxyconsole",
                "proxyconsolecommand", "proxy", "pcc");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String commandString = String.join(" ", args);
        
        if (!isCommandAllowed(commandString)) {
            sender.sendMessage(ChatColor.RED + "Cette commande n'est pas autorisée pour des raisons de sécurité.");
            plugin.getLogger().warning("Le joueur " + sender.getName() + " a tenté d'exécuter une commande proxy interdite : " + commandString);
            return true;
        }
        
        sender.sendMessage(ChatColor.GRAY + "Exécution de '" + sanitizeForDisplay(commandString) + "' sur le proxy");
        plugin.getBridge().runProxyConsoleCommand(commandString, sender::sendMessage).thenAccept(success -> sender
                .sendMessage(success ? "Commande exécutée avec succès !" : "Erreur lors de l'exécution de la commande."));
        return true;
    }
    
    private boolean isCommandAllowed(String commandString) {
        String lowerCmd = commandString.toLowerCase(Locale.ROOT).trim();
        
        String[] forbiddenCommands = {
            "end", "stop", "restart", "reload", "alertraw", "greload", "perms",
            "glist", "permission", "lpv", "lpb", "luckperms"
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
