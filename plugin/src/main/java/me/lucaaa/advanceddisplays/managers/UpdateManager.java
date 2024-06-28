package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.Bukkit;

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
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                InputStream resourcePage = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.RESOURCE_ID + "/~").openStream();
                Scanner scanner = new Scanner(resourcePage);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}