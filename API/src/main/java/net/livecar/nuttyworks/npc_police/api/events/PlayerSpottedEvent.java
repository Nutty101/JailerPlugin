package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class PlayerSpottedEvent extends GenericEvent {

    /**
     * This method provides access get the player the event was raised for.
     *
     * @return <code>Player</code> returns the player who triggered this event
     * @since 2.1.1
     */
    abstract public OfflinePlayer getPlayer();

    /**
     * This method provides access get the npc that witnessed the attack.
     *
     * @return <code>NPC</code> returns the Citizens NPC reference for the witnessing NPC
     * @since 2.1.1
     */
    abstract public NPC getWitnessNPC();

    /**
     * This method returns the the players PlayerManager for further interaction
     *
     * @return <code>PlayerManager</code> An object that allows interaction with the triggering player
     * @since 2.1.1
     */
    abstract public PlayerManager getPlayerManager();

}
