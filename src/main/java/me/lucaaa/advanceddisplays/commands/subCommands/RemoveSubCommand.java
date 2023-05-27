package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class RemoveSubCommand extends SubCommandsFormat {
    public RemoveSubCommand() {
        this.name = "remove";
        this.description = "Removes an existing display.";
        this.usage = "/ad remove [name]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.remove";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(AdvancedDisplays.displaysManager.displays.keySet().stream().toList());
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean couldRemoveDisplay = AdvancedDisplays.displaysManager.removeDisplay(args[1]);

        if (couldRemoveDisplay) {
            sender.sendMessage(MessagesManager.getColoredMessage("&aThe display &e" + args[1] + " &ahas been successfully removed.", true));
        } else {
            sender.sendMessage(MessagesManager.getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!", true));
        }
    }
}
