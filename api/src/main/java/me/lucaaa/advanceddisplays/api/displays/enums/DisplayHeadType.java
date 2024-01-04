package me.lucaaa.advanceddisplays.api.displays.enums;

/**
 * If the item display is a head, if can display a player skin or a custom texture.
 */
public enum DisplayHeadType {
    /**
     * The head will have the texture of a player's skin.
     */
    PLAYER("player"),
    /**
     * The head will have a custom texture.
     */
    BASE64("base64");

    // configName is the name that the setting will have in the config. Used to get a setting by the type of the displayed head.
    private final String configName;

    DisplayHeadType(String configName) {
        this.configName = configName;
    }

    /**
     * @hidden
     * Returns the name of the setting (key) in the config file.
     * @return The name of the setting (key) in the config file.
     */
    public String getConfigName() {
        return this.configName;
    }
}
