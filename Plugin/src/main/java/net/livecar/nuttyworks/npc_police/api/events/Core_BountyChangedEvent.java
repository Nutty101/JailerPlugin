package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_BountyChangedEvent extends BountyChangedEvent {
    private Double jailerBounty;
    private JAILED_BOUNTY bountyReason;
    private Arrest_Record playerRecord;
    private NPC_Police getStorageReference = null;

    public Core_BountyChangedEvent(NPC_Police policeRef, double jailerBounty, JAILED_BOUNTY reason, Arrest_Record playerRecord) {
        this.jailerBounty = jailerBounty;
        this.playerRecord = playerRecord;
        this.getStorageReference = policeRef;
        this.bountyReason = reason;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return this.playerRecord.getOfflinePlayer();
    }

    @Override
    public double getBounty() {
        return this.jailerBounty;
    }

    @Override
    public JAILED_BOUNTY getBountyReason() {
        return bountyReason;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
