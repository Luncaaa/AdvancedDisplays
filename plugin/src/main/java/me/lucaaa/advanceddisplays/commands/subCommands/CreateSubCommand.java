package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateSubCommand extends SubCommandsFormat {
    public CreateSubCommand() {
        this.name = "create";
        this.description = "Creates a new display.";
        this.usage = "/ad create [type] [name] [value]";
        this.minArguments = 3;
        this.executableByConsole = false;
        this.neededPermission = "ad.create";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(Arrays.stream(DisplayType.values()).map(Enum::name).toList());

        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("BLOCK") || args[1].equalsIgnoreCase("ITEM")) {
                return new ArrayList<>(Arrays.stream(Material.values()).map(Enum::name).toList());
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        try {
            Player player = (Player) sender;
            DisplayType type = DisplayType.valueOf(args[1].toUpperCase());

            AdvancedDisplays.displaysManager.createDisplay(player, type, args[2], String.join(" ", Arrays.copyOfRange(args, 3, args.length)));

        } catch (IllegalArgumentException e) {
            sender.sendMessage(MessagesManager.getColoredMessage("&cThe type &b" + args[1] + " &cis not a valid display type.", true));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
