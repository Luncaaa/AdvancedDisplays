package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommandsFormat {
    public ReloadSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "reload";
        this.description = "Reloads the plugin's configuration files.";
        this.usage = "/ad reload";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "ad.reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.reloadConfigs();
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe configuration file has been reloaded successfully.", true));
    }
}