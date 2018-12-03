package net.livecar.nuttyworks.npc_police.players;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.*;
import net.livecar.nuttyworks.npc_police.api.events.Core_PlayerSpottedEvent;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.jails.DistanceDelaySetting;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.listeners.commands.Pending_Command;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerDataManager {

    public ScheduledExecutorService playerMonitorTask;
    public ScheduledExecutorService playerDataMonitorTask;
    public HashMap<UUID, Pending_Command> pendingCommands = null;
    private NPC_Police getStorageReference = null;
    private ConcurrentHashMap<UUID, Arrest_Record> playerData = new ConcurrentHashMap<UUID, Arrest_Record>();

    public PlayerDataManager(NPC_Police policeRef) {
        getStorageReference = policeRef;
        pendingCommands = new HashMap<UUID, Pending_Command>();
    }

    public void MonitorPlayers() {
        if (playerMonitorTask == null) {
            playerMonitorTask = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    MonitorTask();
                }
            };
            playerMonitorTask.scheduleAtFixedRate(task, 120, 60, TimeUnit.MILLISECONDS);
        } else {
            playerMonitorTask.shutdown();
        }
    }

    public Arrest_Record getPlayer(UUID playerUUID) {
        if (playerData.containsKey(playerUUID)) {
            return playerData.get(playerUUID);
        } else {
            getStorageReference.getDatabaseManager.queueLoadPlayerRequest(playerUUID);
            return null;
        }
    }

    public Arrest_Record[] getPlayerRecords() {
        return playerData.values().toArray(new Arrest_Record[playerData.values().size()]);
    }

    public void removePlayerRecord(Arrest_Record plrRecord) {
        if (playerData.containsKey(plrRecord.getPlayerUUID())) {
            playerData.remove(plrRecord.getPlayerUUID());
        }
    }

    public void addPlayerRecord(Arrest_Record plrRecord) {
        if (!playerData.containsKey(plrRecord.getPlayerUUID())) {
            playerData.put(plrRecord.getPlayerUUID(), plrRecord);
        }
    }

    public void setPlayerWantedLevel(OfflinePlayer player, WANTED_LEVEL wantedLevel, JAILED_BOUNTY changeReason) {
        Arrest_Record plrRecord = getPlayer(player.getUniqueId());
        if (plrRecord != null) {
            plrRecord.setWantedLevel(wantedLevel, changeReason, true);
        }
    }

    public void setPlayerStatus(OfflinePlayer player, CURRENT_STATUS wantedStatus, WANTED_REASONS changeReason) {
        Arrest_Record plrRecord = getPlayer(player.getUniqueId());
        if (plrRecord != null) {
            switch (wantedStatus) {
                case ESCAPED:
                case WANTED:
                    plrRecord.setNewStatus(wantedStatus, changeReason);
                    plrRecord.lastNotification = new Date();
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getJailedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getWantedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getEscapedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));

                    if (player.isOnline())
                        getStorageReference.getMessageManager.sendMessage(player.getPlayer(), "general_messages.config_command_setstatus_wanted", plrRecord);
                    break;
                case ARRESTED:
                    break;
                case FREE:
                    plrRecord.setNewStatus(CURRENT_STATUS.FREE, changeReason);
                    plrRecord.lastNotification = new Date();
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getJailedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getWantedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                    if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getWorldSettings().getEscapedGroup().isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));

                    break;
                case JAILED:
                    break;
                default:
                    break;
            }
        }
    }

    public void SaveMonitor() {
        if (playerDataMonitorTask == null) {
            playerDataMonitorTask = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    SavePlayers();
                }
            };
            playerDataMonitorTask.scheduleAtFixedRate(task, 120, 60, TimeUnit.MILLISECONDS);
        } else {
            playerDataMonitorTask.shutdown();
            SavePlayers();
        }
    }

    private void SavePlayers() {
        for (Arrest_Record plrRecord : getStorageReference.getPlayerManager.getPlayerRecords()) {
            getStorageReference.getDatabaseManager.queueSavePlayerRequest(plrRecord);
        }
    }

    private void MonitorTask() {

        // Clean out pending commands
        for (Entry<UUID, Pending_Command> pendingSet : pendingCommands.entrySet()) {
            if (pendingSet.getValue().timeOutTime.getTime() < new Date().getTime()) {
                if (!pendingSet.getValue().timeoutMessage.trim().equals("") && !getStorageReference.pluginInstance.getServer().getOfflinePlayer(pendingSet.getKey()).isOnline()) {
                    getStorageReference.getMessageManager.sendMessage(getStorageReference.pluginInstance.getServer().getOfflinePlayer(pendingSet.getKey()).getPlayer(), pendingSet.getValue().timeoutMessage);
                }
                pendingCommands.remove(pendingSet.getKey());
            }
        }

        Double timeBounty = 0.0;

        // Check to see if the player's time is up. If so, release them.
        for (Entry<UUID, Arrest_Record> playerKP : playerData.entrySet()) {
            Arrest_Record plrRecord = playerKP.getValue();
            if (plrRecord.isOnline()) {
                if (!getStorageReference.getJailManager.containsWorld(plrRecord.getPlayer().getLocation().getWorld().getName()))
                    continue;
                if (getStorageReference.getJailManager.getWorldJails(plrRecord.getPlayer().getLocation().getWorld().getName()).length == 0)
                    continue;

                NPC guardWithSight = null;

                RegionSettings regionFlags = new RegionSettings();
                if (getStorageReference.getWorldGuardPlugin != null)
                    regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(plrRecord.getPlayer().getLocation());
                for (Iterator<NPC> npcIter = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().iterator(); npcIter.hasNext(); ) {
                    NPC oTmpNPC = npcIter.next();
                    if ((oTmpNPC != null) && (oTmpNPC.hasTrait(NPCPolice_Trait.class))) {
                        if (oTmpNPC.isSpawned()) {
                            if (oTmpNPC.getEntity().getLocation().getWorld().getName() == plrRecord.getPlayer().getLocation().getWorld().getName()) {
                                NPCPolice_Trait trait = oTmpNPC.getTrait(NPCPolice_Trait.class);
                                if (plrRecord.getCurrentStatus() == CURRENT_STATUS.FREE || plrRecord.getCurrentStatus() == CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() == CURRENT_STATUS.JAILED)
                                    if (getStorageReference.getSentinelPlugin != null)
                                        getStorageReference.getSentinelPlugin.clearTarget(oTmpNPC, plrRecord.getPlayer());

                                if (trait.isGuard) {
                                    boolean hasSight = getStorageReference.getUtilities.hasLineOfSight((LivingEntity) oTmpNPC.getEntity(), plrRecord.getPlayer());

                                    if (hasSight) {
                                        if (getStorageReference.getJailManager.getLOSSetting(plrRecord.getPlayer().getLocation().getWorld(), trait) == STATE_SETTING.TRUE && plrRecord.getCurrentStatus() != CURRENT_STATUS.FREE && plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED)
                                            if (getStorageReference.getSentinelPlugin != null)
                                                getStorageReference.getSentinelPlugin.addTarget(oTmpNPC, plrRecord.getPlayer());

                                        if (guardWithSight == null) {
                                            if (!plrRecord.isSpottedInCooldown()) {
                                                guardWithSight = oTmpNPC;
                                                    Core_PlayerSpottedEvent spottedEvent = new Core_PlayerSpottedEvent(getStorageReference, oTmpNPC, plrRecord);
                                                    try {Bukkit.getServer().getPluginManager().callEvent(spottedEvent);} catch (Exception err) {}
                                                plrRecord.setSpottedTime();
                                            }
                                        }
                                    }

                                    if (regionFlags.region_AutoFlagStatus != null && plrRecord.getCurrentStatus() != regionFlags.region_AutoFlagStatus) {
                                        if ((hasSight && regionFlags.autoFlag_RequiresSight == STATE_SETTING.TRUE) || (regionFlags.autoFlag_RequiresSight != STATE_SETTING.TRUE)) {
                                            if (regionFlags.region_AutoFlagStatus != CURRENT_STATUS.JAILED && regionFlags.region_AutoFlagStatus != CURRENT_STATUS.ESCAPED) {
                                                if (guardWithSight == null) {
                                                    guardWithSight = oTmpNPC;
                                                }
                                                if (plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED && plrRecord.getCurrentStatus() != CURRENT_STATUS.ESCAPED) {
                                                    plrRecord.setNewStatus(regionFlags.region_AutoFlagStatus, WANTED_REASONS.REGION);
                                                }
                                            } else {
                                                plrRecord.setNewStatus(regionFlags.region_AutoFlagStatus, WANTED_REASONS.REGION);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (regionFlags.autoFlag_RequiresSight == STATE_SETTING.TRUE && guardWithSight != null) {
                    if (regionFlags.autoFlag_Bounty != null) {
                        if (regionFlags.autoFlag_CoolDown != null) {
                            if (plrRecord.hasCoolDown(regionFlags.regionName) == STATE_SETTING.FALSE) {
                                // Apply more bounty
                                plrRecord.changeBounty(JAILED_BOUNTY.MANUAL, regionFlags.autoFlag_Bounty);
                            }
                        } else {
                            plrRecord.changeBounty(JAILED_BOUNTY.MANUAL, regionFlags.autoFlag_Bounty);
                        }
                    }

                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.WANTED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                    }
                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.ESCAPED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
                    }
                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.JAILED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                    }

                    if (regionFlags.autoFlag_CaughtNotice != null && !regionFlags.autoFlag_CaughtNotice.trim().equals("")) {
                        plrRecord.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getStorageReference.getMessageManager.parseMessage(plrRecord.getPlayer(), regionFlags.autoFlag_CaughtNotice, null, plrRecord, null, null, null, guardWithSight, null, 0)));
                        break;
                    }
                } else if (regionFlags.autoFlag_Bounty != null) {
                    if (regionFlags.autoFlag_CoolDown != null) {
                        if (plrRecord.hasCoolDown(regionFlags.regionName) == STATE_SETTING.FALSE) {
                            // Apply more bounty
                            plrRecord.changeBounty(JAILED_BOUNTY.MANUAL, regionFlags.autoFlag_Bounty);
                        }
                    } else {
                        plrRecord.changeBounty(JAILED_BOUNTY.MANUAL, regionFlags.autoFlag_Bounty);
                    }

                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.WANTED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                    }
                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.ESCAPED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
                    }
                    if (regionFlags.region_AutoFlagStatus == CURRENT_STATUS.JAILED) {
                        plrRecord.clearGroups();
                        if (getStorageReference.getPermissionManager != null && !getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()).isEmpty())
                            getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                    }

                    if (regionFlags.autoFlag_CaughtNotice != null && !regionFlags.autoFlag_CaughtNotice.trim().equals("")) {
                        plrRecord.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getStorageReference.getMessageManager.parseMessage(plrRecord.getPlayer(), regionFlags.autoFlag_CaughtNotice, null, plrRecord, null, null, null, guardWithSight, null, 0)));
                        break;
                    }

                }

                try {
                    Boolean bIsInJail = false;

                    switch (plrRecord.getCurrentStatus()) {
                        case FREE:
                            if (playerKP.getValue().getLastCheck() == null) {
                                playerKP.getValue().setLastCheck(new Date());
                                continue;
                            }

                            break;
                        case WANTED:
                            // Need to add a check to see if there is a setting to
                            // increase the bounty
                            if (playerKP.getValue().getLastCheck() == null) {
                                playerKP.getValue().setLastCheck(new Date());
                                continue;
                            }

                            if (regionFlags.bounty_Wanted != null)
                                timeBounty = regionFlags.bounty_Wanted;
                            else
                                timeBounty = (getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_WANTED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail));

                            timeBounty = timeBounty * ((int) (new Date().getTime() - playerKP.getValue().getLastCheck().getTime()) / 1000);
                            plrRecord.changeBounty(JAILED_BOUNTY.TIMES_JAILED, timeBounty);

                            if (timeBounty > 0 && plrRecord.lastNotification.getTime() < (new Date()).getTime() - 10000) {
                                // Send a notice about their bounty
                                getStorageReference.getMessageManager.sendMessage(plrRecord.getPlayer(), "general_messages.player_bounty_changed", plrRecord);
                                plrRecord.lastNotification = new Date();
                            } else if (plrRecord.getBounty() < 1.0D) {
                                plrRecord.setNewStatus(CURRENT_STATUS.FREE, WANTED_REASONS.BOUNTY);
                                plrRecord.setBounty(0);
                                plrRecord.clearWanted();
                            }
                            break;
                        case ARRESTED:
                            // This is a status for when a player has the user arrested
                            // and is bringing them to jail
                            if (playerKP.getValue().getLastCheck() == null) {
                                playerKP.getValue().setLastCheck(new Date());
                                continue;
                            }

                            break;
                        case JAILED:
                            // Lower the players bounty, until it is 0, then release them
                            bIsInJail = false;

                            if (!getStorageReference.getJailManager.containsWorld(plrRecord.getPlayer().getWorld().getName()))
                                continue;

                            for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plrRecord.getPlayer().getWorld().getName())) {
                                if (!regionFlags.extendsJail.trim().equals("") && getStorageReference.getJailManager.getJailByName(regionFlags.extendsJail) != null)
                                    bIsInJail = true;
                                else if (getStorageReference.getWorldGuardPlugin.isInRegion(plrRecord.getPlayer().getLocation(), jailSetting.regionName)) {
                                    if (plrRecord.currentJail == null || plrRecord.currentJail.jailName.equalsIgnoreCase(jailSetting.jailName))
                                        plrRecord.currentJail = jailSetting;
                                    bIsInJail = true;
                                    break;
                                }
                            }

                            if (!bIsInJail && plrRecord.getLastCheck() == null) {
                                if (plrRecord.sendPlayerToJail() != null)
                                    bIsInJail = true;
                            }

                            if (plrRecord.getLastCheck() == null)
                                plrRecord.setLastCheck(new Date());

                            if (!bIsInJail) {
                                // They escaped! Remove from the list and let them know
                                // they have a warrant now.
                                for (Player onlinePlayer : getStorageReference.pluginInstance.getServer().getOnlinePlayers()) {

                                    if (onlinePlayer.getUniqueId().equals(plrRecord.getPlayerUUID())) {
                                        // Let the player know they escaped...
                                        getStorageReference.getMessageManager.sendMessage(onlinePlayer, "jail_messages.escaped_notice", plrRecord);
                                    } else {
                                        if (!plrRecord.getPlayer().getWorld().equals(onlinePlayer.getWorld()))
                                            continue;

                                        DistanceDelaySetting distSetting = getStorageReference.getJailManager.getNoticeSetting(NOTICE_SETTING.ESCAPED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);
                                        if (onlinePlayer.getLocation().distanceSquared(plrRecord.getPlayer().getLocation()) < distSetting.getDistanceSquared()) {
                                            if (distSetting.getDelay() < 0.001D) {
                                                // send it now
                                                getStorageReference.getMessageManager.sendMessage(onlinePlayer, "jail_messages.escaped_broadcast", plrRecord);
                                            } else {
                                                // Delayed sending of this message
                                                final Player targetPlayer = onlinePlayer.getPlayer();
                                                final Arrest_Record playerRecord = plrRecord;

                                                getStorageReference.pluginInstance.getServer().getScheduler().scheduleSyncDelayedTask(
                                                        getStorageReference.pluginInstance, new Runnable() {
                                                            public void run() {
                                                                getStorageReference.getMessageManager.sendMessage(targetPlayer, "jail_messages.escaped_broadcast", playerRecord);
                                                            }
                                                        }, (long) (distSetting.getDelay() * 20L)
                                                );
                                            }
                                        }
                                    }
                                }

                                Double addedBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.BOUNTY_ESCAPED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);
                                plrRecord.changeBounty(JAILED_BOUNTY.BOUNTY_ESCAPED, addedBounty);
                                plrRecord.setNewStatus(CURRENT_STATUS.ESCAPED, WANTED_REASONS.ESCAPE);
                                try {
                                    for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.PLAYER_ESCAPED, plrRecord.getPlayer().getWorld(), plrRecord.currentJail)) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(plrRecord.getPlayer(), sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(plrRecord.getPlayer().getWorld().getName()), null, null, 0));
                                    }
                                } catch (Exception err) {

                                }
                                plrRecord.clearWanted();
                                plrRecord.setLastEscape(new Date());
                                if (getStorageReference.getPermissionManager != null) {
                                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                                    getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
                                }

                            } else {
                                // Check to see if the player has a locked time pending.
                                if (!plrRecord.getJailedExpires().after(new Date())) {

                                    Long secondsPast = ((new Date().getTime() - playerKP.getValue().getLastCheck().getTime()) / 1000);

                                    if (secondsPast == 0.0D)
                                        continue;

                                    if (plrRecord.getTime() > 0) {
                                        //# of seconds before changing bounty
                                        if (secondsPast > plrRecord.getTime()) {
                                            secondsPast -= plrRecord.getTime();
                                            plrRecord.setTime(0);
                                        } else {
                                            plrRecord.setTime(plrRecord.getTime() - secondsPast.intValue());
                                            secondsPast = 0L;
                                        }
                                    }

                                    if (!regionFlags.isCell) {
                                        JAILED_BOUNTY bountyType = null;
                                        if (plrRecord.getPlayer().getWorld().getTime() < 12575)
                                            bountyType = JAILED_BOUNTY.TIMES_CELLOUT_DAY;
                                        else
                                            bountyType = JAILED_BOUNTY.TIMES_CELLOUT_NIGHT;

                                        timeBounty = getStorageReference.getJailManager.getBountySetting(bountyType, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);
                                        plrRecord.changeBounty(bountyType, (int) (timeBounty * secondsPast));
                                    }

                                    timeBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_JAILED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);
                                    plrRecord.changeBounty(JAILED_BOUNTY.TIMES_JAILED, (int) (timeBounty * secondsPast));

                                    if (plrRecord.getBounty() > 0) {

                                        if (timeBounty > 0 && plrRecord.lastNotification.getTime() < (new Date()).getTime() - (getStorageReference.getJailManager.getGlobalSettings().getBounty_Notice() * 1000)) {
                                            // Send a notice about their bounty
                                            getStorageReference.getMessageManager.sendMessage(plrRecord.getPlayer(), "general_messages.player_bounty_changed", plrRecord);
                                            plrRecord.lastNotification = new Date();
                                        }
                                    } else if (plrRecord.getBounty() < 1) {
                                        // Set the players status
                                        plrRecord.releasePlayer();
                                        getStorageReference.getMessageManager.sendMessage(plrRecord.getPlayer(), "jail_messages.time_served", plrRecord);
                                    }
                                }
                            }
                            playerKP.getValue().setLastCheck(new Date());
                            break;
                        case ESCAPED:
                            if (playerKP.getValue().getLastCheck() == null) {
                                playerKP.getValue().setLastCheck(new Date());
                                continue;
                            }

                            bIsInJail = false;

                            for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plrRecord.getPlayer().getWorld().getName())) {
                                if (!regionFlags.extendsJail.trim().equals("") && getStorageReference.getJailManager.getJailByName(regionFlags.extendsJail) != null)
                                    bIsInJail = true;
                                else if (getStorageReference.getWorldGuardPlugin.isInRegion(plrRecord.getPlayer().getLocation(), jailSetting.regionName)) {
                                    if (plrRecord.currentJail == null || plrRecord.currentJail.jailName.equalsIgnoreCase(jailSetting.jailName))
                                        plrRecord.currentJail = jailSetting;
                                    bIsInJail = true;
                                    break;
                                }
                            }

                            if (bIsInJail) {
                                plrRecord.clearWanted();
                                plrRecord.setNewStatus(CURRENT_STATUS.JAILED, WANTED_REASONS.REGION);
                                if (getStorageReference.getPermissionManager != null) {
                                    getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
                                }
                                continue;
                            }

                            if (plrRecord.getLastCheck() == null)
                                plrRecord.setLastCheck(new Date());

                            if (regionFlags.bounty_Escaped != null)
                                timeBounty = regionFlags.bounty_Escaped;
                            else
                                timeBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.TIMES_ESCAPED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);

                            timeBounty = timeBounty * ((int) (new Date().getTime() - playerKP.getValue().getLastCheck().getTime()) / 1000);
                            plrRecord.changeBounty(JAILED_BOUNTY.TIMES_JAILED, timeBounty);

                            if (timeBounty > 0 && plrRecord.lastNotification.getTime() < (new Date()).getTime() - 10000) {
                                // Send a notice about their bounty
                                getStorageReference.getMessageManager.sendMessage(plrRecord.getPlayer(), "general_messages.player_bounty_changed", plrRecord);
                                plrRecord.lastNotification = new Date();
                            }
                            break;
                        default:
                            break;
                    }
                    plrRecord.setLastCheck(new Date());
                } catch (Exception err) {
                    // oops.
                    err.printStackTrace();
                }
            } else {
                // Remove this player from the list
                playerData.remove(playerKP.getKey());
            }
        }
    }

}