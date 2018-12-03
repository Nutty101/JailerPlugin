package net.livecar.nuttyworks.npc_police.api.events;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

public class Core_PlayerPayBountyEvent extends PlayerPayBountyEvent {
    private OfflinePlayer responsiblePlayer;
    private OfflinePlayer payingPlayer;
    private Double jailerBounty;
    private boolean bountyPaid = false;
    private Arrest_Record playerRecord;
    private NPC_Police getStorageReference;

    public Core_PlayerPayBountyEvent(NPC_Police policeRef, OfflinePlayer player, OfflinePlayer payingPlayer, Double jailerBounty, Arrest_Record playerRecord) {
        this.responsiblePlayer = player;
        this.payingPlayer = payingPlayer;
        this.jailerBounty = jailerBounty;
        this.playerRecord = playerRecord;
        this.getStorageReference = policeRef;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return this.responsiblePlayer;
    }

    @Override
    public OfflinePlayer getPayingPlayer() { return this.payingPlayer; }

    @Override
    public double getBounty() {
        return this.jailerBounty;
    }

    @Override
    public boolean getBountyPaid() { return this.bountyPaid; }

    @Override
    public void setBountyPaid(boolean paid) { this.bountyPaid = paid; }

    @Override
    public PlayerManager getPlayerManager() {
        return new Core_PlayerManager(getStorageReference, playerRecord.getPlayerUUID());
    }

}
