package me.lucaaa.advanceddisplays.common;

public enum DisplayHeadType {
    NONE(null), PLAYER("player"), BASE64("base64");

    private final String configName;

    DisplayHeadType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return this.configName;
    }
}
