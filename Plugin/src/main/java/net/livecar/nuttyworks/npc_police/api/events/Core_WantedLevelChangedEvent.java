package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class Core_WantedLevelChangedEvent extends WantedLevelChangedEvent {

    private static final HandlerList handlers = new HandlerList();
    private WANTED_LEVEL wantedLevel;
    private JAILED_BOUNTY wantedReason;
    private Arrest_Record playerRecord;
    private NPC_Police getStorageReference;
    private boolean forced;

    public Core_WantedLevelChangedEvent(NPC_Police policeRef, WANTED_LEVEL wantedLevel, JAILED_BOUNTY reason, Arrest_Record playerRecord, boolean forced) {
        this.wantedLevel = wantedLevel;
        this.playerRecord = playerRecord;
        this.getStorageReference = policeRef;
        this.wantedReason = reason;
        this.forced = forced;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getPlayer();
    }

    @Override
    public WANTED_LEVEL getLevel() {
        return this.wantedLevel;
    }

    @Override
    public JAILED_BOUNTY getWantedReason() {
        return wantedReason;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

    @Override
    public boolean isForced() {
        return forced;
    }

}
