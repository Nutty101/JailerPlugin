package net.livecar.nuttyworks.npc_police.players;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.*;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;
import net.livecar.nuttyworks.npc_police.api.events.*;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_LockedInventory;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.listeners.commands.Pending_Command;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.*;

public class Arrest_Record implements Listener {
    public String lastAttack = "";
    public String currentJailName = "";
    public Jail_Setting currentJail = null;
    public Location lastJailCell = null;

    public Date lastNotification = new Date(0);
    public boolean enableDebug = false;

    private UUID playerUUID = null;

    private CURRENT_STATUS currentStatus;
    private CURRENT_STATUS priorStatus;
    private WANTED_LEVEL wantedLevel = WANTED_LEVEL.NONE;

    private HashMap<String, Date> regionCoolDowns = null;
    private HashMap<String, Integer> statistics = new HashMap<String, Integer>();
    private HashMap<String, Wanted_Information> wanted_Reasons = new HashMap<String, Wanted_Information>();
    private Double bounty = 0.0D;
    private Double totalbounty = 0.0D;
    private int pendingTime = 0;
    private Date arrestExpires = new Date(0);

    private Date lastCheck = new Date(0);
    private Date lastWarning = new Date(0);
    private Date lastArrest = new Date(0);
    private Date lastEscape = new Date(0);
    private NPC_Police getStorageReference;

    private Date lastSpottedTime = new Date(0);
    private NPC lastSpottedBy = null;
    private ItemStack[] lockedInventory = null;

    private Double lastMovementSpeed = 0.0;
    private Location lastMovementFrom ;
    private Location lastMovementTo;

    public Arrest_Record(NPC_Police policeRef, UUID playerID, Double totalBounty, HashMap<String, Integer> statistics, CURRENT_STATUS currentStatus, CURRENT_STATUS priorStatus) {
        this.getStorageReference = policeRef;

        try {
            playerUUID = playerID;
        } catch (Exception err) {
            return;
        }
        this.statistics = statistics;

        for (WANTED_REASONS name : WANTED_REASONS.values()) {
            if (!statistics.containsKey(name.toString()))
                statistics.put(name.toString(), 0);
        }
        statistics.put("ARRESTED", 0);
        this.totalbounty = totalBounty;

        regionCoolDowns = new HashMap<String, Date>();

        this.currentStatus = currentStatus;
        this.priorStatus = priorStatus;
    }

    public void setNewStatus(CURRENT_STATUS newStatus, WANTED_REASONS reason) {
        this.priorStatus = this.currentStatus;
        this.currentStatus = newStatus;
        switch (newStatus) {
            case JAILED:
            case ARRESTED:
                this.wanted_Reasons.clear();
                int statCount = statistics.get("ARRESTED");
                statistics.remove("ARRESTED");
                statCount++;
                statistics.put("ARRESTED", statCount);
                break;
            case ESCAPED:
                if (getStorageReference.getJailManager.getEscapeSetting(currentJail.jailWorld, currentJail) == ESCAPE_SETTING.DISABLED)
                    newStatus = CURRENT_STATUS.WANTED;
            case FREE:
            case WANTED:
                if (statistics.containsKey(newStatus.toString())) {
                    int current = statistics.get(newStatus.toString()) + 1;
                    statistics.remove(newStatus.toString());
                    statistics.put(newStatus.toString(), current);
                }
                break;
            default:
                break;
        }

        if (newStatus == CURRENT_STATUS.ESCAPED)
            this.lastEscape = new Date();
        if (newStatus == CURRENT_STATUS.JAILED)
            this.lastArrest = new Date();

        if (newStatus == CURRENT_STATUS.FREE) {
            if (lockedInventory != null && getStorageReference.getJailManager.onFreeReturnInventory(getPlayer().getWorld(), currentJail)) {
                if (currentJail == null || currentJail.lockedInventoryLocation == null) {

                    // Give the player their inventory back.
                    ItemStack[] plrInventory = getPlayer().getInventory().getContents();
                    this.getStorageReference.getUtilities.addToInventory(lockedInventory, plrInventory);
                    getPlayer().getInventory().setContents(plrInventory);
                    if (isOnline()) {
                        getStorageReference.getMessageManager.sendMessage(getPlayer(), "general_messages.time_served_invreturned", this);
                    }
                } else {
                    getStorageReference.getMessageManager.sendMessage(getPlayer(), "general_messages.time_served_invchest", this);
                }
            } else {
                clearLockedInventory();
                getStorageReference.getMessageManager.sendMessage(getPlayer(), "general_messages.time_served", this);
            }
        }

        if (newStatus == CURRENT_STATUS.ESCAPED) {
            if (lockedInventory != null && getStorageReference.getJailManager.onEsapeReturnInventory(getPlayer().getWorld(), currentJail) == STATE_SETTING.TRUE) {
                if (currentJail == null || currentJail.lockedInventoryLocation == null) {

                    // Give the player their inventory back.
                    ItemStack[] plrInventory = getPlayer().getInventory().getContents();
                    this.getStorageReference.getUtilities.addToInventory(lockedInventory, plrInventory);
                    getPlayer().getInventory().setContents(plrInventory);
                }
            } else if (lockedInventory != null && getStorageReference.getJailManager.onEsapeReturnInventory(getPlayer().getWorld(), currentJail) == STATE_SETTING.FALSE) {
                lockedInventory = null;
                getStorageReference.getMessageManager.sendMessage(getPlayer(), "jail_messages.escaped_invlost", this);
            }
        }

        StatusChangedEvent statusEvent = new Core_StatusChangedEvent(getStorageReference, newStatus, reason, this);
        try {Bukkit.getServer().getPluginManager().callEvent(statusEvent);} catch (Exception err) {}

    }

    public Jail_Setting getClosestJail() {
        if (!isOnline())
            return null;

        double lowestDistance = Double.MAX_VALUE;

        if (!getStorageReference.getJailManager.containsWorld(getPlayer().getWorld().getName()))
            return null;

        Location plrLocation = getPlayer().getLocation();

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plrLocation.getWorld().getName())) {
            if (this.wantedLevel.ordinal() < jailSetting.minWanted.ordinal() || this.wantedLevel.ordinal() > jailSetting.maxWanted.ordinal())
                continue;

            for (Location cellLocation : jailSetting.cellLocations) {
                if (plrLocation.distanceSquared(cellLocation) < lowestDistance) {
                    lowestDistance = plrLocation.distanceSquared(cellLocation);
                }
            }
        }

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plrLocation.getWorld().getName())) {
            if (this.wantedLevel.ordinal() < jailSetting.minWanted.ordinal() || this.wantedLevel.ordinal() > jailSetting.maxWanted.ordinal())
                continue;

            for (Location cellLocation : jailSetting.cellLocations) {
                if (plrLocation.distanceSquared(cellLocation) <= lowestDistance) {
                    return jailSetting;
                }
            }
        }

        return null;
    }

    public void setSpotted(NPC npc) {
        if (this.lastSpottedTime.getTime() < new Date().getTime()) {
            this.lastSpottedTime = new Date(new Date().getTime() + 500);
            this.lastSpottedBy = npc;
        }
    }

    public NPC getLastSpottedBy()
    {
        return this.lastSpottedBy;
    }

    public void setSpottedTime()
    {
        if (this.lastSpottedTime.getTime() < new Date().getTime())
            this.lastSpottedTime = new Date(new Date().getTime()+500);
    }

    public boolean isSpottedInCooldown()
    {
        if (this.lastSpottedTime.getTime() < new Date().getTime())
            return false;
        return true;
    }

    public Date getLastSpottedTime()
    {
        return this.lastSpottedTime;
    }


    public STATE_SETTING hasCoolDown(String regionName) {
        if (!isOnline())
            return STATE_SETTING.NOTSET;

        RegionSettings regionFlags = new RegionSettings();
        if (getStorageReference.getWorldGuardPlugin != null)
            regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(getPlayer().getLocation());

        if (regionFlags.autoFlag_CoolDown == null)
            return STATE_SETTING.NOTSET;

        if (!regionCoolDowns.containsKey(regionName)) {
            regionCoolDowns.put(regionName, new Date());
            return STATE_SETTING.FALSE;
        }

        Long coolTime = regionCoolDowns.get(regionName).getTime();
        if (coolTime + (regionFlags.autoFlag_CoolDown * 1000) < new Date().getTime()) {
            regionCoolDowns.remove(regionName);
            regionCoolDowns.put(regionName, new Date());
            return STATE_SETTING.FALSE;
        } else {
            return STATE_SETTING.TRUE;
        }
    }

    public void clearGroups() {
        if (getStorageReference.getPermissionManager == null)
            return;

        getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, getPlayer().getWorld()));
        getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, getPlayer().getWorld()));
        getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, getPlayer().getWorld()));
    }

    public STATE_SETTING isInCell() {
        if (isOnline()) {
            RegionSettings regionFlags = new RegionSettings();
            if (getStorageReference.getWorldGuardPlugin != null)
                regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(getPlayer().getLocation());
            if (regionFlags.isCell)
                return STATE_SETTING.TRUE;
            else if (getPlayer().getWorld().getTime() <= 12575) {
                if (getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_DAY, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == null || getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_DAY, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == Double.MIN_VALUE || getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_DAY, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == 0.0D)
                    return STATE_SETTING.NOTSET;
                else
                    return STATE_SETTING.FALSE;
            } else if (getPlayer().getWorld().getTime() >= 12575) {
                if (getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_NIGHT, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == null || getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_NIGHT, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == Double.MIN_VALUE || getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_NIGHT, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail) == 0.0D)
                    return STATE_SETTING.NOTSET;
                else
                    return STATE_SETTING.FALSE;
            }
            return STATE_SETTING.NOTSET;
        } else
            return STATE_SETTING.NOTSET;
    }

    public Jail_Setting getLastJailedLocation() {
        if (this.currentJail != null)
            return this.currentJail;
        return null;
    }

    public Location getJailedLocation() {
        return this.currentJail.cellLocations.get(0);
    }

    public void setJailedLocation(Location jailLocation) {
        this.lastJailCell = jailLocation;
    }

    public void setJailed(Jail_Setting jailSetting) {
        this.currentJail = jailSetting;
    }

    public Long getPlayerJailTime() {
        Double timeBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_JAILED, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);

        if (isOnline()) {
            RegionSettings regionFlags = new RegionSettings();
            if (getStorageReference.getWorldGuardPlugin != null)
                regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(getPlayer().getLocation());
            if (!regionFlags.isCell) {
                if (getPlayer().getWorld().getTime() < 12575)
                    timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_DAY, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                else
                    timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_NIGHT, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
            }
        }

        Long secondsLeft = 0L;

        if (this.pendingTime > 0) {
            secondsLeft += this.pendingTime;
        }

        if (this.arrestExpires.after(new Date())) {
            secondsLeft += ((new Date()).getTime() - this.arrestExpires.getTime()) / 1000;
        }

        if (timeBounty > -0.0000001)
            return Long.MAX_VALUE;

        if (bounty > 0 && timeBounty != 0.0)
            secondsLeft += (int) (bounty / Math.abs(timeBounty));

        return secondsLeft;
    }

    public int getStat(String statname) {
        if (statistics.containsKey(statname)) {
            return statistics.get(statname);
        }
        return 0;
    }

    public void clearWanted() {
        this.wanted_Reasons.clear();
    }

    public void clearRecord() {
        this.wanted_Reasons.clear();
        this.statistics.clear();
        for (WANTED_REASONS name : WANTED_REASONS.values()) {
            if (!statistics.containsKey(name.toString()))
                statistics.put(name.toString(), 0);
        }
        statistics.put("ARRESTED", 0);
        lastCheck = new Date();
        bounty = 0.0D;
        totalbounty = 0.0D;
        lastArrest = new Date(0);
        lastEscape = new Date(0);
        lastWarning = new Date(0);
        currentStatus = CURRENT_STATUS.FREE;
        priorStatus = CURRENT_STATUS.FREE;
        currentJail = null;
        wantedLevel = WANTED_LEVEL.LOW;
        lastAttack = "";
    }

    public void changeBounty(JAILED_BOUNTY reason, double bounty) {
        if (bounty == 0)
            return;

        // Validate the location for the player and get the bounty settings
        if (this.isOnline()) {
            Player curPlayer = this.getPlayer();
            Location plrLoc = curPlayer.getLocation();
            RegionSettings regionFlags = new RegionSettings();
            if (getStorageReference.getWorldGuardPlugin != null)
                regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(plrLoc);

            if (regionFlags.bounty_Maximum != null && regionFlags.bounty_Maximum > -1) {

                if ((this.bounty + bounty) >= regionFlags.bounty_Maximum) {
                    if (this.bounty < regionFlags.bounty_Maximum) {
                        // Fire off the notice that the player has exceeded it.
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.BOUNTY_MAXIMUM, curPlayer.getWorld(), this.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(curPlayer, sMsg, null, this, null, null, getStorageReference.getJailManager.getWorldSettings(curPlayer.getWorld().getName()), null, null, 0,null));
                            }
                        } catch (Exception err) {

                        }
                    }
                    this.bounty = regionFlags.bounty_Maximum;
                    if (bounty > 0)
                        this.totalbounty += (this.bounty + bounty) - regionFlags.bounty_Maximum;

                        BountyChangedEvent bountyChangedEvent = new Core_BountyChangedEvent(getStorageReference, this.bounty, reason, this);
                        try { Bukkit.getServer().getPluginManager().callEvent(bountyChangedEvent);} catch (Exception err) {}

                    return;
                }
            }

            World_Setting worldSetting = this.getStorageReference.getJailManager.getWorldSettings(plrLoc.getWorld().getName());
            if (worldSetting.getBounty_Maximum() != null && worldSetting.getBounty_Maximum() > -1) {
                if ((this.bounty + bounty) >= worldSetting.getBounty_Maximum()) {
                    if (this.bounty < worldSetting.getBounty_Maximum()) {
                        // Fire off the notice that the player has exceeded it.
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.BOUNTY_MAXIMUM, curPlayer.getWorld(), this.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(curPlayer, sMsg, null, this, null, null, getStorageReference.getJailManager.getWorldSettings(curPlayer.getWorld().getName()), null, null, 0,null));
                            }
                        } catch (Exception err) {

                        }
                    }

                    this.bounty = worldSetting.getBounty_Maximum();
                    if (bounty > 0)
                        this.totalbounty += (this.bounty + bounty) - worldSetting.getBounty_Maximum();


                        BountyChangedEvent bountyChangedEvent = new Core_BountyChangedEvent(getStorageReference, this.bounty, reason, this);
                    try { Bukkit.getServer().getPluginManager().callEvent(bountyChangedEvent); } catch (Exception err) {}

                    return;
                }
            }

            if (this.getStorageReference.getJailManager.getGlobalSettings().getBounty_Maximum() > -1) {
                Double configSetting = this.getStorageReference.getJailManager.getGlobalSettings().getBounty_Maximum();

                if ((this.bounty + bounty) >= configSetting) {
                    if (this.bounty < configSetting) {
                        // Fire off the notice that the player has exceeded it.
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.BOUNTY_MAXIMUM, curPlayer.getWorld(), this.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(curPlayer, sMsg, null, this, null, null, getStorageReference.getJailManager.getWorldSettings(curPlayer.getWorld().getName()), null, null, 0,null));
                            }
                        } catch (Exception err) {

                        }
                    }

                    this.bounty = configSetting;
                    if (bounty > 0)
                        this.totalbounty += (this.bounty + bounty) - configSetting;

                    BountyChangedEvent bountyChangedEvent = new Core_BountyChangedEvent(getStorageReference, this.bounty, reason, this);
                    try {Bukkit.getServer().getPluginManager().callEvent(bountyChangedEvent);} catch (Exception err) {}

                    return;
                }
            }
        }

        this.bounty += bounty;
        if (bounty > 0)
            this.totalbounty += bounty;

        if (this.bounty < 0.0) {
            setBounty(0.0);
        }

        BountyChangedEvent bountyChangedEvent = new Core_BountyChangedEvent(getStorageReference, this.bounty, reason, this);
        try {Bukkit.getServer().getPluginManager().callEvent(bountyChangedEvent);} catch (Exception err) {}

    }

    public int getBountyInt() {
        return this.bounty.intValue();
    }

    public int getTotalBountyInt() {
        return this.totalbounty.intValue();
    }

    public Double getBounty() {
        return this.bounty;
    }

    public void setBounty(double newBounty, JAILED_BOUNTY reason) {
        this.bounty = newBounty;

        BountyChangedEvent bountyChangedEvent = new Core_BountyChangedEvent(getStorageReference, this.bounty, reason, this);
        try {Bukkit.getServer().getPluginManager().callEvent(bountyChangedEvent);} catch (Exception err) {}
    }

    public void setBounty(double newBounty) {
        this.bounty = newBounty;
    }

    public double getTotalBounty() {
        return this.totalbounty;
    }

    public void changeTime(int seconds) {
        this.pendingTime += seconds;
    }

    public int getTime() {
        return this.pendingTime;
    }

    public void setTime(int rawTime, JAILED_BOUNTY reason) {
        this.pendingTime = rawTime;

        TimeChangedEvent timeChangedEvent = new Core_TimeChangedEvent(this.getStorageReference,rawTime,reason,this);
        try {Bukkit.getServer().getPluginManager().callEvent(timeChangedEvent);} catch (Exception err) {}
    }

    public void setTime(int rawTime) {
        this.pendingTime = rawTime;
    }

    public Date getJailedExpires() {
        return this.arrestExpires;
    }

    public void setJailedExpires(Date expires) {
        this.arrestExpires = expires;
    }

    public Date getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }

    public Date getLastWarning() {
        return lastWarning == null ? new Date(0) : lastWarning;
    }

    public void setLastWarning(Date lastWarning) {
        this.lastWarning = lastWarning;
    }

    public Date getLastArrest() {
        return lastArrest == null ? new Date() : lastArrest;
    }

    public void setLastArrest(Date lastArrest) {
        this.lastArrest = lastArrest;
    }

    public Date getLastEscape() {
        return lastEscape == null ? new Date() : lastEscape;
    }

    public void setLastEscape(Date lastEscape) {
        this.lastEscape = lastEscape;
    }

    public List<Wanted_Information> getWantedReasons() {
        return getWantedReasons(null);
    }

    public List<Wanted_Information> getWantedReasons(WANTED_REASONS filter) {
        List<Wanted_Information> resultList = new ArrayList<Wanted_Information>();
        for (Wanted_Information wantedInfo : this.wanted_Reasons.values()) {
            if (filter == null || (filter != null && wantedInfo.getWantedReasonEnum() == filter)) {
                resultList.add(wantedInfo);
            }
        }
        return resultList;
    }

    public void addWantedNoCheck(Wanted_Information wantedInfo) {
        String wantedHeader = wantedInfo.getWantedReason() + ":" + wantedInfo.getRelatedPlayerUUID().toString();
        this.wanted_Reasons.put(wantedHeader, wantedInfo);
    }

    public void addNewWanted(Wanted_Information wantedInfo) {
        String wantedHeader = "";
        int statCount = 0;
        switch (wantedInfo.getWantedReasonEnum()) {
            case PVP:
                wantedHeader = wantedInfo.getWantedReason() + ":" + wantedInfo.getAttackedName();
                if (this.wanted_Reasons.containsKey(wantedHeader)) {
                    Wanted_Information currentWanted = this.wanted_Reasons.get(wantedHeader);
                    currentWanted.addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                } else {
                    this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);
                }
                break;
            case ASSAULT:
                wantedHeader = wantedInfo.getWantedReason() + ":" + wantedInfo.getAttackedName();

                if (this.wanted_Reasons.containsKey(wantedHeader)) {
                    Wanted_Information currentWanted = this.wanted_Reasons.get(wantedHeader);
                    currentWanted.addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                } else {
                    this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    if (statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }

                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);
                }

                NPCAssaultEvent npcAssaultEvent = new Core_NPCAssaultEvent(this.getStorageReference,wantedInfo.getRelatedNPC(), wantedInfo.getWitness(), wantedInfo.getBountyValue().intValue(),this);
                try {Bukkit.getServer().getPluginManager().callEvent(npcAssaultEvent);} catch (Exception err) {}

                break;
            case THEFT:
                wantedHeader = wantedInfo.getWantedReason();
                if (this.wanted_Reasons.containsKey(wantedHeader)) {
                    Wanted_Information currentWanted = this.wanted_Reasons.get(wantedHeader);
                    currentWanted.addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                } else {
                    this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);
                }
                break;
            case ESCAPE:
                wantedHeader = wantedInfo.getWantedReason();
                this.wanted_Reasons.put(wantedHeader, wantedInfo);
                if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                    statistics.put(wantedInfo.getWantedReason(), 0);
                }
                statCount = statistics.get(wantedInfo.getWantedReason());
                statistics.remove(wantedInfo.getWantedReason());
                statCount++;
                statistics.put(wantedInfo.getWantedReason(), statCount);

                PlayerEscapedEvent playerEscapedEvent = new Core_PlayerEscapedEvent(this.getStorageReference, this.currentJail.jailName, this);
                try {Bukkit.getServer().getPluginManager().callEvent(playerEscapedEvent);} catch (Exception err) {}

                break;
            case MURDER:
                if (wantedInfo.wasNPCAttacked()) {
                    wantedHeader = WANTED_REASONS.MURDER.toString() + ":" + wantedInfo.getAttackedName();
                    if (this.wanted_Reasons.containsKey(wantedHeader)) {
                        this.wanted_Reasons.get(wantedHeader).addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                    } else {
                        this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    }
                    if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);

                    NPCMurderedEvent npcMurderedEvent = new Core_NPCMurderedEvent(this.getStorageReference, wantedInfo.getRelatedNPC(), wantedInfo.getWitness(), this);
                    try {Bukkit.getServer().getPluginManager().callEvent(npcMurderedEvent);} catch (Exception err) {}

                } else if (wantedInfo.getRelatedPlayerUUID() != null) {
                    wantedHeader = WANTED_REASONS.PVP.toString() + ":" + wantedInfo.getAttackedName();
                    if (this.wanted_Reasons.containsKey(wantedHeader)) {
                        wantedInfo.addOffense(this.wanted_Reasons.get(wantedHeader).getFirstOffenseDate(), this.wanted_Reasons.get(wantedHeader).getBountyValue());
                        this.wanted_Reasons.remove(wantedHeader);
                    }
                    this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);

                    PlayerMurderedEvent playerMurderedEvent = new Core_PlayerMurderedEvent(this.getStorageReference, wantedInfo.getRelatedPlayer(),wantedInfo.getWitness(), this);
                    try {Bukkit.getServer().getPluginManager().callEvent(playerMurderedEvent);} catch (Exception err) {}
                } else {
                    wantedHeader = WANTED_REASONS.MURDER.toString() + ":" + wantedInfo.getAttackedName();
                    if (this.wanted_Reasons.containsKey(wantedHeader)) {
                        this.wanted_Reasons.get(wantedHeader).addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                    } else {
                        this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    }
                    if (!statistics.containsKey(wantedInfo.getWantedReason())) {
                        statistics.put(wantedInfo.getWantedReason(), 0);
                    }
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);
    
                    EntityMurderedEvent entityMurderedEvent = new Core_EntityMurderedEvent(this.getStorageReference, wantedInfo.getRelatedEntity(), wantedInfo.getWitness(), this);
                    try {Bukkit.getServer().getPluginManager().callEvent(entityMurderedEvent);} catch (Exception err) {}
                }
                break;
            case PLUGIN:
            default:
                wantedHeader = "PLUGIN:" + wantedInfo.getWantedReason();
                if (this.wanted_Reasons.containsKey(wantedHeader)) {
                    Wanted_Information currentWanted = this.wanted_Reasons.get(wantedHeader);
                    currentWanted.addOffense(wantedInfo.getFirstOffenseDate(), wantedInfo.getBountyValue());
                } else {
                    this.wanted_Reasons.put(wantedHeader, wantedInfo);
                    statCount = statistics.get(wantedInfo.getWantedReason());
                    statistics.remove(wantedInfo.getWantedReason());
                    statCount++;
                    statistics.put(wantedInfo.getWantedReason(), statCount);
                }
                break;
        }
    }

    public KICK_ACTION playerKickCheck(World world, boolean onChange) {
        // Do we need to kick for the world?
        int plrWanted = this.wantedLevel.ordinal();

        World_Setting worldSettings = this.getStorageReference.getJailManager.getWorldSettings(world.getName());
        if (plrWanted < worldSettings.getMinimum_WantedLevel().ordinal() || plrWanted > worldSettings.getMaximum_WantedLevel().ordinal()) {
            //Does the region player is in have flags?
            KICK_ACTION wgAction = playerKickCheck(this.getPlayer().getLocation(), onChange);
            if (wgAction != KICK_ACTION.NOACTION)
                return wgAction;

            // Need to kick this player
            if (worldSettings.getKickType() != KICK_TYPE.NOTSET) {
                switch (worldSettings.getKickType()) {
                    case ARREST_SERVER:
                        if (onChange)
                            return KICK_ACTION.NOACTION;
                    case CHANGE_SERVER:
                        this.getStorageReference.getBungeeListener.switchServer(this.getPlayer(), worldSettings.getKickLocation());
                        return KICK_ACTION.SERVER;

                    case ARREST_WORLD:
                        if (onChange)
                            return KICK_ACTION.NOACTION;
                    case CHANGE_WORLD:
                        World newWorld = this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation());
                        if (newWorld == null) {
                            this.getStorageReference.getMessageManager.consoleMessage("console_messages.forced_world_notfound", worldSettings.getKickLocation());
                            return KICK_ACTION.NOACTION;
                        }

                        Location loc = new Location(this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getX(), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getY(), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getZ());
                        this.getPlayer().teleport(loc);
                        return KICK_ACTION.WORLD;
                    default:
                        break;
                }
            } else {
                return playerKickCheck(onChange);
            }
        }
        return KICK_ACTION.NOACTION;
    }

    public KICK_ACTION playerKickCheck(Location plrLocation, boolean onChange) {
        RegionSettings wgFlags = this.getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(plrLocation);
        if (wgFlags.wanted_Kick_Type != KICK_TYPE.NOTSET && !wgFlags.wanted_Kick_Location.isEmpty()) {
            switch (wgFlags.wanted_Kick_Type) {
                case ARREST_SERVER:
                    if (onChange)
                        return KICK_ACTION.NOACTION;

                    this.getStorageReference.getBungeeListener.switchServer(this.getPlayer(), wgFlags.wanted_Kick_Location);
                    return KICK_ACTION.SERVER;
                case CHANGE_SERVER:
                    if (!onChange)
                        return KICK_ACTION.NOACTION;

                    this.getStorageReference.getBungeeListener.switchServer(this.getPlayer(), wgFlags.wanted_Kick_Location);
                    return KICK_ACTION.SERVER;
                case ARREST_WORLD:
                    if (onChange)
                        return KICK_ACTION.NOACTION;

                    World newAWWorld = this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location);
                    if (newAWWorld == null) {
                        this.getStorageReference.getMessageManager.consoleMessage("console_messages.forced_world_notfound", wgFlags.wanted_Kick_Location);
                        return KICK_ACTION.NOACTION;
                    }
                    this.getPlayer().teleport(new Location(this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getX(), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getY(), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getZ()));
                    return KICK_ACTION.WORLD;
                case CHANGE_WORLD:
                    if (!onChange)
                        return KICK_ACTION.NOACTION;

                    World newWorld = this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location);
                    if (newWorld == null) {
                        this.getStorageReference.getMessageManager.consoleMessage("console_messages.forced_world_notfound", wgFlags.wanted_Kick_Location);
                        return KICK_ACTION.NOACTION;
                    }
                    this.getPlayer().teleport(new Location(this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getX(), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getY(), this.getStorageReference.pluginInstance.getServer().getWorld(wgFlags.wanted_Kick_Location).getSpawnLocation().getZ()));
                    return KICK_ACTION.WORLD;
                default:
                    break;
            }
        }
        return KICK_ACTION.NOACTION;
    }

    private KICK_ACTION playerKickCheck(boolean onChange) {
        // Do we need to kick for the world?
        int plrWanted = this.wantedLevel.ordinal();

        World_Setting worldSettings = this.getStorageReference.getJailManager.getGlobalSettings();
        if (plrWanted < worldSettings.getMinimum_WantedLevel().ordinal() || plrWanted > worldSettings.getMaximum_WantedLevel().ordinal()) {
            // Need to kick this player
            if (worldSettings.getKickType() != KICK_TYPE.NOTSET) {
                switch (worldSettings.getKickType()) {
                    case ARREST_SERVER:
                        if (onChange)
                            return KICK_ACTION.NOACTION;
                    case CHANGE_SERVER:
                        this.getStorageReference.getBungeeListener.switchServer(this.getPlayer(), worldSettings.getKickLocation());
                        return KICK_ACTION.SERVER;

                    case ARREST_WORLD:
                        if (onChange)
                            return KICK_ACTION.NOACTION;
                    case CHANGE_WORLD:
                        World newWorld = this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation());
                        if (newWorld == null) {
                            this.getStorageReference.getMessageManager.consoleMessage("console_messages.forced_world_notfound", worldSettings.getKickLocation());
                            return KICK_ACTION.NOACTION;
                        }

                        Location loc = new Location(this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getX(), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getY(), this.getStorageReference.pluginInstance.getServer().getWorld(worldSettings.getKickLocation()).getSpawnLocation().getZ());
                        this.getPlayer().teleport(loc);
                        return KICK_ACTION.WORLD;
                    default:
                        break;
                }
            }
        }
        return KICK_ACTION.NOACTION;
    }

    public Jail_Setting sendPlayerToJail(WANTED_REASONS reason) {
        if (this.currentStatus != CURRENT_STATUS.JAILED)
            currentJailName = "";

        return sendPlayerToJail(reason, currentJailName, 0);
    }

    public Jail_Setting sendPlayerToJail(WANTED_REASONS reason, Integer length) {
        return sendPlayerToJail(reason, currentJailName, length);
    }

    public Jail_Setting sendPlayerToJail(WANTED_REASONS reason, String jailName) {
        return sendPlayerToJail(reason, jailName, 0);
    }

    public Jail_Setting sendPlayerToJail(WANTED_REASONS reason, String jailName, Integer length) {
        if (!isOnline())
            return null;

        Jail_Setting jailRecord = this.getStorageReference.getJailManager.getJailByName(jailName);
        if (jailRecord == null) {
            // Find the closest jail.
            jailRecord = getClosestJail();
            if (jailRecord == null || jailRecord.cellLocations == null || jailRecord.cellLocations.size() < 1)
                return null;
        }

        Random rRnd = new Random();

        int cellIndex = rRnd.nextInt(jailRecord.cellLocations.size());
        getPlayer().teleport(jailRecord.cellLocations.get(cellIndex), TeleportCause.PLUGIN);
        setJailed(jailRecord);
        setJailedLocation(jailRecord.cellLocations.get(cellIndex));
        clearWanted();
        this.priorStatus = this.currentStatus;
        this.currentStatus = CURRENT_STATUS.JAILED;

        Core_StatusChangedEvent statusEvent = new Core_StatusChangedEvent(getStorageReference, CURRENT_STATUS.JAILED, reason, this);
        try {Bukkit.getServer().getPluginManager().callEvent(statusEvent);} catch (Exception err) {}

        if (length > 0) {
            Double bountyAdd = this.getBountyPerSecond();
            if (bountyAdd > 0)
                bountyAdd = 0 - bountyAdd;
            else
                bountyAdd = Math.abs(bountyAdd);

            this.changeBounty(JAILED_BOUNTY.MANUAL, bountyAdd * length);
        }

        if (lockedInventory == null)
            lockedInventory = new ItemStack[100];

        if (getStorageReference.getJailManager.onArrestTakeInventory(getPlayer().getWorld(), jailRecord)) {
            ItemStack[] plrInventory = getPlayer().getInventory().getContents();

            // Check for banned items, do not add it to the locked inventory list
            World_Setting worldConfig = getStorageReference.getJailManager.getWorldSettings(jailRecord.jailWorld.getName());
            if (worldConfig != null && worldConfig.bannedItems != null) {
                for (int bannedCount = 0; bannedCount < worldConfig.bannedItems.length; bannedCount++) {
                    if (worldConfig.bannedItems[bannedCount] == null || worldConfig.bannedItems[bannedCount].getType() == Material.AIR)
                        continue;

                    for (int plrInvCount = 0; plrInvCount < plrInventory.length; plrInvCount++) {
                        if (plrInventory[plrInvCount] != null && plrInventory[plrInvCount].getType() != Material.AIR) {
                            if (getStorageReference.getUtilities.isItemSimular(plrInventory[plrInvCount], worldConfig.bannedItems[bannedCount])) {
                                plrInventory[plrInvCount] = null;
                            }
                        }
                    }
                }
            }

            World_Setting globalConfig = getStorageReference.getJailManager.getWorldSettings();
            if (globalConfig != null && globalConfig.bannedItems != null) {
                for (int bannedCount = 0; bannedCount < globalConfig.bannedItems.length; bannedCount++) {
                    if (globalConfig.bannedItems[bannedCount] == null || globalConfig.bannedItems[bannedCount].getType() == Material.AIR)
                        continue;

                    for (int plrInvCount = 0; plrInvCount < plrInventory.length; plrInvCount++) {
                        if (plrInventory[plrInvCount] != null && plrInventory[plrInvCount].getType() != Material.AIR) {
                            if (getStorageReference.getUtilities.isItemSimular(plrInventory[plrInvCount], globalConfig.bannedItems[bannedCount])) {
                                plrInventory[plrInvCount] = null;
                            }
                        }
                    }
                }
            }

            getStorageReference.getUtilities.addToInventory(plrInventory, lockedInventory);
            getPlayer().getInventory().clear();

            getStorageReference.getMessageManager.sendMessage(getPlayer(), "jail_messages.player_jailed_onrelease", this);
        }
        if (getStorageReference.getJailManager.onEsapeReturnInventory(getPlayer().getWorld(), jailRecord) == STATE_SETTING.FALSE) {
            getStorageReference.getMessageManager.sendMessage(getPlayer(), "jail_messages.player_jailed_noescape", this);
        }



        if (getStorageReference.getPermissionManager == null)
            return jailRecord;

        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerAddGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, getPlayer().getWorld()));
        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, getPlayer().getWorld()));
        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, getPlayer().getWorld()));

        return jailRecord;
    }

    public double getBountyPerSecond() {
        Double timeBounty = 0.0;

        switch (currentStatus) {
            case ARRESTED:
                break;
            case ESCAPED:
                timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_ESCAPED, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                break;
            case FREE:
                break;
            case JAILED:
                timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_JAILED, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                if (this.isInCell() == STATE_SETTING.TRUE) {
                    if (getPlayer().getWorld().getTime() < 12575)
                        timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_DAY, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                    else
                        timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_CELLOUT_NIGHT, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                }
                break;
            case WANTED:
                timeBounty += getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_WANTED, isOnline() ? getPlayer().getLocation().getWorld() : null, currentJail);
                break;
            default:
                break;
        }

        return timeBounty;
    }

    public void releasePlayer() {
        clearWanted();
        setBounty(0);
        setNewStatus(CURRENT_STATUS.FREE, WANTED_REASONS.BOUNTY);
        if (currentJail != null && currentJail.freeSpawnPoint != null) {
            getPlayer().teleport(currentJail.freeSpawnPoint, TeleportCause.PLUGIN);
        }

        try {
            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.PLAYER_RELEASED, getPlayer().getWorld(), currentJail)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(getPlayer(), sMsg, null, this, null, null, getStorageReference.getJailManager.getWorldSettings(getPlayer().getWorld().getName()), null, null, 0,null));
            }
        } catch (Exception err) {

        }

        if (getStorageReference.getPermissionManager == null)
            return;

        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, getPlayer().getWorld()));
        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, getPlayer().getWorld()));
        if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, getPlayer().getWorld()).isEmpty())
            getStorageReference.getPermissionManager.playerRemoveGroup(getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, getPlayer().getWorld()));

    }

    public void clearLockedInventory() {
        this.lockedInventory = null;
    }

    public ItemStack[] getLockedInventory() {
        return this.lockedInventory;
    }

    public void addToLockedInventory(ItemStack[] inventory) {
        if (lockedInventory == null)
            lockedInventory = new ItemStack[100];

        getStorageReference.getUtilities.addToInventory(inventory, lockedInventory);
    }

    public void returnLockedInventory() {
        ItemStack[] plrInventory = getPlayer().getInventory().getContents();
        this.getStorageReference.getUtilities.addToInventory(lockedInventory, plrInventory);
        getPlayer().getInventory().setContents(plrInventory);
    }

    public boolean hasLockedInventory() {
        for (ItemStack item : lockedInventory) {
            if (item != null && item.getType() != Material.AIR)
                return true;
        }
        return false;
    }

    public String serializeLockedInventory() {
        return getStorageReference.getUtilities.serialzeItemStack(lockedInventory);
    }

    public Player getPlayer() {
        return getStorageReference.pluginInstance.getServer().getPlayer(playerUUID);
    }

    public OfflinePlayer getOfflinePlayer() {
        return getStorageReference.pluginInstance.getServer().getOfflinePlayer(playerUUID);
    }

    public boolean isOnline() {
        return getStorageReference.pluginInstance.getServer().getOfflinePlayer(playerUUID).isOnline();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public CURRENT_STATUS getCurrentStatus() {
        return this.currentStatus;
    }

    public CURRENT_STATUS getPriorStatus() {
        return this.priorStatus;
    }

    public WANTED_LEVEL getWantedLevel() {
        return this.wantedLevel;
    }

    public void setWantedLevel(WANTED_LEVEL wantedLevel, JAILED_BOUNTY reason) {
        setWantedLevel(wantedLevel, reason, false);
    }

    public void setWantedLevel(WANTED_LEVEL wantedLevel, JAILED_BOUNTY reason, boolean report) {
        this.wantedLevel = wantedLevel;
        if (report) {
            WantedLevelChangedEvent wantedLevelEvent = new Core_WantedLevelChangedEvent(getStorageReference, wantedLevel, reason, this, false);
            try {Bukkit.getServer().getPluginManager().callEvent(wantedLevelEvent);} catch (Exception err) {}
        }

        if (this.getPlayer().isOnline()) {
            if (getStorageReference.getJailManager.containsWorld(this.getPlayer().getWorld().toString())) {
                World_Setting worldSetting = getStorageReference.getJailManager.getWorldSettings(this.getPlayer().getWorld().toString());
                if (worldSetting.getKickType() != KICK_TYPE.NOTSET) {
                    if (worldSetting.getKickType() == KICK_TYPE.CHANGE_SERVER) {
                        if (this.wantedLevel.ordinal() < worldSetting.getMinimum_WantedLevel().ordinal() || this.wantedLevel.ordinal() > worldSetting.getMinimum_WantedLevel().ordinal()) {
                            getStorageReference.getBungeeListener.switchServer(this.getPlayer(), worldSetting.getKickLocation());
                            return;
                        }
                    } else if (worldSetting.getKickType() == KICK_TYPE.CHANGE_WORLD) {
                        if (this.wantedLevel.ordinal() < worldSetting.getMinimum_WantedLevel().ordinal() || this.wantedLevel.ordinal() > worldSetting.getMinimum_WantedLevel().ordinal()) {
                            for (World newWorld : Bukkit.getServer().getWorlds()) {
                                if (!newWorld.getName().equalsIgnoreCase(worldSetting.getKickLocation()))
                                    continue;
                                this.getPlayer().teleport(newWorld.getSpawnLocation());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void setWantedLevel(WANTED_SETTING wantedLevel, JAILED_BOUNTY reason) {
        setWantedLevel(wantedLevel, reason, false);
    }

    public void setWantedLevel(WANTED_SETTING wantedLevel, JAILED_BOUNTY reason, boolean report) {

        switch (wantedLevel) {
            case LEVELDOWN:
                setWantedLevel(this.wantedLevel.previous(), reason, report);
                break;
            case LEVELUP:
                setWantedLevel(this.wantedLevel.next(), reason, report);
                break;
            case LOW:
            case MEDIUM:
            case MINIMUM:
            case HIGH:
                if (this.wantedLevel.ordinal() < WANTED_SETTING.getWantedLevel(wantedLevel).ordinal())
                    setWantedLevel(WANTED_LEVEL.valueOf(wantedLevel.toString()), reason, report);
                break;
            default:
                break;
        }
    }

    public void setWantedLevelForced(WANTED_SETTING wantedLevel, JAILED_BOUNTY reason, boolean report) {
        this.wantedLevel = WANTED_LEVEL.valueOf(wantedLevel.toString());
        if (report) {
            WantedLevelChangedEvent wantedLevelEvent = new Core_WantedLevelChangedEvent(getStorageReference, WANTED_LEVEL.valueOf(wantedLevel.toString()), reason, this, true);
            try {Bukkit.getServer().getPluginManager().callEvent(wantedLevelEvent);} catch (Exception err) {}
        }
    }

    public void setMovementSpeed(Location from, Location to)
    {
        this.lastMovementSpeed = from.distanceSquared(to);
        this.lastMovementFrom = from;
        this.lastMovementTo = to;
    }

    public Double getLastMovementSpeed()
    {
        return this.lastMovementSpeed;
    }

    public void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(this, getStorageReference.pluginInstance);
    }

    public void unRegisterEvents()
    {
        try {
            PlayerMoveEvent.getHandlerList().unregister(this);
        } catch (Exception err)
        {}
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!getStorageReference.pluginInstance.isEnabled())
            return;

        if (!e.getPlayer().getUniqueId().equals(this.playerUUID))
            return;

        this.lastMovementSpeed = e.getFrom().distanceSquared(e.getTo());
        this.lastMovementFrom = e.getFrom();
        this.lastMovementTo = e.getTo();

    }


}
