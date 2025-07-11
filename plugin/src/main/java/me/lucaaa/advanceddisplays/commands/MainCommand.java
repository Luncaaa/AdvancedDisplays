package me.lucaaa.advanceddisplays.commands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.commands.subcommands.*;
import org.bukkit.command.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand implements TabExecutor {
    private final AdvancedDisplays plugin;
    private final HashMap<String, SubCommandsFormat> subCommands = new HashMap<>();

    public MainCommand(AdvancedDisplays plugin) {
        this.plugin = plugin;
        subCommands.put("reload", new ReloadSubCommand(plugin));
        subCommands.put("create", new CreateSubCommand(plugin));
        subCommands.put("remove", new RemoveSubCommand(plugin));
        subCommands.put("movehere", new MoveHereSubCommand(plugin));
        subCommands.put("teleport", new TeleportSubCommand(plugin));
        subCommands.put("convert", new ConvertSubCommand(plugin));
        subCommands.put("list", new ListSubCommand(plugin));
        subCommands.put("edit", new EditSubCommand(plugin));
        subCommands.put("finish", new FinishSubCommand(plugin));
        subCommands.put("nextPage", new NextPageSubCommand(plugin));
        subCommands.put("previousPage", new PreviousPageSubCommand(plugin));
        subCommands.put("setPage", new SetPageSubCommand(plugin));
        subCommands.put("reset", new ResetSubCommand(plugin));
        subCommands.put("help", new HelpSubCommand(plugin, subCommands));
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        // If there are no arguments, show an error.
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cYou need to enter more arguments to run this command!"));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cUse &b/ad help &cto see the list of existing commands."));
            return true;
        }

        // If the subcommand does not exist, show an error.
        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe command " + args[0] + " &cdoes not exist!"));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cUse &b/ad help &cto see the list of existing commands."));
            return true;
        }

        // If the subcommand exists, get it from the map.
        SubCommandsFormat subCommand = subCommands.get(args[0]);

        // If the player who ran the command does not have the needed permissions, show an error.
        if (!sender.hasPermission("ad.admin") && (subCommand.neededPermission != null && !sender.hasPermission(subCommand.neededPermission))) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cYou don't have permission to execute this command!"));
            return true;
        }

        // If the command was executed by console but only players can execute it, show an error.
        if (sender instanceof ConsoleCommandSender && !subCommand.executableByConsole) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cOnly players can execute this command!"));
            return true;
        }

        // If the user entered fewer arguments than the subcommand needs, an error will appear.
        // args.size - 1 because the name of the subcommand is not included in the minArguments
        if (args.length - 1 < subCommand.minArguments) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cYou need to enter more arguments to run this command!"));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&7Correct usage: &c" + subCommand.usage));
            return true;
        }

        // If the command is valid, run it.
        subCommand.run(sender, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> completions = new ArrayList<>();

        // Tab completions for each subcommand. If the user is going to type the first argument, and it does not need any permission
        // to be executed, complete it. If it needs a permission, check if the user has it and add more completions.
        if (args.length == 1) {
            for (Map.Entry<String, SubCommandsFormat> entry : subCommands.entrySet()) {
                if (entry.getValue().neededPermission == null || sender.hasPermission(entry.getValue().neededPermission) || sender.hasPermission("ad.admin")) {
                    completions.add(entry.getKey());
                }
            }
        }

        // Command's second argument.
        SubCommandsFormat subcommand = subCommands.get(args[0]);
        if (args.length >= 2 && subcommand != null && sender.hasPermission(subcommand.neededPermission)) {
            completions = subCommands.get(args[0]).getTabCompletions(sender, args);
        }

        // Filters the array so only the completions that start with what the user is typing are shown.
        // For example, it can complete "reload", "removeDisplay" and "help". If the user doesn't type anything, all those
        // options will appear. If the user starts typing "r", only "reload" and "removeDisplay" will appear.
        // args[args.size-1] -> To get the argument the user is typing (first, second...)
        return completions.stream().filter(completion -> completion.toLowerCase().contains(args[args.length-1].toLowerCase())).toList();
    }
}

