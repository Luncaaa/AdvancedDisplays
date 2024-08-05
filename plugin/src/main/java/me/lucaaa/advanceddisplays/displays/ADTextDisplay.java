package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ADTextDisplay extends ADBaseDisplay implements DisplayMethods, me.lucaaa.advanceddisplays.api.displays.TextDisplay {
    private ConfigurationSection settings = null;
    private final AnimatedTextRunnable textRunnable;
    private int animationTime;
    private int refreshTime;
    private Map<String, Component> texts = new HashMap<>();
    private TextDisplay.TextAlignment alignment;
    private Color backgroundColor;
    private int lineWidth;
    private byte textOpacity;
    private boolean defaultBackground;
    private boolean seeThrough;
    private boolean shadowed;

    public ADTextDisplay(AdvancedDisplays plugin, ConfigManager configManager, String name, TextDisplay display, boolean isApi) {
        super(plugin, name, DisplayType.TEXT, configManager, display, isApi);
        this.settings = this.config.getConfigurationSection("settings");
        this.textRunnable = new AnimatedTextRunnable(plugin, this.displayId);

        if (this.settings != null) {
            this.animationTime = this.settings.getInt("animationTime");
            this.refreshTime = this.settings.getInt("refreshTime");

            ConfigurationSection textSection = this.settings.getConfigurationSection("texts");
            if (textSection == null) {
                Logger.log(Level.SEVERE, "The text display \"" + configManager.getFile().getName() + "\" does not have a valid \"text\" section! Check the wiki for more information.");
                texts.put("error", LegacyComponentSerializer.legacyAmpersand().deserialize("&cError! No valid \"texts\" section found. Check the wiki for more information."));
            } else {
                for (String sectionName : textSection.getKeys(false)) {
                    if (!textSection.isList(sectionName)) continue;

                    texts.put(sectionName, ComponentSerializer.deserialize(textSection.getStringList(sectionName)));
                }
            }
            this.textRunnable.start(this.texts, this.animationTime, this.refreshTime);

            this.alignment = TextDisplay.TextAlignment.valueOf(this.settings.getString("alignment"));

            String[] colorParts = Objects.requireNonNull(this.settings.getString("backgroundColor")).split(";");
            this.backgroundColor = Color.fromARGB(Integer.parseInt(colorParts[3]), Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));

            this.lineWidth = this.settings.getInt("lineWidth");
            this.textOpacity = (byte) this.settings.getInt("textOpacity");
            this.defaultBackground = this.settings.getBoolean("defaultBackground");
            this.seeThrough = this.settings.getBoolean("seeThrough");
            this.shadowed = this.settings.getBoolean("shadowed");
        }
    }

    public ADTextDisplay(AdvancedDisplays plugin, String name, TextDisplay display) {
        super(plugin, name, DisplayType.TEXT, display);
        this.textRunnable = new AnimatedTextRunnable(plugin, this.displayId);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        if (this.texts.size() == 1 && this.refreshTime <= 0) this.packets.setText(this.displayId, Utils.getColoredTextWithPlaceholders(player, ComponentSerializer.toJSON(this.texts.values().stream().toList().get(0))), player);
        this.packets.setBackgroundColor(this.displayId, this.backgroundColor, player);
        this.packets.setLineWidth(this.displayId, this.lineWidth, player);
        this.packets.setTextOpacity(this.displayId, this.textOpacity, player);
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    public ADTextDisplay create(Component text) {
        if (this.config != null) this.settings = this.config.createSection("settings");
        this.setRefreshTime(10);
        this.setAnimationTime(20);
        this.setSingleText("animation1", text);
        this.setAlignment(TextDisplay.TextAlignment.CENTER);
        this.setBackgroundColor(Color.ORANGE);
        this.setLineWidth(250);
        this.setTextOpacity((byte) -1);
        this.setUseDefaultBackground(true);
        this.setSeeThrough(true);
        this.setShadowed(true);
        return this;
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return this.alignment;
    }
    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        if (this.config != null) {
            this.settings.set("alignment", alignment.name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setAlignment(alignment, onlinePlayer);
        }
    }
    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, alignment, player);
    }

    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        if (this.config != null) {
            this.settings.set("backgroundColor", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";" + color.getAlpha());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setBackgroundColor(color, onlinePlayer);
        }
    }
    @Override
    public void setBackgroundColor(Color color, Player player) {
        this.packets.setBackgroundColor(this.displayId, color, player);
    }

    @Override
    public int getLineWidth() {
        return this.lineWidth;
    }
    @Override
    public void setLineWidth(int width) {
        this.lineWidth = width;
        if (this.config != null) {
            this.settings.set("lineWidth", width);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setLineWidth(width, onlinePlayer);
        }
    }
    @Override
    public void setLineWidth(int width, Player player) {
        this.packets.setLineWidth(this.displayId, width, player);
    }

    @Override
    public Map<String, Component> getText() {
        return this.texts;
    }
    @Override
    public void setAnimatedText(Map<String, Component> text) {
        this.texts.clear();
        this.texts = text;
        if (this.config != null) {
            ConfigurationSection textSection = this.settings.createSection("texts");
            for (Map.Entry<String, Component> entry : text.entrySet()) {
                textSection.set(entry.getKey(), MiniMessage.miniMessage().serialize(entry.getValue()).split(Pattern.quote("\n")));
            }
            this.settings.set("texts", textSection);
            this.save();
        }
        this.textRunnable.updateText(this.texts, this.animationTime, this.refreshTime);
    }
    @Override
    public void setSingleText(String identifier, Component text) {
        this.texts.clear();
        this.texts.put(identifier, text);
        if (this.config != null) {
            ConfigurationSection textSection = this.settings.createSection("texts");
            textSection.set(identifier, MiniMessage.miniMessage().serialize(text).split(Pattern.quote("\n")));
            this.save();
        }
        this.textRunnable.updateText(this.texts, this.animationTime, this.refreshTime);
    }

    @Override
    public boolean addText(String identifier, Component text) {
        if (this.texts.containsKey(identifier)) return false;
        this.texts.put(identifier, text);
        if (this.config != null) {
            ConfigurationSection textSection = this.settings.createSection("texts");
            for (Map.Entry<String, Component> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), MiniMessage.miniMessage().serialize(entry.getValue()).split(Pattern.quote("\n")));
            }
            this.settings.set("texts", textSection);
            this.save();
        }
        this.textRunnable.updateText(this.texts, this.animationTime, this.refreshTime);
        return true;
    }
    @Override
    public boolean removeText(String identifier) {
        if (!this.texts.containsKey(identifier)) return false;
        this.texts.remove(identifier);
        if (this.texts.isEmpty()) {
            this.addText("empty", LegacyComponentSerializer.legacyAmpersand().deserialize("&cThere are no texts to display"));
        }
        if (this.config != null) {
            ConfigurationSection textSection = this.settings.createSection("texts");
            for (Map.Entry<String, Component> entry : texts.entrySet()) {
                textSection.set(entry.getKey(), MiniMessage.miniMessage().serialize(entry.getValue()).split(Pattern.quote("\n")));
            }
            this.settings.set("texts", textSection);
            this.save();
        }
        this.textRunnable.updateText(this.texts, this.animationTime, this.refreshTime);
        return true;
    }

    @Override
    public byte getTextOpacity() {
        return this.textOpacity;
    }
    @Override
    public void setTextOpacity(byte opacity) {
        this.textOpacity = opacity;
        if (this.config != null) {
            this.settings.set("textOpacity", opacity);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setTextOpacity(opacity, onlinePlayer);
        }
    }
    @Override
    public void setTextOpacity(byte opacity, Player player) {
        this.packets.setTextOpacity(this.displayId, opacity, player);
    }

    @Override
    public boolean getUseDefaultBackground() {
        return this.defaultBackground;
    }
    @Override
    public void setUseDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        if (this.config != null) {
            this.settings.set("defaultBackground", defaultBackground);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setUseDefaultBackground(defaultBackground, onlinePlayer);
        }
    }
    @Override
    public void setUseDefaultBackground(boolean defaultBackground, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, defaultBackground, this.alignment, player);
    }

    @Override
    public boolean isSeeThrough() {
        return this.seeThrough;
    }
    @Override
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        if (this.config != null) {
            this.settings.set("seeThrough", seeThrough);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setSeeThrough(seeThrough, onlinePlayer);
        }
    }
    @Override
    public void setSeeThrough(boolean seeThrough, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, seeThrough, this.defaultBackground, this.alignment, player);
    }

    @Override
    public boolean isShadowed() {
        return this.shadowed;
    }
    @Override
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        if (this.config != null) {
            this.settings.set("shadowed", shadowed);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setShadowed(shadowed, onlinePlayer);
        }
    }
    @Override
    public void setShadowed(boolean shadowed, Player player) {
        this.packets.setProperties(this.displayId, shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    @Override
    public int getAnimationTime() {
        return this.animationTime;
    }
    @Override
    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
        if (this.config != null) {
            this.settings.set("animationTime", animationTime);
            this.save();
        }
        this.textRunnable.updateText(this.texts, animationTime, this.refreshTime);
    }

    @Override
    public int getRefreshTime() {
        return this.refreshTime;
    }
    @Override
    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
        if (this.config != null) {
            this.settings.set("refreshTime", refreshTime);
            this.save();
        }
        this.textRunnable.updateText(this.texts, this.animationTime, this.refreshTime);
    }

    public void stopRunnable() {
        this.textRunnable.stop();
    }
    public void restartRunnable() {
        this.textRunnable.stop();
        this.textRunnable.updateDisplayId(this.displayId);
        this.textRunnable.start(this.texts, this.animationTime, this.refreshTime);
    }
}