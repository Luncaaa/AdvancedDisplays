package me.lucaaa.advanceddisplays.conditions.conditionTypes;

public enum ConditionType {
    DISTANCE("distance"),
    // Player must have the permission
    HAS_PERMISSION("has-permission"),
    // Player must NOT have the permission
    LACKS_PERMISSION("lacks-permission");

    private final String configName;
    ConditionType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    public static ConditionType getFromConfigName(String configName) {
        for (ConditionType conditionType : values()) {
            if (conditionType.getConfigName().equalsIgnoreCase(configName)) {
                return conditionType;
            }
        }
        return null;
    }
}