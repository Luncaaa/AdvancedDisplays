package me.lucaaa.advanceddisplays.api.actions;

/**
 * Which button was used to click a display.
 */
public enum ClickType {
    /**
     * The player attacked (used the left button)
     */
    LEFT,
    /**
     * The player interacted at (used the right button)
     */
    RIGHT,
    /**
     * The player attacked while crouching (used the left button while holding shift)
     */
    SHIFT_LEFT,
    /**
     * The player interacted at while crouching (used the right button while holding shift)
     */
    SHIFT_RIGHT;

    /**
     * Transforms Bukkit's ClickType into the plugin's ClickType.
     * @param clickType Bukkit's ClickType.
     * @return The plugin's ClickType.
     */
    public static ClickType getFromBukkit(org.bukkit.event.inventory.ClickType clickType) {
        switch (clickType) {
            case RIGHT -> {
                return ClickType.RIGHT;
            }
            case LEFT -> {
                return ClickType.LEFT;
            }
            case SHIFT_RIGHT -> {
                return ClickType.SHIFT_RIGHT;
            }
            case SHIFT_LEFT -> {
                return ClickType.SHIFT_LEFT;
            }
            default -> {
                return null;
            }
        }
    }
}
