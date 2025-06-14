package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.displays.ADBaseEntity;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetPageSubCommand extends SubCommandsFormat {
    public SetPageSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "setPage";
        this.description = "Switches to the given page of a text display.";
        this.usage = "/ad nextPage [name] [page] <player> <silent>";
        this.minArguments = 2;
        this.executableByConsole = true;
        this.neededPermission = "ad.page";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return plugin.getDisplaysManager().getDisplays().values().stream().filter(display -> display.getType() == DisplayType.TEXT).map(ADBaseEntity::getName).toList();

        } else if (args.length == 3) {
            ADBaseEntity display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);
            if (!(display instanceof ADTextDisplay textDisplay)) return List.of();

            return textDisplay.getText().keySet().stream().toList();

        } else if (args.length == 4) {
            return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();

        } else {
            return List.of("true", "false");
        }
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADBaseEntity display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);

        if (display == null) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not exist!"));
            return;
        }

        if (!(display instanceof ADTextDisplay textDisplay)) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cis not a text display!"));
            return;
        }

        if (!textDisplay.getText().containsKey(args[2])) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe display &b" + args[1] + " &cdoes not have a page named &b" + args[2] + "&c!"));
            return;
        }

        if (args.length >= 4) {
            Player player = plugin.getServer().getPlayer(args[3]);
            if (player == null) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe player &b" + args[3] + " &cdoes not exist or was not found!"));
            } else {
                textDisplay.setPage(args[2], player);
            }
        } else {
            textDisplay.setPage(args[2]);
        }

        // "parseBoolean" returns false even if it's not a boolean.
        if (!Boolean.parseBoolean(args[args.length - 1])) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[1] + " &ais now showing its next page."));
        }
    }
}
