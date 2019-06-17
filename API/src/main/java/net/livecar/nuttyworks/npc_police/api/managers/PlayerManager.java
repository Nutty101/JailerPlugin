package net.livecar.nuttyworks.npc_police.api.managers;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.api.Enumerations;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;

import java.util.Date;

/**
 * PlayerManager is a class that provides the ability to read, or modify many of
 * the players settings inside of NPCPolice.
 *
 * @author Sir_Nutty (Bruce Johnson)
 * @since 2.1.1
 */
public abstract class PlayerManager {

    /**
     * This method provides access to the players current wanted status.
     *
     * @return <code>CURRENT_STATUS</code> returns an enumeration flag that
     * defined the users current status.
     * @since 2.1.1
     */
    abstract public CURRENT_STATUS getCurrentStatus();

    /**
     * This method provides access to set the players current wanted status.
     *
     * @param newStatus Set the players current wanted status
     * @since 2.1.1
     */

    abstract public void setCurrentStatus(CURRENT_STATUS newStatus);

    /**
     * This method provides access to the players prior wanted status.
     *
     * @return <code>CURRENT_STATUS</code> returns an enumeration flag that
     * defined the users prior status.
     * @since 2.1.1
     */

    abstract public CURRENT_STATUS getPriorStatus();

    /**
     * This method provides the users current bounty value.
     *
     * @return <code>Double</code> returns the current value of the users bounty
     * level.
     * @since 2.1.1
     */

    abstract public Double getCurrentBounty();

    /**
     * This method provides access to increase or decrease the users current
     * bounty level<br>
     * <strong>Use negative values to decrease the users bounty</strong>
     *
     * @param newBounty Set the players current bounty level
     * @since 2.1.1
     */
    abstract public void changeBounty(Double newBounty);

    /**
     * This method provides the ability to set the users bounty to 0<br>
     *
     * @since 2.1.1
     */

    abstract public void clearBounty();

    /**
     * This method provides access to increase or decrease the users current jail
     * time (on top of bounty)<br>
     * <strong>Use negative values to decrease the users time</strong>
     *
     * @param seconds Set the players current bounty level
     * @since 2.1.1
     */
    abstract public void changeTime(int seconds);

    /**
     * This method provides access to increase or decrease the users current jail
     * time (on top of bounty)<br>
     * <strong>Use negative values to decrease the users time</strong>
     *
     * @return <code>Integer</code> returns the current value of the users time
     * value
     * @since 2.1.1
     */
    abstract public int getTime();

    /**
     * This method provides access to set the users current jail time (on top of
     * bounty)<br>
     * <strong>This sets the value to a specific value</strong>
     *
     * @param seconds Set the players current bounty level
     * @since 2.1.1
     */
    abstract public void setTime(int seconds);

    /**
     * This method provides the ability to clear the users wanted level<br>
     *
     * @since 2.1.1
     */
    abstract public void clearWanted();

    /**
     * This method provides the ability to add a new item to the users wanted
     * reasons<br>
     *
     * @param wantedInfo Pass a wanted_information object to append to the users wanted
     *                   reasons
     * @since 2.1.1
     */
    abstract public void addWantedReason(Wanted_Information wantedInfo);

    /**
     * This method provides access to the users last date/time being arrested<br>
     *
     * @return <code>Date</code> returns a date object referencing the last
     * system date that the user was arrested
     * @since 2.1.1
     */
    abstract public Date getLastArrest();

    /**
     * This method provides access to the users last date/time they escaped<br>
     *
     * @return <code>Date</code> returns a date object referencing the last
     * system date that the user escaped
     * @since 2.1.1
     */
    abstract public Date getLastEscape();

    /**
     * This method will clear a users locked up inventory. This is items that
     * were confiscated at the time of arrest.
     *
     * @since 2.1.1
     */
    abstract public void clearLockedInventory();

    /**
     * This method will return a users locked up inventory. This is items that
     * were confiscated at the time of arrest.
     *
     * @since 2.1.1
     */

    abstract public void returnLockedInventory();

    /**
     * This method will clear a users locked up inventory. This is items that
     * were confiscated at the time of arrest.
     *
     * @return <code>true</code> The player has items currently locked up in the
     * jails inventory system<br>
     * <code>false</code> The player has no items pending in the jails
     * inventory system
     * @since 2.1.1
     */
    abstract public boolean hasLockedInventory();

    /**
     * This method will provide a squared distance to the closest configured jail
     *
     * @return <code>Double</code> Number of blocks to the closest jail
     * @since 2.1.1
     */
    abstract public Double distanceToJail();

    /**
     * This method will send a player to the nearest configured jail
     *
     * @since 2.1.1
     */
    abstract public void arrestPlayer();

    /**
     * This method will release a player from jail
     *
     * @since 2.1.1
     */
    abstract public void releasePlayer();

    /**
     * This method will return the number of bounty currently being awarded to
     * this player per second<br>
     * <strong>Negative numbers are lowering the users bounty, where as positive
     * are adding onto it</strong>
     *
     * @return <code>Double</code> Returns the number of bounty currently being
     * awarded/subtracted per second to this player
     * @since 2.1.1
     */
    abstract public Double getBountyPerSecond();

    /**
     * This method will return the players WANTED_LEVEL<br>
     *
     * @return <code>WANTED_LEVEL</code> Returns players wanted level
     * @since 2.2.1
     */
    abstract public Enumerations.WANTED_LEVEL getWantedLevel();

    /**
     * This method will attempt to set the players wanted level.<br>
     *
     * @param wantedLevel New wanted level to force upon player
     * @param reason      The reason the player received this new wanted level
     * @since 2.2.1
     */
    abstract public void setWantedLevel(Enumerations.WANTED_SETTING wantedLevel, Enumerations.JAILED_BOUNTY reason);

    /**
     * This method will attempt to set the players wanted level.<br>
     *
     * @param wantedLevel New wanted level to force upon player
     * @param reason      The reason the player received this new wanted level
     * @param report      Raise events to allow other plugins to see this Level change
     * @since 2.2.1
     */
    abstract public void setWantedLevel(Enumerations.WANTED_SETTING wantedLevel, Enumerations.JAILED_BOUNTY reason, boolean report);

    /**
     * This method will force the players wanted level.<br>
     *
     * @param wantedLevel New wanted level to force upon player
     * @param reason      The reason the player received this new wanted level
     * @param report      Raise events to allow other plugins to see this Level change
     * @since 2.2.1
     */
    abstract public void setWantedLevelForced(Enumerations.WANTED_SETTING wantedLevel, Enumerations.JAILED_BOUNTY reason, boolean report);


    /**
     * This method provides access to the NPC that last spotted the user<br>
     *
     * @return <code>NPC</code> returns a citizens NPC object
     * for the NPC that spotted the user<br>
     * @since 2.2.122
     */
    abstract public NPC getLastSpottedBy();

    /**
     * This method provides access to last time the NPC was spotted by an NPC <br>
     *
     * @return <code>Date</code> returns a java date <br>
     * @since 2.2.122
     */
    abstract public Date getLastSpottedTime();


}
