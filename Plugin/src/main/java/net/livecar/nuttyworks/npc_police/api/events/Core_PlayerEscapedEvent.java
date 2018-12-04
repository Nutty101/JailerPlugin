package net.livecar.nuttyworks.npc_police.api.events;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_PlayerEscapedEvent extends PlayerEscapedEvent {
    private Arrest_Record playerRecord;
    private String jailName;
    private NPC_Police getStorageReference ;

    public Core_PlayerEscapedEvent(NPC_Police policeRef, String jailName, Arrest_Record playerRecord) {
        this.playerRecord = playerRecord;
        this.getStorageReference = policeRef;
        this.jailName = jailName;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return playerRecord.getOfflinePlayer();
    }

    @Override
    public String getEscapedJail() {
        return this.jailName;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
