package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.actions.ClickType;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.nms_common.InternalEntityClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

@SuppressWarnings("unused")
public class InternalEntityClickListener implements Listener {
    private final HashMap<Player, Long> pastInteractions = new HashMap<>();

    @EventHandler
    public void onEntityClick(InternalEntityClickEvent event) {
        Player player = event.getPlayer();
        ClickType clickType = ClickType.getFromBukkit(event.getClickType());

        // Because the event is fired twice, the current time is stored in a map along with the player that interacted with the display.
        // When the event is called again, the current time and the one stored in the map are compared. If less than or 20ms have passed, ignore this event.
        if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
            if (this.pastInteractions.containsKey(player)) {
                long now = System.currentTimeMillis();
                if (now - this.pastInteractions.get(player) <= 20) {
                    this.pastInteractions.remove(player);
                    return;
                }
            } else {
                this.pastInteractions.put(player, System.currentTimeMillis());
            }
        }

        ADBaseDisplay display = AdvancedDisplays.interactionsManager.getDisplay(event.getInteractionId());
        if (display != null) display.runActions(player,clickType);
    }
}
