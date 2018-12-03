package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_NPCAssaultEvent extends NPCAssaultEvent {
    private Arrest_Record playerRecord;
    private NPC targetNPC = null;
    private NPC witnessNPC = null;
    private int bountyAmount = 0;
    private NPC_Police getStorageReference = null;

    public Core_NPCAssaultEvent(NPC_Police policeRef, NPC targetNPC, NPC witnessNPC, int bountyAmount, Arrest_Record playerRecord) {
        this.playerRecord = playerRecord;
        this.targetNPC = targetNPC;
        this.witnessNPC = witnessNPC;
        this.bountyAmount = bountyAmount;
        this.getStorageReference = policeRef;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
    }

    @Override
    public NPC getAttackedNPC() {
        return targetNPC;
    }

    @Override
    public NPC getWitnessNPC() {
        return witnessNPC;
    }

    @Override
    public int getBountyAmount() {
        return bountyAmount;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
