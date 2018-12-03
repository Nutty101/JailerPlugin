package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;

public abstract class PlayerEscapedEvent extends GenericEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.2.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides access get the name of the jail the player escaped from.
     *
     * @return <code>String</code> Returns the short name of the jail the player escaped from
     * @since 2.2.1
     */
    abstract public String getEscapedJail();

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.2.1
     */
    abstract public PlayerManager getPlayerManager();
}
