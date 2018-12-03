package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class TimeChangedEvent extends GenericEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides the amount of time being changed for the player
     *
     * @return <code>int</code> Amount of time being changed
     * @since 2.1.1
     */
    abstract public int getTime();

    /**
     * This method returns the reason for the bounty change.
     *
     * @return <code>JAILED_BOUNTY</code> Enum flag that defines the bounty change reason
     * @since 2.1.1
     */
    abstract public JAILED_BOUNTY getBountyReason();

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.1.1
     */
    abstract public PlayerManager getPlayerManager();

}
