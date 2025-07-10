package me.lucaaa.advanceddisplays.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Util class to convert a list of strings to components and vice versa.
 */
@SuppressWarnings("unused")
public class ComponentSerializer {
    /**
     * Minimessage variable. Internal use only.
     * @hidden
     */
    @ApiStatus.Internal
    private static final MiniMessage mm = MiniMessage.miniMessage();

    /**
     * Legacy serializer variable. Internal use only.
     * @hidden
     */
    @ApiStatus.Internal
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().character('&').build();

    /**
     * Legacy serializer variable. Internal use only.
     * @hidden
     */
    @ApiStatus.Internal
    private static final LegacyComponentSerializer legacySectionSerializer = LegacyComponentSerializer.legacySection();

    /**
     * Gson serializer variable. Internal use only.
     * @hidden
     */
    @ApiStatus.Internal
    private static final GsonComponentSerializer gsonSerializer = GsonComponentSerializer.gson();

    /**
     * Transforms a string into a component. Every "\n" will be considered as a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The string to convert into a component.
     * @return The component.
     */
    public static Component deserialize(String text) {
        text = text.replace("\\n", "\n").replace('§', '&');
        // From legacy and minimessage format to a component
        Component legacy = legacySerializer.deserialize(text);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = mm.serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        return mm.deserialize(minimessage);
        // From Minimessage component to legacy string.
        // return BungeeComponentSerializer.get().serialize(component);
    }

    /**
     * Transforms a list of strings into a component. Each element in the list will be considered a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The list of strings to convert into a component.
     * @return The component.
     */
    public static Component deserialize(List<String> text) {
        return deserialize(String.join("\n", text));
    }

    /**
     * Transforms a component into a list of strings with Minimessage format. Every "\n" will be considered as a new line.
     * @param component The component to convert into a list of strings.
     * @return The list of strings.
     * @deprecated Should not be used in any case, so it's susceptible for removal.
     */
    @Deprecated
    public static List<String> serialize(Component component) {
        return Arrays.stream(mm.serialize(component).split(Pattern.quote("\n"))).toList();
    }

    /**
     * Returns the provided component as a legacy string (with the legacy symbol instead of '&').
     * @param component The component to transform into a legacy string.
     * @return The component transformed into a legacy string.
     */
    public static String getLegacyString(Component component) {
        return legacySectionSerializer.serialize(component);
    }

    /**
     * Transforms a component into a JSON string.
     * @param component The component to convert into a JSON string.
     * @return The JSON string.
     */
    public static String toJSON(Component component) {
        return gsonSerializer.serialize(component);
    }
}