package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class Core_NPCMurderedEvent extends NPCMurderedEvent {
    private static final HandlerList handlers = new HandlerList();
    private Arrest_Record playerRecord;
    private NPC targetNPC = null;
    private NPC witnessNPC = null;
    private NPC_Police getStorageReference = null;

    public Core_NPCMurderedEvent(NPC_Police policeRef, NPC targetNPC, NPC witnessNPC, Arrest_Record playerRecord) {
        this.playerRecord = playerRecord;
        this.targetNPC = targetNPC;
        this.witnessNPC = witnessNPC;
        this.getStorageReference = policeRef;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
    }

    @Override
    public NPC getMurderedNPC() {
        return targetNPC;
    }

    @Override
    public NPC getWitnessNPC() {
        return witnessNPC;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
