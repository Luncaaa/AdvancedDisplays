package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADEntityDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return plugin.getDisplaysManager().getDisplays().keySet().stream().toList();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADEntityDisplay display = plugin.getDisplaysManager().getDisplays().get(args[1]);

        if (display == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!"));
            return;
        }

        plugin.getInventoryManager().addEditingPlayer((Player) sender, display, plugin.getInventoryManager().getDisabledItems());
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are now editing the display &e" + display.getName() + "&a. Run &e/ad finish &ato get your old inventory back."));
    }
}