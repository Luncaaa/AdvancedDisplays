package me.lucaaa.advanceddisplays.actions.actionTypes;

public enum ActionType {
    MESSAGE("message"),
    CONSOLE_COMMAND("console-cmd"),
    PLAYER_COMMAND("player-cmd"),
    TITLE("title");

    private final String configName;
    ActionType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return this.configName;
    }

    public static ActionType getFromConfigName(String configName) {
        for (ActionType actionType : values()) {
            if (actionType.getConfigName().equalsIgnoreCase(configName)) {
                return actionType;
            }
        }
        return null;
    }
}
