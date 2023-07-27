package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import me.lucaaa.advanceddisplays.displays.DisplayType;
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

    public ConvertSubCommand() {
        this.name = "convert";
        this.description = "Converts old display configurations to newer versions.";
        this.usage = "/ad convert [previous version]";
        this.minArguments = 1;
        this.executableByConsole = false;
        this.neededPermission = "ad.convert";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(Arrays.asList("1.0", "1.1", "1.2"));
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!AdvancedDisplays.needsConversion) {
            sender.sendMessage(MessagesManager.getColoredMessage("&aConfiguration files seem to be up-to-date. If this is an error, please report it on GitHub.", true));
            return;
        }

        if (!hasRunOnce) {
            sender.sendMessage(MessagesManager.getColoredMessage("&aRun the command again to confirm the conversion. It is highly recommended to create a backup of the displays folder before running the command again.", true));
            sender.sendMessage(MessagesManager.getColoredMessage("&cMake sure you select the correct version. Selecting the incorrect one may cause errors. Command usage: &b/ad convert [previous version]", true));
            this.hasRunOnce = true;
            return;
        }

        switch (args[1]) {
            case "1.0" -> {
                for (File configFile : Objects.requireNonNull(new File(AdvancedDisplays.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "displays").listFiles())) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                    ConfigurationSection settingsSection = config.createSection("settings");

                    if (config.getString("block") != null) {
                        config.set("type", DisplayType.BLOCK.name());
                        settingsSection.set("block", config.getString("block"));
                        config.set("block", null);

                    } else if (config.getString("item") != null) {
                        config.set("type", DisplayType.ITEM.name());
                        settingsSection.set("item", config.getString("item"));
                        settingsSection.set("itemTransformation", config.getString("itemTransformation"));
                        config.set("item", null);
                        config.set("itemTransformation", null);

                    } else if (config.getString("text") != null) {
                        config.set("type", DisplayType.TEXT.name());
                        settingsSection.set("animationTime", 20);
                        settingsSection.set("refreshTime", 20);
                        settingsSection.set("text", List.of(config.getString("text")));
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
                    display.remove();
                    config.set("id", null);

                    try {
                        AdvancedDisplays.mainConfig.getConfig().set("text-update", 20);
                        AdvancedDisplays.mainConfig.save();
                        config.save(configFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case "1.1" -> {
                for (File configFile : Objects.requireNonNull(new File(AdvancedDisplays.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "displays").listFiles())) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                    if (DisplayType.valueOf(config.getString("type")) != DisplayType.TEXT) continue;

                    ConfigurationSection settingsSection = config.getConfigurationSection("settings");
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);
                    settingsSection.set("text", List.of(settingsSection.get("text")));

                    try {
                        config.save(configFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case "1.2" -> {
                for (File configFile : Objects.requireNonNull(new File(AdvancedDisplays.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "displays").listFiles())) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                    if (DisplayType.valueOf(config.getString("type")) != DisplayType.TEXT) continue;

                    ConfigurationSection settingsSection = config.getConfigurationSection("settings");
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);

                    try {
                        AdvancedDisplays.mainConfig.getConfig().set("text-update", null);
                        AdvancedDisplays.mainConfig.save();
                        config.save(configFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            default -> sender.sendMessage(MessagesManager.getColoredMessage("&b" + args[1] + " &cis not a valid version.", true));
        }

        AdvancedDisplays.needsConversion = false;
        this.hasRunOnce = false;
        AdvancedDisplays.reloadConfigs();
        sender.sendMessage(MessagesManager.getColoredMessage("&aThe displays have been successfully converted!", true));
    }
}
