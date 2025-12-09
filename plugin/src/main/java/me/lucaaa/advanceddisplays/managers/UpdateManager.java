package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateManager {
    private final AdvancedDisplays plugin;
    private final int RESOURCE_ID = 110865;

    public UpdateManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        plugin.getTasksManager().runTaskAsynchronously(plugin, () -> {
            try {
                InputStream resourcePage = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID + "/~").openStream();
                Scanner scanner = new Scanner(resourcePage);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public static void sendStatus(AdvancedDisplays plugin, String spigotVersion, String pluginVersion) {
        String[] spigotVerDivided = spigotVersion.split("\\.");
        double spigotVerMajor = Double.parseDouble(spigotVerDivided[0] + "." + spigotVerDivided[1]);
        double spigotVerMinor = (spigotVerDivided.length > 2) ? Integer.parseInt(spigotVerDivided[2]) : 0;

        String[] pluginVerDivided = pluginVersion.split("\\.");
        double pluginVerMajor = Double.parseDouble(pluginVerDivided[0] + "." + pluginVerDivided[1]);
        double pluginVerMinor = (pluginVerDivided.length > 2) ? Integer.parseInt(pluginVerDivided[2]) : 0;

        if (spigotVerMajor == pluginVerMajor && spigotVerMinor == pluginVerMinor) {
            plugin.getServer().getConsoleSender().sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe plugin is up to date! &7(v" + pluginVersion + ")"));

        } else if (spigotVerMajor > pluginVerMajor || (spigotVerMajor == pluginVerMajor && spigotVerMinor > pluginVerMinor)) {
            plugin.getServer().getConsoleSender().sendMessage(plugin.getMessagesManager().getColoredMessage("&6There's a new update available on Spigot! &c" + pluginVersion + " &7-> &a" + spigotVersion));
            plugin.getServer().getConsoleSender().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Download it at &7https://www.spigotmc.org/resources/advanceddisplays.110865/"));

        } else {
            plugin.getServer().getConsoleSender().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Your plugin version is newer than the Spigot version! &a" + pluginVersion + " &7-> &c" + spigotVersion));
            plugin.getServer().getConsoleSender().sendMessage(plugin.getMessagesManager().getColoredMessage("&6There may be bugs and/or untested features!"));
        }
    }
}