package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;

public abstract class WantedLevelChangedEvent extends CancellableEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides the new WANTED_LEVEL being applied to the player
     *
     * @return <code>WANTED_LEVEL</code> New Wanted level for the player
     * @since 2.1.1
     */
    abstract public WANTED_LEVEL getLevel();


    /**
     * Returns the reason for the change in wanted level
     *
     * @return <code>JAILED_BOUNTY</code> returns the reason for the wanted level change
     * @since 2.1.1
     */
    abstract public JAILED_BOUNTY getWantedReason();

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.1.1
     */
    abstract public PlayerManager getPlayerManager();

    /**
     * This method lets you know if the change was forced and cannot be cancelled.
     *
     * @return <code>boolean</code> True designates that you cannot cancel this event.
     * @since 2.2.1
     */
    abstract public boolean isForced();
}
