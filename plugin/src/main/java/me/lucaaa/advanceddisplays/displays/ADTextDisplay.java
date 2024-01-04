package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.List;
import java.util.Objects;

public class ADTextDisplay extends ADBaseDisplay implements DisplayMethods, me.lucaaa.advanceddisplays.api.displays.TextDisplay {
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
    public List<String> getText() {
        return this.text;
    }
    @Override
    public void setText(List<String> text) {
        this.text = text;
        if (this.config != null) {
            this.settings.set("text", text);
            this.save();
        }
        this.textRunnable.stop();
        this.textRunnable.start(text, this.animationTime, this.refreshTime);
    }
    @Override
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
        this.textRunnable.stop();
        if (this.text != null) {
            this.textRunnable.start(this.text, animationTime, this.refreshTime);
        }
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
        this.textRunnable.stop();
        if (this.text != null) {
            this.textRunnable.start(this.text, animationTime, this.refreshTime);
        }
    }

    public void stopRunnable() {
        this.textRunnable.stop();
    }
    public void restartRunnable() {
        this.textRunnable.stop();
        this.textRunnable.updateDisplayId(this.displayId);
        this.textRunnable.start(this.text, this.animationTime, this.refreshTime);
    }
}