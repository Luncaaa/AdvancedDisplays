package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MoveToSubCommand extends SubCommandsFormat {
    public MoveToSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "moveto";
        this.description = "Moves a display to the given location.";
        this.usage = "/ad moveto [name] [x/~/*] [y/~/*] [z/~/*] <world/~/*>";
        this.minArguments = 4;
        this.executableByConsole = true;
        this.neededPermission = "ad.moveto";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> plugin.getDisplaysManager().getDisplays().keySet().stream().toList();
            case 3, 4, 5 -> List.of("~", "*");
            case 6 -> new java.util.ArrayList<>(plugin.getServer().getWorlds().stream().map(World::getName).toList());
            default -> List.of();
        };
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        BaseEntity display = plugin.getDisplaysManager().getDisplays().get(args[1]);

        if (display == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!"));
            return;
        }

        if (String.join(" ", args).contains("~") && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cOnly players can use \"~\" (relative to the player's position)."));
            return;
        }

        World world;
        if (args.length >= 6) {
            String worldName = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
            world = plugin.getServer().getWorld(worldName);

            if (world == null) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe world &b" + worldName + " &cdoes not exist!"));
                return;
            }
        } else {
            world = display.getLocation().getWorld();
        }

        try {
            double x = Utils.parsePosition(args[2], Utils.CoordComponent.X, display.getLocation(), sender);
            double y = Utils.parsePosition(args[3], Utils.CoordComponent.Y, display.getLocation(), sender);
            double z = Utils.parsePosition(args[4], Utils.CoordComponent.Z, display.getLocation(), sender);

            display.setLocation(new Location(world, x, y, z));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display has been successfully moved to &e" + x + " " + y + " " + z + " &ain world &e" + Objects.requireNonNull(world).getName()));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cOne of the coordinates is not a valid number or relative position! For decimals use dots (\".\"), NOT commas (\",\")."));
        }

    }
}