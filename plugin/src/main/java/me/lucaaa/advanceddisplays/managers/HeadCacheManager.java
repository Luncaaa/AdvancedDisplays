package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.ItemDisplay;
import me.lucaaa.advanceddisplays.data.DisplayHeadType;
import me.lucaaa.advanceddisplays.data.HeadUtils;
import me.lucaaa.advanceddisplays.displays.ADItemDisplay;
import me.lucaaa.advancedlinks.common.ITask;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HeadCacheManager {
    private final AdvancedDisplays plugin;
    private final Map<ItemDisplay, ITask> tasks = new HashMap<>();

    public final ItemStack CANCEL = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");
    public final ItemStack DONE = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=");
    public final ItemStack LOADING = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVlMTc0ZjQxZTU5NGU2NGVhMzE0MWMwN2RhZjdhY2YxZmEwNDVjMjMwYjJiMGIwZmIzZGExNjNkYjIyZjQ1NSJ9fX0=");
    public final ItemStack ADD = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
    public final ItemStack LEFT = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ==");
    public final ItemStack RIGHT = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ==");

    public HeadCacheManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public void loadHead(ADItemDisplay display, DisplayHeadType type, String value) {
        ITask task = plugin.getTasksManager().runTaskAsynchronously(plugin, () -> {
            ItemStack head;
            if (type == DisplayHeadType.PLAYER) {
                head = HeadUtils.getPlayerHead(value, plugin);
            } else {
                head = HeadUtils.getBase64Head(value, plugin);
            }

            display.setHead(head);
            tasks.remove(display);
        });

        tasks.put(display, task);
    }

    public void cancelTask(ItemDisplay display) {
        if (tasks.containsKey(display)) tasks.get(display).cancel();
        tasks.remove(display);
    }

    public void shutdown() {
        for (ITask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }

    private ItemStack getHead(String base64) {
        return HeadUtils.getBase64Head(
                base64,
                plugin
        );
    }
}