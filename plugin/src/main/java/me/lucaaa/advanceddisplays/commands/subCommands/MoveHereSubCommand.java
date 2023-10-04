package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.Internal.BaseDisplay;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MoveHereSubCommand extends SubCommandsFormat {
    public MoveHereSubCommand() {
        this.name = "movehere";
        this.description = "Moves a display to the player's location.";
        this.usage = "/ad movehere [name]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.movehere";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(AdvancedDisplays.displaysManager.displays.keySet().stream().toList());
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        BaseDisplay display = AdvancedDisplays.displaysManager.getDisplayFromMap(args[1]);

        if (display == null) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!", true));
            return;
        }

        Player player = (Player) sender;
        display.setLocation(player.getEyeLocation());
        sender.sendMessage(MessagesManager.getColoredMessage("&aThe display &e" + args[1] + " &ahas been successfully moved.", true));
    }
}
