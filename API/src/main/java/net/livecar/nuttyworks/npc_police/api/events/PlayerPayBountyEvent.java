package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class PlayerPayBountyEvent extends CancellableEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides access get the player who is trying to pay the fine.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPayingPlayer();

    /**
     * This method provides access get the amount of bounty to be paid
     *
     * @return <code>Double</code> returns the amount of bounty modified
     * @since 2.1.1
     */
    abstract public double getBounty();

    /**
     * This method provides access to see if the the bounty was paid
     * by another plugin
     *
     * @return <code>Boolean</code> returns the amount of bounty modified
     * @since 2.1.1
     */
    abstract public boolean getBountyPaid();

    /**
     * This method provides access to set the bounty as paid
     *
     * @param paid set to true if your plugin has covered the payment
     * @since 2.1.1
     */
    abstract public void setBountyPaid(boolean paid);

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.1.1
     */
    abstract public PlayerManager getPlayerManager();

}
