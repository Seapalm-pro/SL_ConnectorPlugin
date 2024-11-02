package fr.mrbaguette07.slconnector.velocity.commands;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import fr.mrbaguette07.slconnector.velocity.Velocityslconnector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class SubCommand implements CommandMeta, SimpleCommand {
    protected final Velocityslconnector plugin;
    private final String name;
    private final String usage;
    private final String permission;
    private final Collection<String> aliases;
    private Map<String, SubCommand> subCommands = new LinkedHashMap<>();
    private Map<String, SubCommand> subCommandAliases = new LinkedHashMap<>();

    public SubCommand(Velocityslconnector plugin, String name) {
        this(plugin, name, null);
    }

    public SubCommand(Velocityslconnector plugin, String usage, String permission, String... aliases) {
        this.plugin = plugin;
        String[] usageParts = usage.split(" ", 2);
        this.name = usageParts[0];
        this.usage = usageParts.length > 1 ? usageParts[1] : "";
        this.permission = permission;
        this.aliases = new ArrayList<>();
        this.aliases.add(name);
        Collections.addAll(this.aliases, aliases);
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(Locale.ROOT), subCommand);
        for (String alias : subCommand.getAliases()) {
            subCommandAliases.put(alias.toLowerCase(Locale.ROOT), subCommand);
        }
    }

    public SubCommand getSubCommand(String name) {
        SubCommand subCommand = subCommands.get(name.toLowerCase(Locale.ROOT));
        if (subCommand == null) {
            return subCommandAliases.get(name.toLowerCase(Locale.ROOT));
        }
        return subCommand;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!hasPermission(invocation.source())) {
            invocation.source().sendMessage(Component.text("No permission").color(NamedTextColor.RED));
            return;
        }
        if (!run(invocation.source(), invocation.alias(), invocation.arguments()) && getUsage() != null) {
            invocation.source().sendMessage(Component.text("Usage: /" + invocation.alias() + " " + getUsage()));
        }
    }

    public boolean run(CommandSource sender, String alias, String[] args) {
        if (args.length == 0) {
            return false;
        }
        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand != null) {
            subCommand.execute(
                    new SubInvocation(sender, alias + " " + args[0], Arrays.copyOfRange(args, 1, args.length)));
            return true;
        }
        return false;
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return onTabComplete(invocation.source(), invocation.arguments());
    }

    public List<String> onTabComplete(CommandSource sender, String[] args) {
        if (!hasPermission(sender)) {
            return Collections.emptyList();
        }
        if (args.length == 0 || args[0].isEmpty()) {
            return new ArrayList<>(subCommands.keySet());
        }
        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand != null && subCommand.hasPermission(sender)) {
            return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        List<String> completions = new ArrayList<>();
        for (Map.Entry<String, SubCommand> e : subCommands.entrySet()) {
            if (e.getKey().startsWith(args[0].toLowerCase(Locale.ROOT)) && e.getValue().hasPermission(sender)) {
                completions.add(e.getKey());
            }
        }
        for (Map.Entry<String, SubCommand> e : subCommandAliases.entrySet()) {
            if (e.getKey().startsWith(args[0].toLowerCase(Locale.ROOT)) && e.getValue().hasPermission(sender)) {
                completions.add(e.getKey());
            }
        }
        return completions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return hasPermission(invocation.source());
    }

    public boolean hasPermission(CommandSource source) {
        return getPermission() == null || getPermission().isEmpty() || source.hasPermission(getPermission());
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public Velocityslconnector getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public Collection<CommandNode<CommandSource>> getHints() {
        return Collections.emptySet();
    }

    private class SubInvocation implements Invocation {
        private final CommandSource source;
        private final String alias;
        private final String[] args;

        public SubInvocation(CommandSource source, String alias, String[] args) {
            this.source = source;
            this.alias = alias;
            this.args = args;
        }

        @Override
        public String alias() {
            return alias;
        }

        @Override
        public CommandSource source() {
            return source;
        }

        @Override
        public String @NonNull [] arguments() {
            return args;
        }
    }
}
