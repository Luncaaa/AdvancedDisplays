package me.lucaaa.advanceddisplays.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Util class to convert a list of strings to components and vice versa.
 */
@SuppressWarnings("unused")
public class ComponentSerializer {
    /**
     * Transforms a string into a component. Every "\n" will be considered as a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The string to convert into a component.
     * @return The component.
     */
    public static Component deserialize(String text) {
        return deserialize(Arrays.stream(text.split(Pattern.quote("\n"))).toList());
    }

    /**
     * Transforms a list of strings into a component. Each element in the list will be considered a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The list of strings to convert into a component.
     * @return The component.
     */
    public static Component deserialize(List<String> text) {
        String message = String.join("\n", text);

        // From legacy and minimessage format to a component
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = MiniMessage.miniMessage().serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        return MiniMessage.miniMessage().deserialize(minimessage);
        // From Minimessage component to legacy string.
        // return BungeeComponentSerializer.get().serialize(component);
    }

    /**
     * Transforms a component into a list of strings with Minimessage format. Every "\n" will be considered as a new line.
     * @param component The component to convert into a list of strings.
     * @return The list of strings.
     */
    public static List<String> serialize(Component component) {
        return Arrays.stream(MiniMessage.miniMessage().serialize(component).split(Pattern.quote("\n"))).toList();
    }
}
