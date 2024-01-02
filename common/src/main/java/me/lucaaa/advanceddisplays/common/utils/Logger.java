package me.lucaaa.advanceddisplays.common.utils;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {
    public static void log(Level level, String message) {
        Bukkit.getLogger().log(level, "[AdvancedDisplays] " + message);
    }

    public static void logError(Level level, String message, Throwable error) {
        Bukkit.getLogger().log(level, "[AdvancedDisplays] " + message, error);
    }
}