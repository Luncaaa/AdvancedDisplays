package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.visibility.Visibility;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import me.lucaaa.advanceddisplays.conditions.ConditionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ADVisibilityManager implements VisibilityManager {
    private final ADBaseDisplay display;
    private final ConditionsHandler conditionsHandler;
    // String, not player, because players leaving a joining again are totally different objects.
    private final Map<String, Visibility> individualVis = new HashMap<>();
    private Visibility globalVisibility = Visibility.SHOW;
    private final Map<Player, Boolean> cachedVis = new HashMap<>();

    public ADVisibilityManager(AdvancedDisplays plugin, ADBaseDisplay display) {
        this.display = display;
        this.conditionsHandler = new ConditionsHandler(plugin, display, display.getConfigManager().getConfig().getConfigurationSection("view-conditions"));
    }

    @Override
    public void setGlobalVisibility(Visibility visibility) {
        globalVisibility = visibility;
    }

    @Override
    public Visibility getGlobalVisibility() {
        return globalVisibility;
    }

    @Override
    public void setVisibility(Visibility visibility, Player player) {
        individualVis.put(player.getName(), visibility);
    }

    @Override
    public boolean isVisibleByPlayer(Player player) {
        if (!player.getWorld().equals(display.getLocation().getWorld())) return false;

        boolean def = globalVisibility == Visibility.SHOW;
        boolean individual = individualVis.containsKey(player.getName()) && individualVis.get(player.getName()) == Visibility.SHOW;
        boolean meetsConditions = conditionsHandler.checkConditions(player);

        return (def || individual) && meetsConditions;
    }

    @Override
    public void removeIndividualVisibility(Player player) {
        individualVis.remove(player.getName());
    }

    @Override
    public void clearPlayerVisibilities() {
        individualVis.clear();
    }

    public void updateVisibility() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            boolean isSeen = isVisibleByPlayer(onlinePlayer);
            Boolean cached = cachedVis.get(onlinePlayer);

            if (cached == null) {
                if (isSeen) {
                    display.spawnToPlayer(onlinePlayer);
                } else {
                    display.removeToPlayer(onlinePlayer);
                }
                cachedVis.put(onlinePlayer, isSeen);

            } else if (cached && !isSeen) {
                display.removeToPlayer(onlinePlayer);
                cachedVis.put(onlinePlayer, false);

            } else if (!cached && isSeen) {
                display.spawnToPlayer(onlinePlayer);
                cachedVis.put(onlinePlayer, true);
            }
        }

        // Removes a cached player if he goes offline.
        cachedVis.keySet().removeIf(player -> !player.isOnline());
    }
}