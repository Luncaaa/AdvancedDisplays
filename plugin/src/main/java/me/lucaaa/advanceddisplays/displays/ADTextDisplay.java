package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
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
    private TextDisplay.TextAlignment alignment;
    private Color backgroundColor;
    private int lineWidth;
    private byte textOpacity;
    private boolean defaultBackground;
    private boolean seeThrough;
    private boolean shadowed;

    public ADTextDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name, TextDisplay display) {
        super(plugin, displaysManager, configManager, name, DisplayType.TEXT, display);
        this.textRunnable = new AnimatedTextRunnable(plugin, entityId);

        if (settings != null) {
            this.animationTime = config.getOrDefault("animationTime", 20, settings);
            this.refreshTime = config.getOrDefault("refreshTime", 10, settings);

            ConfigurationSection textSection = config.getSection("texts", settings);
            if (textSection == null || textSection.getKeys(false).isEmpty()) {
                plugin.log(Level.SEVERE, "The text display \"" + configManager.getFile().getName() + "\" does not have a valid \"text\" section! Check the wiki for more information.");
                texts.put("error", "&cError! No valid \"texts\" section found. Check the wiki for more information.");
            } else {
                for (String sectionName : textSection.getKeys(false)) {
                    if (!textSection.isList(sectionName)) continue;

                    texts.put(sectionName, String.join("\n", textSection.getStringList(sectionName)));
                }
            }
            textRunnable.start(texts, animationTime, refreshTime);

            this.alignment = TextDisplay.TextAlignment.valueOf(config.getOrDefault("alignment", TextDisplay.TextAlignment.CENTER.name(), settings));

            String[] colorParts = config.getOrDefault("backgroundColor", "255;170;0;255", settings).split(";");
            this.backgroundColor = Color.fromARGB(Integer.parseInt(colorParts[3]), Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));

            this.lineWidth = config.getOrDefault("lineWidth", 250, settings);
            this.textOpacity = (byte) config.getOrDefault("textOpacity", -1, settings).intValue();
            this.defaultBackground = config.getOrDefault("defaultBackground", true, settings);
            this.seeThrough = config.getOrDefault("seeThrough", true, settings);
            this.shadowed = config.getOrDefault("shadowed", true, settings);
        }
    }

    public ADTextDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, TextDisplay display, boolean saveToConfig) {
        super(plugin, displaysManager, name, DisplayType.TEXT, display, saveToConfig);
        this.textRunnable = new AnimatedTextRunnable(plugin, entityId);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        textRunnable.sendToPlayer(player, packets);
        packets.setBackgroundColor(entityId, backgroundColor, player);
        packets.setLineWidth(entityId, lineWidth, player);
        packets.setTextOpacity(entityId, textOpacity, player);
        packets.setProperties(entityId, shadowed, seeThrough, defaultBackground, alignment, player);
    }

    public ADTextDisplay create(String text) {
        setSingleText("animation1", text);
        setInitialValues();
        return this;
    }

    public ADTextDisplay create(Component text) {
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setAlignment(alignment, onlinePlayer);
        }
    }
    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment, Player player) {
        packets.setProperties(entityId, shadowed, seeThrough, defaultBackground, alignment, player);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBackgroundColor(color, onlinePlayer);
        }
    }
    @Override
    public void setBackgroundColor(Color color, Player player) {
        packets.setBackgroundColor(entityId, color, player);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setLineWidth(width, onlinePlayer);
        }
    }
    @Override
    public void setLineWidth(int width, Player player) {
        packets.setLineWidth(entityId, width, player);
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
        textRunnable.start(texts, animationTime, refreshTime);
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
        textRunnable.start(texts, animationTime, refreshTime);
    }

    @Override
    public void setSingleText(String identifier, Component text) {
        setSingleText(identifier, MiniMessage.miniMessage().serialize(text));
    }

    @Override
    public boolean addText(String identifier, String text) {
        if (texts.containsKey(identifier)) return false;
        texts.put(identifier, text);
        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), entry.getValue().split(Pattern.quote("\n")));
            }
            settings.set("texts", textSection);
            save();
        }
        textRunnable.start(texts, animationTime, refreshTime);
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
        if (texts.isEmpty()) {
            addText("empty", "&cThere are no texts to display");
        }
        if (config != null) {
            ConfigurationSection textSection = settings.createSection("texts");
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), entry.getValue().split(Pattern.quote("\n")));
            }
            settings.set("texts", textSection);
            save();
        }
        textRunnable.start(texts, animationTime, refreshTime);
        return true;
    }

    @Override
    public void nextPage() {
        textRunnable.nextPage();
    }

    @Override
    public void previousPage() {
        textRunnable.previousPage();
    }

    @Override
    public void setPage(String page) {
        if (!texts.containsKey(page)) {
            throw new IllegalArgumentException("The display " + getName() + " does not have a page called " + page);
        }

        textRunnable.setPage(page);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setTextOpacity(opacity, onlinePlayer);
        }
    }
    @Override
    public void setTextOpacity(byte opacity, Player player) {
        packets.setTextOpacity(entityId, opacity, player);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setUseDefaultBackground(defaultBackground, onlinePlayer);
        }
    }
    @Override
    public void setUseDefaultBackground(boolean defaultBackground, Player player) {
        packets.setProperties(entityId, shadowed, seeThrough, defaultBackground, alignment, player);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setSeeThrough(seeThrough, onlinePlayer);
        }
    }
    @Override
    public void setSeeThrough(boolean seeThrough, Player player) {
        packets.setProperties(entityId, shadowed, seeThrough, defaultBackground, alignment, player);
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setShadowed(shadowed, onlinePlayer);
        }
    }
    @Override
    public void setShadowed(boolean shadowed, Player player) {
        packets.setProperties(entityId, shadowed, seeThrough, defaultBackground, alignment, player);
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
        textRunnable.start(texts, animationTime, refreshTime);
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
        textRunnable.start(texts, animationTime, refreshTime);
    }

    public void stopRunnable() {
        textRunnable.stop();
    }
    public void restartRunnable() {
        textRunnable.stop();
        textRunnable.updateDisplayId(entityId);
        textRunnable.start(texts, animationTime, refreshTime);
    }
}