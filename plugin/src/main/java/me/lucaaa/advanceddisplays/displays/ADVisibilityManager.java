package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.api.displays.visibility.Visibility;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * The commented out code is from the old version of VisibilityManager.
 * Because {@link ADVisibilityManager#updateVisibility()} is now run every tick, the commented code is redundant.
 */
public class ADVisibilityManager implements VisibilityManager {
    private final ADBaseDisplay display;
    // String, not player, because players leaving a joining again are totally different objects.
    private final Map<String, Visibility> individualVis = new HashMap<>();
    private Visibility globalVisibility = Visibility.SHOW;
    private final Map<Player, Boolean> cachedVis = new HashMap<>();

    public ADVisibilityManager(ADBaseDisplay display) {
        this.display = display;
    }

    @Override
    public void setGlobalVisibility(Visibility visibility) {
        this.globalVisibility = visibility;
        //this.setGlobalVisibility(visibility, true);
    }

    /*@Override
    public void setGlobalVisibility(Visibility visibility, boolean modify) {
        this.globalVisibility = visibility;

        if (!modify) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (individualVis.containsKey(player.getName())) continue;
            updateIndividualVis(player, visibility);
        }
    }*/

    @Override
    public Visibility getGlobalVisibility() {
        return this.globalVisibility;
    }

    @Override
    public void setVisibility(Visibility visibility, Player player) {
        individualVis.put(player.getName(), visibility);

        //updateIndividualVis(player, visibility);
    }

    @Override
    public boolean isVisibleByPlayer(Player player) {
        boolean defaultVis = this.globalVisibility == Visibility.SHOW;
        boolean individualVis = this.individualVis.containsKey(player.getName()) && this.individualVis.get(player.getName()) == Visibility.SHOW;
        boolean permissionVis = display.getPermission().equalsIgnoreCase("none") || display.isPermissionInverted() != player.hasPermission(display.getPermission());
        boolean inRange = display.getViewDistance() <= 0.0 || player.getLocation().distanceSquared(display.getLocation()) <= display.getViewDistance();
        boolean inWorld = display.getLocation().getWorld() == player.getLocation().getWorld();

        return (defaultVis || individualVis) && permissionVis && inRange && inWorld;
    }

    @Override
    public void removeIndividualVisibility(Player player) {
        //updateIndividualVis(player, globalVisibility);
        this.individualVis.remove(player.getName());
    }

    @Override
    public void clearPlayerVisibilities() {
        /*for (String playerName : this.individualVis.keySet()) {
            Player player = Bukkit.getPlayerExact(playerName);
            this.updateIndividualVis(player, globalVisibility);
        }*/

        individualVis.clear();
    }

    /*private void updateIndividualVis(Player player, Visibility visibility) {
        switch (visibility) {
            case SHOW -> display.spawnToPlayer(player);
            case HIDE -> display.removeToPlayer(player);
        }
    }*/

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