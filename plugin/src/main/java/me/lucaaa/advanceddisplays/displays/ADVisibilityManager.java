package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.api.displays.visibility.Visibility;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ADVisibilityManager implements VisibilityManager {
    private final ADBaseDisplay display;
    // String, not player, because players leaving a joining again are totally different objects.
    private final Map<String, Visibility> individualVis = new HashMap<>();
    private Visibility globalVisibility = Visibility.SHOW;

    public ADVisibilityManager(ADBaseDisplay display) {
        this.display = display;
    }

    @Override
    public void setGlobalVisibility(Visibility visibility) {
        this.setGlobalVisibility(visibility, true);
    }

    @Override
    public void setGlobalVisibility(Visibility visibility, boolean modify) {
        this.globalVisibility = visibility;

        if (!modify) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (individualVis.containsKey(player.getName())) continue;
            updateIndividualVis(player, visibility);
        }
    }

    @Override
    public Visibility getGlobalVisibility() {
        return this.globalVisibility;
    }

    @Override
    public void setVisibility(Visibility visibility, Player player) {
        individualVis.put(player.getName(), visibility);

        updateIndividualVis(player, visibility);
    }

    @Override
    public boolean isVisibleByPlayer(Player player) {
        return (individualVis.containsKey(player.getName()))
                ? individualVis.get(player.getName()) == Visibility.SHOW
                : globalVisibility == Visibility.SHOW;
    }

    @Override
    public void removeIndividualVisibility(Player player) {
        updateIndividualVis(player, globalVisibility);
        this.individualVis.remove(player.getName());
    }

    @Override
    public void clearPlayerVisibilities() {
        for (String playerName : this.individualVis.keySet()) {
            Player player = Bukkit.getPlayerExact(playerName);
            this.updateIndividualVis(player, globalVisibility);
        }

        individualVis.clear();
    }

    private void updateIndividualVis(Player player, Visibility visibility) {
        switch (visibility) {
            case SHOW -> display.spawnToPlayer(player);
            case HIDE -> display.removeToPlayer(player);
        }
    }
}
