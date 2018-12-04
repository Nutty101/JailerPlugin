package net.livecar.nuttyworks.npc_police.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GenericEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
