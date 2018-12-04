package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_PlayerMurderedEvent extends PlayerMurderedEvent {
    private Arrest_Record playerRecord;
    private OfflinePlayer targetPlayer;
    private NPC witnessNPC;
    private NPC_Police getStorageReference ;

    public Core_PlayerMurderedEvent(NPC_Police policeRef, OfflinePlayer targetPlayer, NPC witnessNPC, Arrest_Record playerRecord) {
        this.playerRecord = playerRecord;
        this.targetPlayer = targetPlayer;
        this.getStorageReference = policeRef;
        this.witnessNPC = witnessNPC;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
    }

    @Override
    public OfflinePlayer getMurderedPlayer() {
        return targetPlayer;
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
