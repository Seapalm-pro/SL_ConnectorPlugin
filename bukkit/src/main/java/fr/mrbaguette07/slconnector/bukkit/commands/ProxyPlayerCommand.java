package fr.mrbaguette07.slconnector.bukkit.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProxyPlayerCommand extends SubCommand {

    public ProxyPlayerCommand(ConnectorCommand parent) {
        super(parent.getPlugin(), "proxyplayercommand <playername> <command...>",
                parent.getPermission() + ".proxyplayercommand", "proxyplayer", "player", "ppc");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (!isValidPlayerName(args[0])) {
            sender.sendMessage(ChatColor.RED + "Nom de joueur invalide !");
            return true;
        }

        Player player = plugin.getServer().getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Aucun joueur avec le nom " + args[0] + " n'est connecté sur ce serveur !");
            return true;
        }
        
        String commandString = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        
        if (!isCommandAllowed(commandString)) {
            sender.sendMessage(ChatColor.RED + "Cette commande n'est pas autorisée pour des raisons de sécurité.");
            plugin.getLogger().warning("Le joueur " + sender.getName() + " a tenté d'exécuter une commande interdite en tant que " + player.getName() + " : " + commandString);
            return true;
        }
        
        sender.sendMessage(ChatColor.GRAY + "Exécution de '" + sanitizeForDisplay(commandString) + "' sur le proxy pour le joueur '"
                + player.getName() + "'");
        plugin.getBridge().runProxyPlayerCommand(player, commandString).thenAccept(success -> sender
                .sendMessage(success ? "Commande exécutée avec succès !" : "Erreur lors de l'exécution de la commande."));
        return true;
    }
    
    private boolean isValidPlayerName(String playerName) {
        return playerName != null && playerName.matches("^[a-zA-Z0-9_]{1,16}$");
    }
    
    private boolean isCommandAllowed(String commandString) {
        String lowerCmd = commandString.toLowerCase(Locale.ROOT).trim();
        
        String[] forbiddenCommands = {
            "stop", "end", "restart", "alert", "perm", "server"
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
        return null;
    }
}
