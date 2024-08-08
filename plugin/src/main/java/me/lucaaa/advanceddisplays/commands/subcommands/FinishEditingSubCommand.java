package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FinishEditingSubCommand extends SubCommandsFormat {
    public FinishEditingSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "finishEditing";
        this.description = "Returns your inventory when you are done editing a display.";
        this.usage = "/ad finishEditing";
        this.minArguments = 0;
        this.executableByConsole = false;
        this.neededPermission = null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (plugin.getInventoryManager().isPlayerNotEditing(player)) {
            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cYou are not editing any display!", true));
            return;
        }

        plugin.getInventoryManager().getEditingPlayer(player).finishEditing();
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYour old inventory has been successfully given back to you.", true));
    }
}
