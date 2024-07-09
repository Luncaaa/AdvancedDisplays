package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EditSubCommand extends SubCommandsFormat {
    public EditSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "edit";
        this.description = "Opens the GUI editor for a certain display.";
        this.usage = "/ad edit [name]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.edit";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(plugin.getDisplaysManager().getDisplays().keySet().stream().toList());
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADBaseDisplay display = plugin.getDisplaysManager().getDisplays().get(args[1]);

        if (display == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!", true));
            return;
        }

        plugin.getInventoryManager().addEditingPlayer((Player) sender, display);
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are now editing the display &e" + display.getName() + "&a. Run &e/ad finishEditing &ato get your old inventory back.", true));
    }
}
