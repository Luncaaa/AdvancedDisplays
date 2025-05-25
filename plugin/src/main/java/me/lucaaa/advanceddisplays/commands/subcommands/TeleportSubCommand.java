package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADEntityDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportSubCommand extends SubCommandsFormat {
    public TeleportSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "teleport";
        this.description = "Teleports the player to the display's location.";
        this.usage = "/ad teleport [name]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.teleport";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return plugin.getDisplaysManager().getDisplays().keySet().stream().toList();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADEntityDisplay display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);

        if (display != null) {
            Player player = (Player) sender;
            player.teleport(display.getLocation());
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[1] + " &ahas been successfully removed."));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!"));
        }
    }
}
