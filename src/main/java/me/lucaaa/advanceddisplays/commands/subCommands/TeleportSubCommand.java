package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeleportSubCommand extends SubCommandsFormat {
    public TeleportSubCommand() {
        this.name = "teleport";
        this.description = "Teleports the player to the display's location.";
        this.usage = "/ad teleport [name]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.teleport";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(AdvancedDisplays.displaysManager.displays.keySet().stream().toList());
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        BaseDisplay display = AdvancedDisplays.displaysManager.getDisplayFromMap(args[1]);

        if (display != null) {
            Player player = (Player) sender;
            player.teleport(display.getLocation());
            sender.sendMessage(MessagesManager.getColoredMessage("&aThe display &e" + args[1] + " &ahas been successfully removed.", true));
        } else {
            sender.sendMessage(MessagesManager.getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!", true));
        }
    }
}
