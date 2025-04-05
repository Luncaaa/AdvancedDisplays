package me.lucaaa.advanceddisplays.common.utils;

import java.util.logging.Level;

public interface Logger {
    void log(Level level, String message);

    void logError(Level level, String message, Throwable error);
}