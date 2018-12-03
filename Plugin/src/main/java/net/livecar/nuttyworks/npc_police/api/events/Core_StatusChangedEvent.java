package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_StatusChangedEvent extends StatusChangedEvent {

    private CURRENT_STATUS jailerStatus;
    private Arrest_Record playerRecord;
    private WANTED_REASONS changeReason;
    private NPC_Police getStorageReference = null;

    public Core_StatusChangedEvent(NPC_Police policeRef, CURRENT_STATUS jailStatus, WANTED_REASONS reason, Arrest_Record playerRecord) {
        this.jailerStatus = jailStatus;
        this.playerRecord = playerRecord;
        this.changeReason = reason;
        this.getStorageReference = policeRef;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
    }

    @Override
    public CURRENT_STATUS getStatus() {
        return this.jailerStatus;
    }

    @Override
    public WANTED_REASONS getReason() {
        return this.changeReason;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
