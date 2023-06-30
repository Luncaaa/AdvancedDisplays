package me.lucaaa.advanceddisplays.commands;

import me.lucaaa.advanceddisplays.commands.subCommands.*;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand implements CommandExecutor, TabCompleter {
    public static final HashMap<String, SubCommandsFormat>  subCommands = new HashMap<>();

    public MainCommand() {
        subCommands.put("help", new HelpSubCommand());
        subCommands.put("reload", new ReloadSubCommand());
        subCommands.put("create", new CreateSubCommand());
        subCommands.put("remove", new RemoveSubCommand());
        subCommands.put("movehere", new MoveHereSubCommand());
        subCommands.put("teleport", new TeleportSubCommand());
        subCommands.put("test", new TestSubCommand());
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        // If there are no arguments, show an error.
        if (args.length == 0) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cYou need to enter more arguments to run this command!", true));
            sender.sendMessage(MessagesManager.getColoredMessage("&cUse &b/ad help &cto see the list of existing commands.", true));
            return true;
        }

        // If the subcommand does not exist, show an error.
        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cThe command " + args[0] + " &cdoes not exist!", true));
            sender.sendMessage(MessagesManager.getColoredMessage("&cUse &b/ad help &cto see the list of existing commands.", true));
            return true;
        }

        // If the subcommand exists, get it from the map.
        SubCommandsFormat subCommand = subCommands.get(args[0]);

        // If the player who ran the command does not have the needed permissions, show an error.
        if (!sender.hasPermission("ad.admin") && (subCommand.neededPermission != null && !sender.hasPermission(subCommand.neededPermission))) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cYou don't have permission to execute this command!", true));
            return true;
        }

        // If the command was executed by console but only players can execute it, show an error.
        if (sender instanceof ConsoleCommandSender && !subCommand.executableByConsole) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cOnly players can execute this command!", true));
            return true;
        }

        // If the user entered fewer arguments than the subcommand needs, an error will appear.
        // args.size - 1 because the name of the subcommand is not included in the minArguments
        if (args.length - 1 < subCommand.minArguments) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cYou need to enter more arguments to run this command!", true));
            sender.sendMessage(MessagesManager.getColoredMessage("&7Correct usage: &c" + subCommand.usage, true));
            return true;
        }

        // If the command is valid, run it.
        try {
            subCommand.run(sender, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        // Tab completions for each subcommand. If the user is going to type the first argument, and it does not need any permission
        // to be executed, complete it. If it needs a permission, check if the user has it and add more completions.
        if (args.length == 1) {
            for (Map.Entry<String, SubCommandsFormat> entry : subCommands.entrySet()) {
                if (entry.getValue().neededPermission == null || sender.hasPermission(entry.getValue().neededPermission) || sender.hasPermission("ad.admin")) {
                    completions.add(entry.getKey());
                } else if (sender.hasPermission(entry.getValue().neededPermission) || sender.hasPermission("plugin.admin")) {
                    completions.add(entry.getKey());
                }
            }
        }

        // Command's second argument.
        if (args.length >= 2 && subCommands.containsKey(args[0])) {
            completions = subCommands.get(args[0]).getTabCompletions(sender, args);
        }

        // Filters the array so only the completions that start with what the user is typing are shown.
        // For example, it can complete "reload", "removeDisplay" and "help". If the user doesn't type anything, all those
        // options will appear. If the user starts typing "r", only "reload" and "removeDisplay" will appear.
        // args[args.size-1] -> To get the argument the user is typing (first, second...)
        return completions.stream().filter(completion -> completion.startsWith(args[args.length-1])).toList();
    }
}

