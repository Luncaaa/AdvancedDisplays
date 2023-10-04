package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.common.managers.ConversionManager;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConvertSubCommand extends SubCommandsFormat {
    private boolean hasRunOnce = false;
    private final ArrayList<String> validVersions = new ArrayList<>(Arrays.asList("1.0", "1.1", "1.2"));

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
            this.convert(previousVersion, fileOrDir);
        }
    }

    private void convert(String previousVersion, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection settingsSection;
        if (config.contains("settings")) {
            settingsSection = config.getConfigurationSection("settings");
        } else {
            settingsSection = config.createSection("settings");
        }
        assert settingsSection != null;

        switch (previousVersion) {
            case "1.0" -> {
                if (config.getString("block") != null) {
                    config.set("type", DisplayType.BLOCK.name());
                    settingsSection.set("block", config.getString("block"));
                    config.set("block", null);

                } else if (config.getString("item") != null) {
                    config.set("type", DisplayType.ITEM.name());
                    settingsSection.set("item", config.getString("item"));
                    settingsSection.set("enchanted", false);
                    settingsSection.set("itemTransformation", config.getString("itemTransformation"));
                    config.set("item", null);
                    config.set("itemTransformation", null);

                } else if (config.getString("text") != null) {
                    config.set("type", DisplayType.TEXT.name());
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);
                    settingsSection.set("text", List.of(Objects.requireNonNull(config.getString("text"))));
                    settingsSection.set("alignment", config.getString("alignment"));
                    settingsSection.set("backgroundColor", config.getString("backgroundColor") + ";255");
                    settingsSection.set("lineWidth", config.getInt("lineWidth"));
                    settingsSection.set("textOpacity", config.getInt("textOpacity"));
                    settingsSection.set("defaultBackground", config.getBoolean("defaultBackground"));
                    settingsSection.set("seeThrough", config.getBoolean("seeThrough"));
                    config.set("text", null);
                    config.set("alignment", null);
                    config.set("backgroundColor", null);
                    config.set("lineWidth", null);
                    config.set("textOpacity", null);
                    config.set("defaultBackground", null);
                    config.set("seeThrough", null);
                }

                Display display = (Display) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString("id"))));
                Objects.requireNonNull(display).remove();
                config.set("id", null);
            }

            case "1.1" -> {
                DisplayType type = DisplayType.valueOf(config.getString("type"));
                if (type == DisplayType.BLOCK) break;

                if (type == DisplayType.TEXT) {
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);
                    settingsSection.set("text", List.of(Objects.requireNonNull(settingsSection.get("text"))));

                } else if (type == DisplayType.ITEM) {
                    settingsSection.set("enchanted", false);
                }
            }

            case "1.2" -> {
                DisplayType type = DisplayType.valueOf(config.getString("type"));
                if (type == DisplayType.BLOCK) break;

                if (type == DisplayType.TEXT) {
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);

                } else if (type == DisplayType.ITEM) {
                    settingsSection.set("enchanted", false);
                }

                AdvancedDisplays.mainConfig.getConfig().set("text-update", null);
                AdvancedDisplays.mainConfig.save();
            }

            case "1.2.1" -> {
                if (DisplayType.valueOf(config.getString("type")) != DisplayType.ITEM) break;
                settingsSection.set("enchanted", false);
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
