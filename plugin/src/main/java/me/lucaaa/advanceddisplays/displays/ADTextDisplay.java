package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.AnimatedTextRunnable;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.List;
import java.util.Objects;

public class ADTextDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings;
    private final AnimatedTextRunnable textRunnable = new AnimatedTextRunnable(this.displayId);
    private List<String> text;
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
            this.text = this.settings.getStringList("text");
            if (this.text.size() > 1) this.textRunnable.start(this.text);

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

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        // The text runnable will set the text automatically if there are more than one texts.
        if (this.text.size() == 1) this.packets.setText(this.displayId, this.text.get(0), player);
        this.packets.setBackgroundColor(this.displayId, this.backgroundColor, player);
        this.packets.setLineWidth(this.displayId, this.lineWidth, player);
        this.packets.setTextOpacity(this.displayId, this.textOpacity, player);
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    public ADTextDisplay create(List<String> text) {
        this.settings = this.config.createSection("settings");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setText(text, onlinePlayer);
            this.setAlignment(TextDisplay.TextAlignment.CENTER, onlinePlayer);
            this.setBackgroundColor(Color.fromARGB(0xFFFFAA00), onlinePlayer);
            this.setLineWidth(250, onlinePlayer);
            this.setTextOpacity((byte) -1, onlinePlayer);
            this.setDefaultBackground(true, onlinePlayer);
            this.setSeeThrough(true, onlinePlayer);
            this.setShadowed(true, onlinePlayer);
        }
        return this;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return this.alignment;
    }
    public void setAlignment(TextDisplay.TextAlignment alignment, Player player) {
        this.alignment = alignment;
        this.settings.set("alignment", alignment.name());
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, alignment, player);
        this.save();
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    public void setBackgroundColor(Color color, Player player) {
        this.backgroundColor = color;
        this.settings.set("backgroundColor", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";" + color.getAlpha());
        this.packets.setBackgroundColor(this.displayId, color, player);
        this.save();
    }

    public int getLineWidth() {
        return this.lineWidth;
    }
    public void setLineWidth(int width, Player player) {
        this.lineWidth = width;
        this.settings.set("lineWidth", width);
        this.packets.setLineWidth(this.displayId, width, player);
        this.save();
    }

    public List<String> getText() {
        return this.text;
    }
    public void setText(List<String> text, Player player) {
        this.text = text;
        this.settings.set("text", text);
        if (text.size() == 1) {
            this.packets.setText(this.displayId, text.get(0), player);
        } else {
            this.textRunnable.stop();
            this.textRunnable.start(text);
        }
        this.save();
    }

    public byte getTextOpacity() {
        return this.textOpacity;
    }
    public void setTextOpacity(byte opacity, Player player) {
        this.textOpacity = opacity;
        this.settings.set("textOpacity", opacity);
        this.packets.setTextOpacity(this.displayId, opacity, player);
        this.save();
    }

    public boolean getDefaultBackground() {
        return this.defaultBackground;
    }
    public void setDefaultBackground(boolean defaultBackground, Player player) {
        this.defaultBackground = defaultBackground;
        this.settings.set("defaultBackground", defaultBackground);
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, defaultBackground, this.alignment, player);
        this.save();
    }

    public boolean getSeeThrough() {
        return this.seeThrough;
    }
    public void setSeeThrough(boolean seeThrough, Player player) {
        this.seeThrough = seeThrough;
        this.settings.set("seeThrough", seeThrough);
        this.packets.setProperties(this.displayId, this.shadowed, seeThrough, this.defaultBackground, this.alignment, player);
        this.save();
    }

    public boolean getShadowed() {
        return this.shadowed;
    }
    public void setShadowed(boolean shadowed, Player player) {
        this.shadowed = shadowed;
        this.settings.set("shadowed", shadowed);
        this.packets.setProperties(this.displayId, shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
        this.save();
    }

    public void stopRunnable() {
        this.textRunnable.stop();
    }
}