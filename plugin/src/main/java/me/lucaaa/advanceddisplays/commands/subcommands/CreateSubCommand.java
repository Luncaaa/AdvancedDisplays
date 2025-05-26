package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.AttachedDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateSubCommand extends SubCommandsFormat {
    private final List<String> blocksList = new ArrayList<>();

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
                blocksList.add(material.name());
            } catch (IllegalArgumentException | NullPointerException ignored) {}
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> completions = new ArrayList<>(Arrays.stream(DisplayType.values()).map(Enum::name).toList());
            completions.add("ATTACHED");
            return completions;

        } else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("ITEM")) {
                return Arrays.stream(Material.values()).map(Enum::name).toList();

            } else if (args[1].equalsIgnoreCase("BLOCK")) {
                return blocksList;

            } else if (args[1].equalsIgnoreCase("ATTACHED")) {
                return Arrays.stream(AttachedDisplay.Side.values()).map(Enum::name).toList();

            } else if (args[1].equalsIgnoreCase("ENTITY")) {
                return Arrays.stream(EntityType.values()).map(Enum::name).toList();
            }
        }

        return List.of();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (plugin.getDisplaysManager().existsDisplay(args[2])) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cA display with the name &b" + args[2] + " &calready exists!"));
            return;
        }

        if (args[1].equalsIgnoreCase("ATTACHED")) {
            AttachedDisplay.Side side;
            try {
                side = AttachedDisplay.Side.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe side &b" + args[3] + " &cis not a valid side."));
                return;
            }

            String value = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            plugin.getDisplaysManager().addAttachingPlayer(player, new AttachedDisplay(args[2], side, value , true));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&6Right-click the block where you want your display to be attached or run &e/ad finish &6to cancel the action."));

            return;
        }

        DisplayType type;
        try {
            type = DisplayType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe type &b" + args[1] + " &cis not a valid display type."));
            return;
        }
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        if (type == DisplayType.BLOCK || type == DisplayType.ITEM) {
            if (Material.getMaterial(value) == null) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + value + " &cis not a valid material!"));
                return;
            }
        }

        if (type == DisplayType.ENTITY) {
            try {
                // Check if the entity type exists.
                EntityType.valueOf(value);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + value + " &cis not a valid material!"));
                return;
            }
        }

        if (type == DisplayType.BLOCK) {
            try {
                Objects.requireNonNull(Material.getMaterial(value)).createBlockData();
            } catch (IllegalArgumentException e) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe material &b" + value + " &cis not a valid block."));
                return;
            }
        }

        switch (type) {
            case TEXT -> plugin.getDisplaysManager().createTextDisplay(player.getEyeLocation(), args[2], value.replace("\\n", "\n"), true);
            case ITEM -> plugin.getDisplaysManager().createItemDisplay(player.getEyeLocation(), args[2], Material.getMaterial(value), true);
            case BLOCK -> plugin.getDisplaysManager().createBlockDisplay(player.getEyeLocation(), args[2], Objects.requireNonNull(Material.getMaterial(value)).createBlockData(), true);
            case ENTITY -> plugin.getDisplaysManager().createEntityDisplay(player.getLocation(), args[2], EntityType.valueOf(value), true);
        }

        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[2] + " &ahas been successfully created."));
    }
}