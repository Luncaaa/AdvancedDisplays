package me.lucaaa.advanceddisplays.common.managers;

import me.lucaaa.advanceddisplays.common.utils.Logger;

import java.util.logging.Level;

public class ConversionManager {
    private static boolean conversionNeeded;

    public static void setConversionNeeded(boolean needsConversion) {
        if (needsConversion) {
            Logger.log(Level.WARNING, "The displays configuration files are from an older version and have been changed in newer versions.");
            Logger.log(Level.WARNING, "Run the command \"/ad convert [previous version]\" in-game to update the configuration files to newer versions.");
            Logger.log(Level.WARNING, "Not converting the configurations will cause commands to malfunction. See more information at lucaaa.gitbook.io/advanceddisplays/usage/commands-and-permissions/convert-subcommand");
        }
        conversionNeeded = needsConversion;
    }

    public static boolean isConversionNeeded() {
        return conversionNeeded;
    }
}
