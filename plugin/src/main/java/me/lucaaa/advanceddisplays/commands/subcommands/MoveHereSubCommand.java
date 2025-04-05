package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MoveHereSubCommand extends SubCommandsFormat {
    public MoveHereSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "movehere";
        this.description = "Moves a display to the player's location.";
        this.usage = "/ad movehere [name] <center>";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.movehere";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return plugin.getDisplaysManager().getDisplays().keySet().stream().toList();
        } else {
            return List.of("center");
        }
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADBaseDisplay display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);

        if (display == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!"));
            return;
        }

        Player player = (Player) sender;
        display.setLocation(player.getLocation());

        if (args.length >= 3 && (args[2].equalsIgnoreCase("center"))) {
            display.center();
        }

        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[1] + " &ahas been successfully moved."));
    }
}