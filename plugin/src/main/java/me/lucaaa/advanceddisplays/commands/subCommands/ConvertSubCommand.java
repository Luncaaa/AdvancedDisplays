package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.ConversionManager;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.*;

public class ConvertSubCommand extends SubCommandsFormat {
    private boolean hasRunOnce = false;
    private final ArrayList<String> validVersions = new ArrayList<>(Arrays.asList("1.0", "1.1", "1.2", "1.2.1", "1.2.2", "1.2.3"));

    public ConvertSubCommand() {
        this.name = "convert";
        this.description = "Converts old display configurations to newer versions.";
        this.usage = "/ad convert [previous version]";
        this.minArguments = 1;
        this.executableByConsole = true;
        this.neededPermission = "ad.convert";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return validVersions;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!ConversionManager.isConversionNeeded()) {
            sender.sendMessage(MessagesManager.getColoredMessage("&aConfiguration files seem to be up-to-date. If this is an error, please report it on GitHub.", true));
            return;
        }

        if (!hasRunOnce) {
            sender.sendMessage(MessagesManager.getColoredMessage("&aRun the command again to confirm the conversion. It is highly recommended to create a backup of the displays folder before running the command again.", true));
            sender.sendMessage(MessagesManager.getColoredMessage("&cMake sure you select the correct version. Selecting the incorrect one may cause errors. Command usage: &b/ad convert [previous version]", true));
            this.hasRunOnce = true;
            return;
        }

        if (!validVersions.contains(args[1])) {
            sender.sendMessage(MessagesManager.getColoredMessage("&b" + args[1] + " &cis not a valid version.", true));
        }

        this.loopFiles(args[1], new File(AdvancedDisplays.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "displays"));

        ConversionManager.setConversionNeeded(false);
        AdvancedDisplays.reloadConfigs();
        sender.sendMessage(MessagesManager.getColoredMessage("&aThe displays have been successfully converted!", true));
    }

    private void loopFiles(String previousVersion, File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File insideFileOrDir : Objects.requireNonNull(fileOrDir.listFiles())) {
                this.loopFiles(previousVersion, insideFileOrDir);
            }

        } else {
            ConversionManager.convert(previousVersion, fileOrDir);
        }
    }
}