package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;

public abstract class BountyChangedEvent extends GenericEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides access get the amount of bounty changed
     *
     * @return <code>Double</code> returns the amount of bounty modified
     * @since 2.1.1
     */
    abstract public double getBounty();

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
