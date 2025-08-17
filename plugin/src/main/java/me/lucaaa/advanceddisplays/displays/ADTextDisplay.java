package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.nms_common.Metadata;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ADTextDisplay extends ADBaseDisplay implements me.lucaaa.advanceddisplays.api.displays.TextDisplay {
    private final AnimatedTextRunnable textRunnable;
    private int animationTime;
    private int refreshTime;
    private Map<String, String> texts = new LinkedHashMap<>();
    private boolean isEmpty = false;
    private TextDisplay.TextAlignment alignment;
    private Color backgroundColor;
    private int lineWidth;
    private byte textOpacity;
    private boolean defaultBackground;
    private boolean seeThrough;
    private boolean shadowed;

    public ADTextDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name) {
        super(plugin, displaysManager, configManager, name, DisplayType.TEXT, EntityType.TEXT_DISPLAY);
        this.textRunnable = new AnimatedTextRunnable(plugin, this);

        if (settings != null) {
            this.animationTime = config.getOrDefault("animationTime", 20, settings);
            this.refreshTime = config.getOrDefault("refreshTime", 10, settings);

            ConfigurationSection textSection = config.getSection("texts", settings);
            if (textSection == null) {
                plugin.log(Level.WARNING, "The text display \"" + configManager.getFile().getName() + "\" does not have a valid \"texts\" section! Check the wiki for more information.");
                texts.put("error", "&cError! No valid \"texts\" section found. Check the wiki for more information.");
                isEmpty = true;
            } else if (textSection.getKeys(false).isEmpty()) {
                plugin.log(Level.WARNING, "The text display \"" + configManager.getFile().getName() + "\" has an empty \"texts\" section!");
                texts.put("empty", "&cThere are no texts to display");
                isEmpty = true;
            } else {
                for (String sectionName : textSection.getKeys(false)) {
                    if (!textSection.isList(sectionName)) continue;

                    texts.put(sectionName, String.join("\n", textSection.getStringList(sectionName)));
                }
            }

            this.lineWidth = config.getOrDefault("lineWidth", 250, settings);
            this.textOpacity = (byte) config.getOrDefault("textOpacity", -1, settings).intValue();
            this.defaultBackground = config.getOrDefault("defaultBackground", true, settings);
            this.seeThrough = config.getOrDefault("seeThrough", true, settings);
            this.shadowed = config.getOrDefault("shadowed", true, settings);

            try {
                this.alignment = TextDisplay.TextAlignment.valueOf(config.getOrDefault("alignment", TextDisplay.TextAlignment.CENTER.name(), settings));

                String[] colorParts = config.getOrDefault("backgroundColor", "255;170;0;255", settings).split(";");
                this.backgroundColor = Color.fromARGB(Integer.parseInt(colorParts[3]), Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));

            } catch (IllegalArgumentException e) {
                errors.add("Invalid text alignment type or invalid background color - make sure none of the color components are lower than 0 or higher than 255");
                return;
            }

            textRunnable.start();
        }
    }

    public ADTextDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, Location location, boolean saveToConfig) {
        super(plugin, displaysManager, name, location, DisplayType.TEXT, EntityType.TEXT_DISPLAY, saveToConfig);
        this.textRunnable = new AnimatedTextRunnable(plugin, this);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        textRunnable.sendToPlayer(player);
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.BG_COLOR, backgroundColor.asARGB()),
                new Metadata.DataPair<>(metadata.LINE_WIDTH, lineWidth),
                new Metadata.DataPair<>(metadata.TEXT_OPACITY, textOpacity),
                new Metadata.DataPair<>(metadata.TEXT_PROPERTIES, Metadata.getTextProperties(shadowed, seeThrough, defaultBackground, alignment))
        );
    }

    public ADTextDisplay create(String text) {
        create();
        setSingleText("animation1", text);
        setInitialValues();
        return this;
    }

    public ADTextDisplay create(Component text) {
        create();
        setSingleText("animation1", text);
        setInitialValues();
        return this;
    }

    private void setInitialValues() {
        setRefreshTime(10);
        setAnimationTime(20);
        setAlignment(TextDisplay.TextAlignment.CENTER);
        setBackgroundColor(Color.ORANGE);
        setLineWidth(250);
        setTextOpacity((byte) -1);
        setUseDefaultBackground(true);
        setSeeThrough(true);
        setShadowed(true);
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }
    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        if (config != null) {
            settings.set("alignment", alignment.name());
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setAlignment(alignment, onlinePlayer);
        }
    }
    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment, Player player) {
        packets.setMetadata(entityId, player, metadata.TEXT_PROPERTIES, Metadata.getTextProperties(shadowed, seeThrough, defaultBackground, alignment));
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    @Override
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        if (config != null) {
            settings.set("backgroundColor", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";" + color.getAlpha());
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setBackgroundColor(color, onlinePlayer);
        }
    }
    @Override
    public void setBackgroundColor(Color color, Player player) {
        packets.setMetadata(entityId, player, metadata.BG_COLOR, color.asARGB());
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }
    @Override
    public void setLineWidth(int width) {
        lineWidth = width;
        if (config != null) {
            settings.set("lineWidth", width);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setLineWidth(width, onlinePlayer);
        }
    }
    @Override
    public void setLineWidth(int width, Player player) {
        packets.setMetadata(entityId, player, metadata.LINE_WIDTH, width);
    }

    @Override
    public Map<String, String> getText() {
        return texts;
    }
    @Override
    public void setAnimatedText(Map<String, String> text) {
        texts.clear();
        texts = text;
        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            for (Map.Entry<String, String> entry : text.entrySet()) {
                textSection.set(entry.getKey(), entry.getValue().split(Pattern.quote("\n")));
            }
            settings.set("texts", textSection);
            save();
        }
        textRunnable.start();
    }

    @Override
    public void setAnimatedTextComponent(Map<String, Component> text) {
        Map<String, String> parsedTexts = new HashMap<>();
        for (Map.Entry<String, Component> entry : text.entrySet()) {
            parsedTexts.put(entry.getKey(), MiniMessage.miniMessage().serialize(entry.getValue()));
        }

        setAnimatedText(parsedTexts);
    }

    @Override
    public void setSingleText(String identifier, String text) {
        texts.clear();
        texts.put(identifier, text);
        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            textSection.set(identifier, text.split(Pattern.quote("\n")));
            save();
        }
        textRunnable.start();
    }

    @Override
    public boolean addText(String identifier, String text) {
        if (texts.containsKey(identifier)) return false;
        texts.put(identifier, text);

        if (isEmpty) {
            texts.remove("empty");
            isEmpty = false;
        }

        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), entry.getValue().split(Pattern.quote("\n")));
            }
            settings.set("texts", textSection);
            save();
        }
        textRunnable.start();
        return true;
    }

    @Override
    public boolean addText(String identifier, Component text) {
        return addText(identifier, MiniMessage.miniMessage().serialize(text));
    }

    @Override
    public boolean removeText(String identifier) {
        if (!texts.containsKey(identifier)) return false;
        texts.remove(identifier);
        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), entry.getValue().split(Pattern.quote("\n")));
            }
            settings.set("texts", textSection);
            save();
        }
        // Do not save this to the config
        if (texts.isEmpty()) {
            texts.put("empty", "&cThere are no texts to display");
            isEmpty = true;
        }
        textRunnable.start();
        return true;
    }

    @Override
    public void nextPage() {
        textRunnable.nextPage();
    }

    @Override
    public void nextPage(Player player) {
        textRunnable.excludePlayer(player);
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);
        int newIndex = playerData.getRunnable(plugin, this).nextPage();

        if (newIndex == textRunnable.getCurrentIndex()) {
            resetPlayer(player);
        }
    }

    @Override
    public void previousPage() {
        textRunnable.previousPage();
    }

    @Override
    public void previousPage(Player player) {
        textRunnable.excludePlayer(player);
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);
        int newIndex = playerData.getRunnable(plugin, this).previousPage();

        if (newIndex == textRunnable.getCurrentIndex()) {
            resetPlayer(player);
        }
    }

    @Override
    public void setPage(String page) {
        if (!texts.containsKey(page)) {
            throw new IllegalArgumentException("The display " + getName() + " does not have a page called " + page);
        }

        textRunnable.setPage(texts.keySet().stream().toList().indexOf(page));
    }

    @Override
    public void setPage(String page, Player player) {
        if (!texts.containsKey(page)) {
            throw new IllegalArgumentException("The display " + getName() + " does not have a page called " + page);
        }

        textRunnable.excludePlayer(player);
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);
        int newIndex = texts.keySet().stream().toList().indexOf(page);

        if (newIndex == textRunnable.getCurrentIndex()) {
            resetPlayer(player);
        } else {
            playerData.getRunnable(plugin, this).setPage(newIndex);
        }
    }

    @Override
    public void resetPlayer(Player player) {
        plugin.getPlayersManager().resetDisplay(player, this);
        textRunnable.resetPlayer(player);
    }

    @Override
    public byte getTextOpacity() {
        return textOpacity;
    }
    @Override
    public void setTextOpacity(byte opacity) {
        textOpacity = opacity;
        if (config != null) {
            settings.set("textOpacity", opacity);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setTextOpacity(opacity, onlinePlayer);
        }
    }
    @Override
    public void setTextOpacity(byte opacity, Player player) {
        packets.setMetadata(entityId, player, metadata.TEXT_OPACITY, opacity);
    }

    @Override
    public boolean getUseDefaultBackground() {
        return defaultBackground;
    }
    @Override
    public void setUseDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        if (config != null) {
            settings.set("defaultBackground", defaultBackground);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setUseDefaultBackground(defaultBackground, onlinePlayer);
        }
    }
    @Override
    public void setUseDefaultBackground(boolean defaultBackground, Player player) {
        packets.setMetadata(entityId, player, metadata.TEXT_PROPERTIES, Metadata.getTextProperties(shadowed, seeThrough, defaultBackground, alignment));
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }
    @Override
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        if (config != null) {
            settings.set("seeThrough", seeThrough);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setSeeThrough(seeThrough, onlinePlayer);
        }
    }
    @Override
    public void setSeeThrough(boolean seeThrough, Player player) {
        packets.setMetadata(entityId, player, metadata.TEXT_PROPERTIES, Metadata.getTextProperties(shadowed, seeThrough, defaultBackground, alignment));
    }

    @Override
    public boolean isShadowed() {
        return shadowed;
    }
    @Override
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        if (config != null) {
            settings.set("shadowed", shadowed);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setShadowed(shadowed, onlinePlayer);
        }
    }
    @Override
    public void setShadowed(boolean shadowed, Player player) {
        packets.setMetadata(entityId, player, metadata.TEXT_PROPERTIES, Metadata.getTextProperties(shadowed, seeThrough, defaultBackground, alignment));
    }

    @Override
    public int getAnimationTime() {
        return animationTime;
    }
    @Override
    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
        if (config != null) {
            settings.set("animationTime", animationTime);
            save();
        }
        textRunnable.start();
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }
    @Override
    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
        if (config != null) {
            settings.set("refreshTime", refreshTime);
            save();
        }
        textRunnable.start();
    }

    public AnimatedTextRunnable getTextRunnable() {
        return textRunnable;
    }

    public void stopRunnable() {
        textRunnable.stop();
    }

    public boolean isNotEmpty() {
        return !isEmpty;
    }

    public int getTextsNumber() {
        return (isEmpty) ? 0 : texts.size();
    }
}