package net.livecar.nuttyworks.npc_police.listeners;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.*;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;
import net.livecar.nuttyworks.npc_police.api.events.Core_NPCMurderedEvent;
import net.livecar.nuttyworks.npc_police.api.events.Core_PlayerMurderedEvent;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.jails.DistanceDelaySetting;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Date;
import java.util.Iterator;

public class DamageListener implements Listener {
    private NPC_Police getStorageReference = null;

    public DamageListener(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (getStorageReference.disableDamageMonitoring)
            return;

        customEntityDamageByEntity(event);
    }

    public void customEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity() == null) {
            return;
        }

        if (!getStorageReference.getJailManager.containsWorld(event.getDamager().getLocation().getWorld().getName()))
            return;
        if (getStorageReference.getJailManager.getWorldJails(event.getDamager().getLocation().getWorld().getName()).length == 0)
            return;

        switch (event.getCause())
        {
            case THORNS:
                return;
            case DRAGON_BREATH:
                return;
        }

        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Arrow) {
            damager = (Entity) ((Arrow) damager).getShooter();
        }

        if (getStorageReference.Version >= 10902) {
            if (damager instanceof SplashPotion) {
                damager = (Entity) ((SplashPotion) damager).getShooter();
            }

            if (damager instanceof LingeringPotion) {
                damager = (Entity) ((LingeringPotion) damager).getShooter();
            }


        }

        if ((!damaged.hasMetadata("NPC")) && (damager.hasMetadata("NPC")))
            onNPCvsPlayer(damager, damaged, event);
        else if ((damager instanceof Player) && damaged.hasMetadata("NPC"))
            onPlayerVSNPC(damager, damaged, event);
        else if (((damager instanceof Player) && !damager.hasMetadata("NPC")) && ((damaged instanceof Player) && !damaged.hasMetadata("NPC")))
            onPlayervsPlayer(damager, damaged, event);

    }

    private void onNPCvsPlayer(Entity damager, Entity damaged, EntityDamageByEntityEvent event) {
        RegionSettings regionFlags = new RegionSettings();
        if (getStorageReference.getWorldGuardPlugin != null)
            regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(damaged.getLocation());

        NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(damager);
        if ((npc != null) && (npc.hasTrait(NPCPolice_Trait.class))) {
            NPCPolice_Trait trait = npc.getTrait(NPCPolice_Trait.class);
            if (trait.isGuard) {
                if (!(damaged instanceof Player)) {
                    return;
                }

                final Player player = Bukkit.getPlayer(damaged.getUniqueId());

                //2016-10-12:  Added permissions to bypass all NPC police actions.
                if (getStorageReference.hasPermissions(player, "npcpolice.bypass.*"))
                    return;

                // is the damage greater than the players health? if so, take
                // them to jail.
                if (event.getFinalDamage() >= ((Player) damaged).getHealth() && !regionFlags.noArrest) {

                    World tmpWorld = damaged.getWorld();

                    Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(player.getUniqueId());

                    KICK_ACTION kickAction = KICK_ACTION.NOACTION;

                    //2016-10-12:  Added permissions to bypass being kicking.
                    if (getStorageReference.hasPermissions(player, "npcpolice.bypass.kick"))
                        kickAction = plrRecord.playerKickCheck(tmpWorld, false);

                    if (kickAction == null)
                        kickAction = KICK_ACTION.NOACTION;

                    if (kickAction == KICK_ACTION.SERVER) {
                        event.setDamage(((Player) damaged).getHealth() - 1);
                        return;
                    } else if (kickAction == KICK_ACTION.WORLD) {
                        tmpWorld = player.getWorld();
                    }

                    final World world = tmpWorld;

                    if (!getStorageReference.getJailManager.containsWorld(world.getName())) {
                        // No jail setup on this world.
                        event.setDamage(((Player) damaged).getHealth() - 1);
                        return;
                    }

                    //2016-10-12:  Added permissions to bypass being arrested
                    if (getStorageReference.hasPermissions(player, "npcpolice.bypass.arrest"))
                        return;

                    Jail_Setting jailRecord = null;
                    jailRecord = plrRecord.sendPlayerToJail(WANTED_REASONS.BOUNTY);

                    if (jailRecord == null)
                        return;

                    event.setDamage(((Player) damaged).getHealth() - 1);

                    // Process commands for getting jailed
                    try {
                        for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.PLAYER_JAILED, world, plrRecord.currentJail)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, trait, plrRecord, null, jailRecord, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                        }
                    } catch (Exception err) {

                    }

                    // Clear the attack status on the NPC
                    if (getStorageReference.getSentinelPlugin != null) {
                        getStorageReference.getSentinelPlugin.clearTarget(npc, player);
                    }

                    // Send message that player X was arrested to others, then
                    // let the player know they were arrested

                    for (Player onlinePlayer : getStorageReference.pluginInstance.getServer().getOnlinePlayers()) {
                        if (onlinePlayer.getUniqueId().equals(plrRecord.getPlayerUUID())) {
                            // Let the player know they escaped...
                            getStorageReference.getMessageManager.sendMessage(onlinePlayer, "npc_messages.player_jailed", trait, plrRecord);
                        } else {
                            if (!plrRecord.getPlayer().getWorld().equals(onlinePlayer.getWorld()))
                                continue;

                            DistanceDelaySetting distSetting = getStorageReference.getJailManager.getNoticeSetting(NOTICE_SETTING.JAILED, plrRecord.getPlayer().getLocation().getWorld(), plrRecord.currentJail);
                            if (onlinePlayer.getLocation().distanceSquared(plrRecord.getPlayer().getLocation()) < distSetting.getDistanceSquared()) {
                                if (distSetting.getDelay() < 0.001D) {
                                    // send it now
                                    getStorageReference.getMessageManager.sendMessage(onlinePlayer, "npc_messages.broadcast_jailed", plrRecord);
                                } else {
                                    // Delayed sending of this message
                                    final Player targetPlayer = onlinePlayer.getPlayer();
                                    final Arrest_Record playerRecord = plrRecord;

                                    getStorageReference.pluginInstance.getServer().getScheduler().scheduleSyncDelayedTask(
                                            getStorageReference.pluginInstance, new Runnable() {
                                                public void run() {
                                                    getStorageReference.getMessageManager.sendMessage(targetPlayer, "npc_messages.broadcast_jailed", playerRecord);
                                                }
                                            }, (long) (distSetting.getDelay() * 20L)
                                    );
                                }
                            }
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    private void onPlayerVSNPC(Entity damager, Entity damaged, EntityDamageByEntityEvent event) {
        RegionSettings regionFlags = new RegionSettings();
        if (getStorageReference.getWorldGuardPlugin != null)
            regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(damaged.getLocation());

        if (regionFlags != null && regionFlags.noArrest)
            return;

        NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(damaged);
        World world = damaged.getLocation().getWorld();
        NPCPolice_Trait npcTrait = null;
        NPC witnessNPC = null;

        if (npc != null) {
            if (!npc.hasTrait(NPCPolice_Trait.class) && (getStorageReference.getJailManager.getProtectOnlyTraits(world))) {
                // NPC isn't protected by the config
                return;
            }

            if (npc.hasTrait(NPCPolice_Trait.class))
                npcTrait = npc.getTrait(NPCPolice_Trait.class);

            if (npcTrait != null && npcTrait.ignoresAssault) {
                //Configured to not monitor the damage
                return;
            }

            Player player = (Player) damager;
            //2016-10-12:  Added permissions to bypass actions for assaulting npc's
            if (getStorageReference.hasPermissions(player, "npcpolice.bypass.assault.npc"))
                return;

            Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(player.getUniqueId());
            if (plrRecord == null)
                return;

            // Check min damage in config
            if ((new Date().getTime() - plrRecord.getLastWarning().getTime() > 2000) && (event.getDamage() < getStorageReference.getJailManager.getMaxWarningDamage(world)) && ((plrRecord.lastAttack != null && !plrRecord.lastAttack.trim().equals("")) || !plrRecord.lastAttack.equalsIgnoreCase(npc.getName())) && (plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED && plrRecord.getCurrentStatus() != CURRENT_STATUS.WANTED)) {
                // Give a warning to the player
                plrRecord.setLastWarning(new Date());
                plrRecord.lastAttack = npc.getName();
                try {
                    for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.NPC_WARNING, world, plrRecord.currentJail)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                    }
                } catch (Exception err) {

                }
                getStorageReference.getMessageManager.sendMessage(player, "npc_messages.warning_message", npc, plrRecord);

            } else {
                plrRecord.lastAttack = npc.getName();
                Boolean bJailerClose = false;
                if (npc.hasTrait(NPCPolice_Trait.class))
                    npcTrait = npc.getTrait(NPCPolice_Trait.class);

                if (regionFlags.regionGuard) {
                    // Future commands could be called for???
                } else if (npcTrait != null && npcTrait.isGuard) {
                    // Attacking a guard. Adds bounty
                } else {
                    for (Iterator<NPC> npcIter = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().iterator(); npcIter.hasNext(); ) {
                        NPC oTmpNPC = npcIter.next();
                        if ((oTmpNPC != null) && (oTmpNPC != npc) && (oTmpNPC.hasTrait(NPCPolice_Trait.class))) {
                            if (oTmpNPC.isSpawned()) {
                                if (oTmpNPC.getEntity().getLocation().getWorld().getName() == damager.getLocation().getWorld().getName()) {
                                    NPCPolice_Trait trait = oTmpNPC.getTrait(NPCPolice_Trait.class);
                                    int maxDistance = getStorageReference.getJailManager.getMaxDistance(world);
                                    if (npcTrait != null)
                                        maxDistance = getStorageReference.getJailManager.getMaxDistance(world, npcTrait);

                                    maxDistance = maxDistance * maxDistance;

                                    if (oTmpNPC.getEntity().getLocation().distanceSquared(damager.getLocation()) <= maxDistance) {
                                        if (trait.isGuard) {
                                            bJailerClose = true;
                                            witnessNPC = oTmpNPC;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!bJailerClose || regionFlags.monitorAssaults == STATE_SETTING.FALSE) {
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.NPC_NOGUARDS, world, plrRecord.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                            }
                        } catch (Exception err) {

                        }
                        getStorageReference.getMessageManager.sendMessage(player, "npc_messages.broadcast_attack_noguards", npc, npcTrait, plrRecord);
                        return;
                    } else if (!plrRecord.lastAttack.equalsIgnoreCase(npc.getName()) && (plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED)) {
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.NPC_ALERTGUARDS, world, plrRecord.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                            }
                        } catch (Exception err) {

                        }
                        getStorageReference.getMessageManager.sendMessage(player, "npc_messages.broadcast_attack_guards", npc, npcTrait, plrRecord);
                    }
                }

                if (event.getDamage() >= ((LivingEntity) damaged).getHealth() && (regionFlags.monitorMurder == STATE_SETTING.TRUE || regionFlags.monitorMurder == STATE_SETTING.NOTSET)) {

                    //2016-10-12:  Added permissions to bypass actions for assaulting npc's
                    if (getStorageReference.hasPermissions(player, "npcpolice.bypass.murder.npc"))
                        return;

                    // Add npc to list of murder for this player
                    Double newBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.BOUNTY_MURDER, world, npcTrait);
                    Wanted_Information wantedInf = new Wanted_Information(getStorageReference.serverName, witnessNPC, npc, null, WANTED_REASONS.MURDER, newBounty, new Date());
                    plrRecord.addNewWanted(wantedInf);
                    plrRecord.setNewStatus(CURRENT_STATUS.WANTED, WANTED_REASONS.MURDER);
                    plrRecord.changeBounty(JAILED_BOUNTY.BOUNTY_MURDER, newBounty);

                    if (npcTrait != null) {
                        if (npcTrait.timeMurder > -1)
                            plrRecord.changeTime(npcTrait.timeMurder);
                        if (npcTrait.wantedSetting != null && npcTrait.wantedSetting != WANTED_SETTING.NONE)
                            plrRecord.setWantedLevel(npcTrait.wantedSetting, JAILED_BOUNTY.BOUNTY_MURDER);
                        else if (regionFlags.wanted_NPC_Setting != null && regionFlags.wanted_NPC_Setting.ordinal() > plrRecord.getWantedLevel().ordinal())
                            plrRecord.setWantedLevel(regionFlags.wanted_NPC_Setting, JAILED_BOUNTY.BOUNTY_MURDER);
                    } else if (regionFlags.wanted_NPC_Setting != null && regionFlags.wanted_NPC_Setting.ordinal() > plrRecord.getWantedLevel().ordinal()) {
                            plrRecord.setWantedLevel(regionFlags.wanted_NPC_Setting, JAILED_BOUNTY.BOUNTY_MURDER);
                    }

                    try {
                        for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.NPC_MURDERED, world, plrRecord.currentJail)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                        }
                    } catch (Exception err) {

                    }

                    getStorageReference.getMessageManager.sendMessage(player, "npc_messages.player_wanted_for_murder", npc, npcTrait, plrRecord);

                    Core_NPCMurderedEvent murderEvent = new Core_NPCMurderedEvent(getStorageReference, npc, witnessNPC, plrRecord);
                    try { Bukkit.getServer().getPluginManager().callEvent(murderEvent); } catch (Exception err) {}

                } else if (regionFlags.monitorAssaults == STATE_SETTING.TRUE || regionFlags.monitorAssaults == STATE_SETTING.NOTSET || regionFlags.regionGuard) {

                    //2016-10-12:  Added permissions to bypass actions for assaulting npc's
                    if (getStorageReference.hasPermissions(player, "npcpolice.bypass.assault.npc"))
                        return;

                    Double newBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.BOUNTY_DAMAGE, world) * event.getDamage();
                    Wanted_Information wantedInf = new Wanted_Information(getStorageReference.serverName, witnessNPC, npc, null, WANTED_REASONS.ASSAULT, newBounty, new Date());
                    plrRecord.addNewWanted(wantedInf);
                    plrRecord.changeBounty(JAILED_BOUNTY.BOUNTY_DAMAGE, newBounty);

                    if (npcTrait != null && npcTrait.timeAssault > -1)
                        plrRecord.changeTime(npcTrait.timeAssault);

                    if (plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED) {
                        if (getStorageReference.getJailManager.getMinBountyWanted(player.getWorld(), npcTrait) > plrRecord.getBounty())
                            getStorageReference.getMessageManager.sendMessage(player, "npc_messages.player_bounty_tolow", npc, npcTrait, plrRecord);
                        else if (plrRecord.getCurrentStatus() != CURRENT_STATUS.WANTED) {
                            plrRecord.setNewStatus(CURRENT_STATUS.WANTED, WANTED_REASONS.ASSAULT);
                            // Process commands for getting jailed
                            try {
                                for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.PLAYER_WANTED, world, plrRecord.currentJail)) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, null, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), npc, null, 0));
                                }
                            } catch (Exception err) {

                            }
                            getStorageReference.getMessageManager.sendMessage(player, "npc_messages.player_bounty_wanted", npc, npcTrait, plrRecord);
                            plrRecord.lastNotification = new Date();
                        } else {

                        }
                    }
                }

                plrRecord.lastAttack = npc.getName();
                plrRecord.setLastWarning(new Date());

                if (plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED) {

                    if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()).isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                    if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()).isEmpty())
                        getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                    if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()).isEmpty())
                        getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
                }
            }
        }
    }

    private void onPlayervsPlayer(Entity damager, Entity damaged, EntityDamageByEntityEvent event) {
        RegionSettings regionFlags = new RegionSettings();
        if (getStorageReference.getWorldGuardPlugin != null)
            regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(damaged.getLocation());
        World currentWorld = damager.getLocation().getWorld();

        //2016-10-12:  Added permissions to bypass actions for assaulting players
        if (getStorageReference.hasPermissions(damager, "npcpolice.bypass.assault.player"))
            return;

        // PVP
        if (regionFlags.monitorPVP == STATE_SETTING.TRUE || regionFlags.monitorPVP == STATE_SETTING.NOTSET) {
            NPC witnessNPC = null;
            Boolean bJailerClose = false;

            Player player = (Player) damager;
            Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(player.getUniqueId());
            NPCPolice_Trait witnessTrait = null;

            if (regionFlags.regionGuard) {
                bJailerClose = true;
                witnessNPC = null;
                witnessTrait = null;
            } else {

                for (Iterator<NPC> npcIter = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().iterator(); npcIter.hasNext(); ) {
                    NPC oTmpNPC = npcIter.next();
                    if ((oTmpNPC != null) && (oTmpNPC.hasTrait(NPCPolice_Trait.class))) {
                        if (oTmpNPC.isSpawned()) {

                            if (oTmpNPC.getEntity().getLocation().getWorld().getName() == currentWorld.getName()) {
                                NPCPolice_Trait trait = oTmpNPC.getTrait(NPCPolice_Trait.class);
                                int maxDistance = getStorageReference.getJailManager.getMaxDistance(currentWorld, trait);
                                maxDistance = maxDistance * maxDistance;

                                if (oTmpNPC.getEntity().getLocation().distanceSquared(damager.getLocation()) <= maxDistance) {
                                    if (trait.isGuard) {
                                        bJailerClose = true;
                                        witnessNPC = oTmpNPC;
                                        witnessTrait = trait;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!bJailerClose || (bJailerClose && regionFlags.monitorPVP != STATE_SETTING.FALSE && regionFlags.monitorPVP != STATE_SETTING.NOTSET && regionFlags.regionGuard == false)) {
                // No one here cares.
                return;
            }

            Player targetPlayer = (Player) damaged;
            Arrest_Record targetRecord = getStorageReference.getPlayerManager.getPlayer(targetPlayer.getUniqueId());

            if (event.getDamage() >= ((LivingEntity) damaged).getHealth() && (regionFlags.monitorMurder == STATE_SETTING.TRUE || regionFlags.monitorMurder == STATE_SETTING.NOTSET)) {
                //2016-10-12:  Added permissions to bypass actions for PVP murder
                if (getStorageReference.hasPermissions(damager, "npcpolice.bypass.murder.player"))
                    return;

                Double newBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.BOUNTY_MURDER, currentWorld);
                Wanted_Information wantedInf = new Wanted_Information(getStorageReference.serverName, witnessNPC, null, targetPlayer, WANTED_REASONS.MURDER, newBounty, new Date());
                plrRecord.addNewWanted(wantedInf);
                plrRecord.setNewStatus(CURRENT_STATUS.WANTED, WANTED_REASONS.MURDER);
                plrRecord.changeBounty(JAILED_BOUNTY.BOUNTY_MURDER, newBounty);
                getStorageReference.getMessageManager.sendMessage(player, "npc_messages.player_wanted_for_pvpmurder", witnessTrait, plrRecord, targetRecord);

                Core_PlayerMurderedEvent murderEvent = new Core_PlayerMurderedEvent(getStorageReference, targetPlayer,witnessNPC, plrRecord);
                try {Bukkit.getServer().getPluginManager().callEvent(murderEvent);} catch (Exception err) {}

            } else if ((new Date().getTime() - plrRecord.getLastWarning().getTime() > 2000) && (event.getDamage() < getStorageReference.getJailManager.getMaxWarningDamage(currentWorld)) && (plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED)) {
                // Give a warning to the player
                plrRecord.setLastWarning(new Date());
                getStorageReference.getMessageManager.sendMessage(player, "npc_messages.pvp_warning", witnessTrait, plrRecord);
                return;
            } else if (regionFlags.monitorAssaults == STATE_SETTING.TRUE || regionFlags.monitorAssaults == STATE_SETTING.NOTSET) {
                Double newBounty = getStorageReference.getJailManager.getBountySetting(JAILED_BOUNTY.BOUNTY_PVP, currentWorld) * event.getDamage();
                Wanted_Information wantedInf = new Wanted_Information(getStorageReference.serverName, witnessNPC, null, targetPlayer, WANTED_REASONS.ASSAULT, newBounty, new Date());
                plrRecord.addNewWanted(wantedInf);
                plrRecord.changeBounty(JAILED_BOUNTY.BOUNTY_DAMAGE, newBounty);

                if (plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED) {
                    if (getStorageReference.getJailManager.getMinBountyWanted(player.getWorld(), witnessTrait) > plrRecord.getBounty())
                        getStorageReference.getMessageManager.sendMessage(player, "npc_messages.player_bounty_tolow", witnessTrait, plrRecord);
                    else if (plrRecord.getCurrentStatus() != CURRENT_STATUS.WANTED) {
                        plrRecord.setNewStatus(CURRENT_STATUS.WANTED, WANTED_REASONS.ASSAULT);
                        // Process commands for getting jailed
                        try {
                            for (String sMsg : getStorageReference.getJailManager.getProcessedCommands(COMMAND_LISTS.PLAYER_WANTED, currentWorld, plrRecord.currentJail)) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStorageReference.getMessageManager.parseMessage(player, sMsg, null, plrRecord, targetRecord, null, getStorageReference.getJailManager.getWorldSettings(player.getWorld().getName()), witnessNPC, null, 0));
                            }
                        } catch (Exception err) {

                        }
                        getStorageReference.getMessageManager.sendMessage(player, "npc_messages.pvp_wanted", witnessTrait, plrRecord);
                        plrRecord.lastNotification = new Date();
                    }
                }
            }

            plrRecord.setLastWarning(new Date());

            if (plrRecord.getCurrentStatus() != CURRENT_STATUS.ARRESTED && plrRecord.getCurrentStatus() != CURRENT_STATUS.JAILED) {
                if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()).isEmpty())
                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.JAILED, plrRecord.getPlayer().getWorld()));
                if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()).isEmpty())
                    getStorageReference.getPermissionManager.playerAddGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.WANTED, plrRecord.getPlayer().getWorld()));
                if (!getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()).isEmpty())
                    getStorageReference.getPermissionManager.playerRemoveGroup(plrRecord.getPlayer(), getStorageReference.getJailManager.getJailGroup(JAILED_GROUPS.ESCAPED, plrRecord.getPlayer().getWorld()));
            }

        }
    }
}
