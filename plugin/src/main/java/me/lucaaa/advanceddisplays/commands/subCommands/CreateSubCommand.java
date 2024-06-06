package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateSubCommand extends SubCommandsFormat {
    private final ArrayList<String> blocksList = new ArrayList<>();

    public CreateSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "create";
        this.description = "Creates a new display.";
        this.usage = "/ad create [type] [name] [value]";
        this.minArguments = 3;
        this.executableByConsole = false;
        this.neededPermission = "ad.create";

        for (Material material : Material.values()) {
            try {
                // If the material is a block, it will be added to the blocks list.
                material.createBlockData();
                this.blocksList.add(material.name());
            } catch (IllegalArgumentException | NullPointerException ignored) {}
        }
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(Arrays.stream(DisplayType.values()).map(Enum::name).toList());

        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("ITEM")) {
                return new ArrayList<>(Arrays.stream(Material.values()).map(Enum::name).toList());

            } else if (args[1].equalsIgnoreCase("BLOCK")) {
                return this.blocksList;
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        DisplayType type;
        try {
            type = DisplayType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe type &b" + args[1] + " &cis not a valid display type.", true));
            return;
        }
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        if (type == DisplayType.BLOCK || type == DisplayType.ITEM) {
            if (Material.getMaterial(value) == null) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + value + " &cis not a valid material!", true));
                return;
            }
        }

        if (type == DisplayType.BLOCK) {
            try {
                Objects.requireNonNull(Material.getMaterial(value)).createBlockData();
            } catch (IllegalArgumentException e) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe material &b" + value + " &cis not a valid block.", true));
                return;
            }
        }

        ADBaseDisplay newDisplay = null;
        switch (type) {
            case TEXT -> newDisplay = plugin.getDisplaysManager().createTextDisplay(player.getEyeLocation(), args[2], List.of(value), true);
            case ITEM -> newDisplay = plugin.getDisplaysManager().createItemDisplay(player.getEyeLocation(), args[2], Material.getMaterial(value), true);
            case BLOCK -> newDisplay = plugin.getDisplaysManager().createBlockDisplay(player.getEyeLocation(), args[2], Objects.requireNonNull(Material.getMaterial(value)).createBlockData(), true);
        }

        if (newDisplay == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cA display with the name &b" + args[2] + " &calready exists!", true));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[2] + " &ahas been successfully created.", true));
        }
    }
}
