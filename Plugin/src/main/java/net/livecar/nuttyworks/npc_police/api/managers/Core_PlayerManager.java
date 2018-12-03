package net.livecar.nuttyworks.npc_police.api.managers;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Date;
import java.util.UUID;

public class Core_PlayerManager extends PlayerManager {
    private NPC_Police getStorageReference = null;
    private Arrest_Record playerRecord = null;

    public Core_PlayerManager(NPC_Police policeRef, UUID playerUUID) {
        getStorageReference = policeRef;
        playerRecord = getStorageReference.getPlayerManager.getPlayer(playerUUID);
    }

    @Override
    public CURRENT_STATUS getCurrentStatus() {
        final CURRENT_STATUS currentStatus = playerRecord.getCurrentStatus();
        return currentStatus;
    }

    @Override
    public void setCurrentStatus(CURRENT_STATUS newStatus) {
        playerRecord.setNewStatus(newStatus, WANTED_REASONS.PLUGIN);
    }

    @Override
    public CURRENT_STATUS getPriorStatus() {
        final CURRENT_STATUS priorStatus = playerRecord.getPriorStatus();
        return priorStatus;
    }

    @Override
    public Double getCurrentBounty() {
        final Double currentBounty = playerRecord.getBounty();
        return currentBounty;
    }

    @Override
    public void changeBounty(Double newBounty) {
        playerRecord.changeBounty(JAILED_BOUNTY.PLUGIN, newBounty);
    }

    @Override
    public void clearBounty() {
        playerRecord.setBounty(0.0D);
    }

    @Override
    public void changeTime(int seconds) {
        playerRecord.changeTime(seconds);
    }

    @Override
    public int getTime() {
        return playerRecord.getTime();
    }

    @Override
    public void setTime(int seconds) {
        playerRecord.setTime(seconds);
    }

    @Override
    public void clearWanted() {
        playerRecord.clearWanted();
    }

    @Override
    public void addWantedReason(Wanted_Information wantedInfo) {
        playerRecord.addNewWanted(wantedInfo);
    }

    @Override
    public Date getLastArrest() {
        return playerRecord.getLastArrest();
    }

    @Override
    public Date getLastEscape() {
        return playerRecord.getLastEscape();
    }

    @Override
    public void clearLockedInventory() {
        playerRecord.clearLockedInventory();
    }

    @Override
    public void returnLockedInventory() {
        playerRecord.returnLockedInventory();
    }

    @Override
    public boolean hasLockedInventory() {
        return playerRecord.hasLockedInventory();
    }

    @Override
    public Double distanceToJail() {
        if (!playerRecord.isOnline())
            return Double.MAX_VALUE;

        Player plr = playerRecord.getPlayer();
        double lowestDistance = Double.MAX_VALUE;

        if (!getStorageReference.getJailManager.containsWorld(plr.getWorld().getName())) {
            return Double.MAX_VALUE;
        }

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plr.getWorld().getName())) {
            for (Location cellLocation : jailSetting.cellLocations) {
                if (plr.getLocation().distanceSquared(cellLocation) < lowestDistance) {
                    lowestDistance = plr.getLocation().distanceSquared(cellLocation);
                }
            }
        }

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plr.getWorld().getName())) {
            for (Location cellLocation : jailSetting.cellLocations) {
                if (plr.getLocation().distanceSquared(cellLocation) <= lowestDistance) {
                    return plr.getLocation().distance(cellLocation);
                }
            }
        }

        return Double.MAX_VALUE;
    }

    @Override
    public void arrestPlayer() {
        if (!playerRecord.isOnline())
            return;

        Player plr = playerRecord.getPlayer();
        double lowestDistance = Double.MAX_VALUE;

        if (!getStorageReference.getJailManager.containsWorld(plr.getWorld().getName())) {
            return;
        }

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plr.getWorld().getName())) {
            for (Location cellLocation : jailSetting.cellLocations) {
                if (plr.getLocation().distanceSquared(cellLocation) < lowestDistance) {
                    lowestDistance = plr.getLocation().distanceSquared(cellLocation);
                }
            }
        }

        for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(plr.getWorld().getName())) {
            for (Location cellLocation : jailSetting.cellLocations) {
                if (plr.getLocation().distanceSquared(cellLocation) <= lowestDistance) {
                    // Send the player here
                    playerRecord.setNewStatus(CURRENT_STATUS.JAILED, WANTED_REASONS.PLUGIN);
                    playerRecord.clearWanted();
                    plr.teleport(cellLocation, TeleportCause.PLUGIN);
                }
            }
        }
    }

    @Override
    public void releasePlayer() {
        if (!playerRecord.isOnline())
            return;

        if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED)
            playerRecord.releasePlayer();
    }

    @Override
    public Double getBountyPerSecond() {
        return playerRecord.getBountyPerSecond();
    }

    @Override
    public Enumerations.WANTED_LEVEL getWantedLevel() {
        return playerRecord.getWantedLevel();
    }

    @Override
    public void setWantedLevel(Enumerations.WANTED_SETTING wantedLevel, JAILED_BOUNTY reason) {
        playerRecord.setWantedLevel(wantedLevel, reason);
    }

    @Override
    public void setWantedLevel(Enumerations.WANTED_SETTING wantedLevel, JAILED_BOUNTY reason, boolean report) {
        playerRecord.setWantedLevel(wantedLevel, reason, report);
    }

    @Override
    public void setWantedLevelForced(Enumerations.WANTED_SETTING wantedLevel, JAILED_BOUNTY reason, boolean report) {
        playerRecord.setWantedLevelForced(wantedLevel, reason, report);
    }


}
