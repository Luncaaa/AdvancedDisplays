package me.lucaaa.advanceddisplays.api.displays.enums;

public enum DisplayHeadType {
    PLAYER("player"), BASE64("base64");

    // configName is the name that the setting will have in the config. Used to get a setting by the type of the displayed head.
    private final String configName;

    DisplayHeadType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return this.configName;
    }
}