package me.lucaaa.advanceddisplays.api.displays.enums;

/**
 * The visibility of a display's custom name.
 */
public enum NameVisibility {
    /**
     * The name will always be visible.
     */
    SHOWN,

    /**
     * The name will never be visible.
     */
    HIDDEN,

    /**
     * The name will only be visible if looked at.
     */
    IF_LOOKING
}