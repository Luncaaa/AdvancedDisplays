package me.lucaaa.advanceddisplays.commands.subcommands;

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

        int failedLoads = plugin.getDisplaysManager().getFailedLoads();

        String message;
        if (failedLoads > 0) {
            message =  "&eThe configuration files have been reloaded, but &6" + failedLoads + " &edisplay(s) have failed to load! Check console for more information.";
        } else {
            message = "&aThe configuration files have been reloaded successfully.";
        }
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage(message));
    }
}