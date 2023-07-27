package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.AnimatedTextRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.List;
import java.util.Objects;

public class ADTextDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings = null;
    private final AnimatedTextRunnable textRunnable = new AnimatedTextRunnable(this.displayId);
    private int animationTime;
    private int refreshTime;
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
            this.animationTime = this.settings.getInt("animationTime");
            this.refreshTime = this.settings.getInt("refreshTime");

            this.text = this.settings.getStringList("text");
            this.textRunnable.start(this.text, this.animationTime, this.refreshTime);

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

    public ADTextDisplay(TextDisplay display) {
        super(DisplayType.TEXT, display);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        // The text runnable will set the text automatically if there are more than one texts.
        if (this.text.size() == 1 && this.refreshTime <= 0) this.packets.setText(this.displayId, this.text.get(0), player);
        this.packets.setBackgroundColor(this.displayId, this.backgroundColor, player);
        this.packets.setLineWidth(this.displayId, this.lineWidth, player);
        this.packets.setTextOpacity(this.displayId, this.textOpacity, player);
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    public ADTextDisplay create(List<String> text) {
        if (this.config != null) this.settings = this.config.createSection("settings");
        this.setRefreshTime(10);
        this.setAnimationTime(20);
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
        if (this.config != null) {
            this.settings.set("alignment", alignment.name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setAlignment(alignment, onlinePlayer);
        }
    }
    public void setAlignment(TextDisplay.TextAlignment alignment, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, this.defaultBackground, alignment, player);
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
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
    public void setBackgroundColor(Color color, Player player) {
        this.packets.setBackgroundColor(this.displayId, color, player);
    }

    public int getLineWidth() {
        return this.lineWidth;
    }
    public void setLineWidth(int width) {
        this.lineWidth = width;
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.settings.set("lineWidth", width);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setLineWidth(width, onlinePlayer);
        }
    }
    public void setLineWidth(int width, Player player) {
        this.packets.setLineWidth(this.displayId, width, player);
    }

    public List<String> getText() {
        return this.text;
    }
    public void setText(List<String> text) {
        this.text = text;
        if (this.config != null) {
            this.settings.set("text", text);
            this.save();
        }
        this.textRunnable.stop();
        this.textRunnable.start(text, this.animationTime, this.refreshTime);
    }
    public void addText(String text) {
        this.text.add(text);
        if (this.config != null) {
            List<String> textsList = this.settings.getStringList("text");
            textsList.add(text);
            this.settings.set("text", textsList);
            this.save();
        }
        if (!this.textRunnable.isRunning()) this.textRunnable.start(this.text, this.animationTime, this.refreshTime);
        else this.textRunnable.addText(text);
    }

    public byte getTextOpacity() {
        return this.textOpacity;
    }
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
    public void setTextOpacity(byte opacity, Player player) {
        this.packets.setTextOpacity(this.displayId, opacity, player);
    }

    public boolean getDefaultBackground() {
        return this.defaultBackground;
    }
    public void setDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        if (this.config != null) {
            this.settings.set("defaultBackground", defaultBackground);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setDefaultBackground(defaultBackground, onlinePlayer);
        }
    }
    public void setDefaultBackground(boolean defaultBackground, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, this.seeThrough, defaultBackground, this.alignment, player);
    }

    public boolean getSeeThrough() {
        return this.seeThrough;
    }
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
    public void setSeeThrough(boolean seeThrough, Player player) {
        this.packets.setProperties(this.displayId, this.shadowed, seeThrough, this.defaultBackground, this.alignment, player);
    }

    public boolean getShadowed() {
        return this.shadowed;
    }
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
    public void setShadowed(boolean shadowed, Player player) {
        this.packets.setProperties(this.displayId, shadowed, this.seeThrough, this.defaultBackground, this.alignment, player);
    }

    public int getAnimatedSeconds() {
        return this.animationTime;
    }
    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
        if (this.config != null) {
            this.settings.set("animationTime", animationTime);
            this.save();
        }
        this.textRunnable.stop();
        this.textRunnable.start(this.text, animationTime, this.refreshTime);
    }

    public int getRefreshTime() {
        return this.refreshTime;
    }
    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
        if (this.config != null) {
            this.settings.set("refreshTime", refreshTime);
            this.save();
        }
        this.textRunnable.stop();
        this.textRunnable.start(this.text, animationTime, this.refreshTime);
    }

    public void stopRunnable() {
        this.textRunnable.stop();
    }
}