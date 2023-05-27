package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends SubCommandsFormat {
    public HelpSubCommand() {
        this.name = "help";
        this.description = "Information about the commands the plugin has.";
        this.usage = "/ad help";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        for (SubCommandsFormat value : AdvancedDisplays.subCommands.values()) {
            if (value.neededPermission == null || sender.hasPermission(value.neededPermission) || sender.hasPermission("ad.admin")) {
                sender.sendMessage(value.usage + " - " + value.description);
            }
        }
    }
}
