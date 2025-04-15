package me.lucaaa.advanceddisplays.conditions.conditionTypes;

public enum ConditionType {
    DISTANCE("distance", Double.class),
    // Player must have the permission
    HAS_PERMISSION("has-permission", String.class),
    // Player must NOT have the permission
    LACKS_PERMISSION("lacks-permission", String.class);

    private final String configName;
    private final Class<?> type;

    ConditionType(String configName, Class<?> type) {
        this.configName = configName;
        this.type = type;
    }

    public String getConfigName() {
        return configName;
    }

    public Class<?> getType() {
        return type;
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