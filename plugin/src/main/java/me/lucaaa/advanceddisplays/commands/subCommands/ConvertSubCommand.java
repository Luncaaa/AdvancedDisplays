package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.ConversionManager;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.*;

public class ConvertSubCommand extends SubCommandsFormat {
    private boolean hasRunOnce = false;

    public ConvertSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "convert";
        this.description = "Converts old display configurations to newer versions.";
        this.usage = "/ad convert";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "ad.convert";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!ConversionManager.isConversionNeeded()) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aConfiguration files seem to be up-to-date. If this is an error, please report it on GitHub.", true));
            return;
        }

        if (!hasRunOnce) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aRun the command again to confirm the conversion. It is highly recommended to create a backup of the displays folder before running the command again.", true));
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cMake sure you select the correct version. Selecting the incorrect one may cause errors. Command usage: &b/ad convert [previous version]", true));
            this.hasRunOnce = true;
            return;
        }

        this.loopFiles(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "displays"));

        ConversionManager.setConversionNeeded(false);
        plugin.reloadConfigs();
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe displays have been successfully converted!", true));
        this.hasRunOnce = false;
    }

    private void loopFiles(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File insideFileOrDir : Objects.requireNonNull(fileOrDir.listFiles())) {
                this.loopFiles(insideFileOrDir);
            }

        } else {
            ConversionManager.convert(plugin, fileOrDir);
        }
    }
}