package me.lucaaa.advanceddisplays.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Util class to convert a list of strings to components and vice versa.
 */
@SuppressWarnings("unused")
public interface ComponentSerializer {
    /**
     * Transforms a string into a component. Every "\n" will be considered as a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The string to convert into a component.
     * @return The component.
     */
    static Component deserialize(String text) {
        // From legacy and minimessage format to a component
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = MiniMessage.miniMessage().serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        return MiniMessage.miniMessage().deserialize(minimessage);
        // From Minimessage component to legacy string.
        // return BungeeComponentSerializer.get().serialize(component);
    }

    /**
     * Transforms a list of strings into a component. Each element in the list will be considered a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The list of strings to convert into a component.
     * @return The component.
     */
    static Component deserialize(List<String> text) {
        return deserialize(String.join("\n", text));
    }

    /**
     * Transforms a string into a Bungee component. Every "\n" will be considered as a new line.
     * Supports Minimessage format and legacy color codes.
     * @param text The string to convert into a component.
     * @return The component.
     */
    static BaseComponent[] toBaseComponent(String text) {
        return BungeeComponentSerializer.get().serialize(deserialize(text));
    }

    /**
     * Transforms a component into a list of strings with Minimessage format. Every "\n" will be considered as a new line.
     * @param component The component to convert into a list of strings.
     * @return The list of strings.
     */
    static List<String> serialize(Component component) {
        return Arrays.stream(MiniMessage.miniMessage().serialize(component).split(Pattern.quote("\n"))).toList();
    }

    /**
     * Transforms a component into a JSON string.
     * @param component The component to convert into a JSON string.
     * @return The JSON string.
     */
    static String toJSON(Component component) {
        return net.md_5.bungee.chat.ComponentSerializer.toString(BungeeComponentSerializer.get().serialize(component));
    }
}
