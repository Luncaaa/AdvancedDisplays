package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Utils;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpSubCommand extends SubCommandsFormat {
    private final Map<String, SubCommandsFormat> subCommands;

    public HelpSubCommand(AdvancedDisplays plugin, Map<String, SubCommandsFormat> subCommands) {
        super(plugin);
        this.name = "help";
        this.description = "Information about the commands the plugin has.";
        this.usage = "/ad help";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = null;
        this.subCommands = subCommands;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.getColoredText("&c---------[ AdvancedDisplays help menu ]---------"));

        sender.sendMessage(Utils.getColoredText("&cCommands: &7&o([] - mandatory args, <> - optional args)"));
        for (SubCommandsFormat value : subCommands.values()) {
            if (value.neededPermission == null || sender.hasPermission(value.neededPermission)) {
                sender.sendMessage(Utils.getColoredText(" &7- &6" + value.usage + "&7: &e" + value.description));
            }
        }
    }
}