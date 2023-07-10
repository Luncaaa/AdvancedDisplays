package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.Objects;

public class ADTextDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings;
    private String text;
    private TextDisplay.TextAlignment alignment;
    private Color backgroundColor;
    private int lineWidth;
    private byte textOpacity;
    private boolean defaultBackground;
    private boolean seeThrough;
    private boolean shadowed;

    public ADTextDisplay(ConfigManager configManager, TextDisplay display) {
        super(DisplayType.TEXT, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            this.text = this.settings.getString("text");
            this.alignment = TextDisplay.TextAlignment.valueOf(this.settings.getString("alignment"));

            String[] colorParts = Objects.requireNonNull(this.settings.getString("backgroundColor")).split(";");
            this.backgroundColor = Color.fromARGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]), Integer.parseInt(colorParts[3]));

            this.lineWidth = this.settings.getInt("lineWidth");
            this.textOpacity = (byte) this.settings.getInt("textOpacity");
            this.defaultBackground = this.settings.getBoolean("defaultBackground");
            this.seeThrough = this.settings.getBoolean("seeThrough");
            this.shadowed = this.settings.getBoolean("shadowed");
        }
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        this.packets.setText(this.displayId, this.text, player);
        this.packets.setBackgroundColor(this.displayId, this.backgroundColor, player);
        this.packets.setLineWidth(this.displayId, this.lineWidth, player);
        this.packets.setTextOpacity(this.displayId, this.textOpacity, player);
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    public ADTextDisplay create(String text) {
        this.settings = this.config.createSection("settings");
        this.setText(text);
        this.setAlignment(TextDisplay.TextAlignment.CENTER);
        this.setBackgroundColor(Color.fromARGB(0xFFFFAA00));
        this.setLineWidth(250);
        this.setTextOpacity((byte) -1);
        this.setDefaultBackground(true);
        this.setSeeThrough(true);
        this.setShadowed(true);

        return this;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return this.alignment;
    }
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        this.settings.set("alignment", alignment.name());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, alignment, onlinePlayer);
        }
        this.save();
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.settings.set("backgroundColor", color.getAlpha() + ";" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setBackgroundColor(this.displayId, color, onlinePlayer);
        }
        this.save();
    }

    public int getLineWidth() {
        return this.lineWidth;
    }
    public void setLineWidth(int width) {
        this.lineWidth = width;
        this.settings.set("lineWidth", width);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setLineWidth(this.displayId, width, onlinePlayer);
        }
        this.save();
    }

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
        this.settings.set("text", text);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setText(this.displayId, text, onlinePlayer);
        }
        this.save();
    }

    public byte getTextOpacity() {
        return this.textOpacity;
    }
    public void setTextOpacity(byte opacity) {
        this.textOpacity = opacity;
        this.settings.set("textOpacity", opacity);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setTextOpacity(this.displayId, opacity, onlinePlayer);
        }
        this.save();
    }

    public boolean getDefaultBackground() {
        return this.defaultBackground;
    }
    public void setDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        this.settings.set("defaultBackground", defaultBackground);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, defaultBackground, this.alignment, onlinePlayer);
        }
        this.save();
    }

    public boolean getSeeThrough() {
        return this.seeThrough;
    }
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        this.settings.set("seeThrough", seeThrough);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setProperties(this.displayId, this.shadowed, seeThrough, this.defaultBackground, this.alignment, onlinePlayer);
        }
        this.save();
    }

    public boolean getShadowed() {
        return this.shadowed;
    }
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        this.settings.set("shadowed", shadowed);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setProperties(this.displayId, shadowed, this.seeThrough, this.defaultBackground, this.alignment, onlinePlayer);
        }
        this.save();
    }
}