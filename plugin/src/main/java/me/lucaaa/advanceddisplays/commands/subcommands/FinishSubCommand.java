package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FinishSubCommand extends SubCommandsFormat {
    public FinishSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "finish";
        this.description = "Stops what you are currently doing (creating an ATTACHED display or editing a display).";
        this.usage = "/ad finish";
        this.minArguments = 0;
        this.executableByConsole = false;
        this.neededPermission = null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        boolean sendError = true;

        if (plugin.getDisplaysManager().isPlayerAttaching(player)) {
            sendError = false;
            plugin.getDisplaysManager().removeAttachingDisplay(player);
            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are no longer creating an ATTACHED display."));
        }

        if (plugin.getInventoryManager().isPlayerEditing(player)) {
            sendError = false;
            plugin.getInventoryManager().getEditingPlayer(player).finishEditing();
            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYour old inventory has been successfully given back to you."));
        }

        if (sendError) {
            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cYou are not editing or creating any display!"));
        }
    }
}