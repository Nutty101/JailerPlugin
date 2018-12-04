package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class StatusChangedEvent extends GenericEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides access to preview the status change being requested
     *
     * @return <code>CURRENT_STATUS</code> returns the new status being requested
     * @since 2.1.1
     */
    abstract public CURRENT_STATUS getStatus();

    /**
     * This method provides access to preview the reason for the status change being requested
     *
     * @return <code>WANTED_REASONS</code> returns the reason for the status change
     * @since 2.1.1
     */
    abstract public WANTED_REASONS getReason();

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.1.1
     */
    abstract public PlayerManager getPlayerManager();

}
