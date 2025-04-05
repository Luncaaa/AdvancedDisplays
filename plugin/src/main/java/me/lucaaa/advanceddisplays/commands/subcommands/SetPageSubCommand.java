package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetPageSubCommand extends SubCommandsFormat {
    public SetPageSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "setPage";
        this.description = "Switches to the given page of a text display.";
        this.usage = "/ad nextPage [name] [page]";
        this.minArguments = 2;
        this.executableByConsole = true;
        this.neededPermission = "ad.page";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length <= 2) {
            return plugin.getDisplaysManager().getDisplays().values().stream().filter(display -> display.getType() == DisplayType.TEXT).map(ADBaseDisplay::getName).toList();
        }

        ADBaseDisplay display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);
        if (!(display instanceof ADTextDisplay textDisplay)) return List.of();

        return textDisplay.getText().keySet().stream().toList();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        ADBaseDisplay display = plugin.getDisplaysManager().getDisplayFromMap(args[1]);

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

        textDisplay.setPage(args[2]);
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + args[1] + " &ais now showing its next page."));
    }
}
