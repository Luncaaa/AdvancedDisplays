package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ADTextDisplay extends BaseDisplay {
    private final TextDisplay display;

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
        this.display = display;

        if (this.config.getString("text") != null) {
            this.text = this.config.getString("text");
            this.display.setText(this.text);
        }

        if (this.config.getString("alignment") != null) {
            this.alignment = TextDisplay.TextAlignment.valueOf(this.config.getString("alignment"));
            this.display.setAlignment(this.alignment);
        }

        if (this.config.getString("backgroundColor") != null) {
            String[] colorParts = Objects.requireNonNull(this.config.getString("backgroundColor")).split(";");
            this.backgroundColor = Color.fromRGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));
            this.display.setBackgroundColor(this.backgroundColor);
        }

        this.lineWidth = this.config.getInt("lineWidth");
        this.display.setLineWidth(this.lineWidth);

        this.textOpacity = (byte) this.config.getInt("textOpacity");
        this.display.setTextOpacity(this.textOpacity);

        this.defaultBackground = this.config.getBoolean("defaultBackground");
        this.display.setDefaultBackground(this.defaultBackground);

        this.seeThrough = this.config.getBoolean("seeThrough");
        this.display.setSeeThrough(this.seeThrough);

        this.shadowed = this.config.getBoolean("shadowed");
        this.display.setShadowed(this.shadowed);
    }

    public ADTextDisplay create(String text) {
        this.setText(text);
        this.setAlignment(TextDisplay.TextAlignment.CENTER);
        this.setBackgroundColor(Color.fromRGB(0xFFAA00));
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
        this.config.set("alignment", alignment.name());
        this.display.setAlignment(alignment);
        this.save();
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.config.set("backgroundColor", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
        this.display.setBackgroundColor(color);
        this.save();
    }

    public int getLineWidth() {
        return this.lineWidth;
    }
    public void setLineWidth(int width) {
        this.lineWidth = width;
        this.config.set("lineWidth", width);
        this.display.setLineWidth(width);
        this.save();
    }

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
        this.config.set("text", text);
        this.display.setText(text.replace("\\n", "\n"));
        this.save();
    }

    public byte getTextOpacity() {
        return this.textOpacity;
    }
    public void setTextOpacity(byte opacity) {
        this.textOpacity = opacity;
        this.config.set("textOpacity", opacity);
        this.display.setTextOpacity(opacity);
        this.save();
    }

    public boolean getDefaultBackground() {
        return this.defaultBackground;
    }
    public void setDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        this.config.set("defaultBackground", defaultBackground);
        this.display.setDefaultBackground(defaultBackground);
        this.save();
    }

    public boolean getSeeThrough() {
        return this.seeThrough;
    }
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        this.config.set("seeThrough", seeThrough);
        this.display.setSeeThrough(seeThrough);
        this.save();
    }

    public boolean getShadowed() {
        return this.shadowed;
    }
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
        this.config.set("shadowed", shadowed);
        this.display.setShadowed(shadowed);
        this.save();
    }
}
