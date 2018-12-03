package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_PlayerSpottedEvent extends PlayerSpottedEvent {

    private Arrest_Record playerRecord;
    private NPC witnessNPC = null;
    private NPC_Police getStorageReference = null;

    public Core_PlayerSpottedEvent(NPC_Police policeRef, NPC witnessNPC, Arrest_Record playerRecord) {
        this.playerRecord = playerRecord;
        this.witnessNPC = witnessNPC;
        this.getStorageReference = policeRef;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
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
