package net.livecar.nuttyworks.npc_police.api;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Wanted_Information {
    private WANTED_REASONS wantedReason = null;
    private String pluginReason = null;
    private UUID playerTargeted = null;
    private NPC npcAttacked = null;
    private String attackedName = "";
    private NPC npcWitness = null;
    private String witnessName = "";
    private Double bountyWorth = 0.0D;
    private String serverName = "";
    private List<Date> offenseDate = null;

    public Wanted_Information(String server, NPC witness, NPC npcTarget, Player playerTarget, WANTED_REASONS reason, Double bountyAdded, Date dateOfOffense) {

        if (witness != null) {
            witnessName = witness.getName();
            npcWitness = witness;
        }
        if (npcTarget != null) {
            npcAttacked = npcTarget;
            attackedName = npcTarget.getName();
        }
        if (playerTarget != null) {
            playerTargeted = playerTarget.getUniqueId();
            attackedName = playerTarget.getName();
        }
        serverName = server;
        wantedReason = reason;
        bountyWorth = bountyAdded;
        offenseDate = new ArrayList<Date>();
        offenseDate.add(dateOfOffense);
    }

    public Wanted_Information(String server, NPC witness, NPC npcTarget, Player playerTarget, String reason, Double bountyAdded, Date dateOfOffense) {
        if (witness != null) {
            witnessName = witness.getName();
            npcWitness = witness;
        }
        if (npcTarget != null) {
            npcAttacked = npcTarget;
            attackedName = npcTarget.getName();
        }
        if (playerTarget != null) {
            playerTargeted = playerTarget.getUniqueId();
            attackedName = playerTarget.getName();
        }
        serverName = server;
        bountyWorth = bountyAdded;
        wantedReason = WANTED_REASONS.PLUGIN;
        pluginReason = reason;
        offenseDate = new ArrayList<Date>();
        offenseDate.add(dateOfOffense);
    }

    public Wanted_Information(String witness, String attacked, String server, String reason, Double bountyAdded, Date dateOfOffense, int offenseCount) {
        witnessName = witness;
        attackedName = attacked;
        serverName = server;
        wantedReason = WANTED_REASONS.valueOf(reason);
        bountyWorth = bountyAdded;
        offenseDate = new ArrayList<Date>();
        for (int nCnt = 0; nCnt < offenseCount; nCnt++)
            offenseDate.add(dateOfOffense);
    }

    public NPC getWitness() {
        return npcWitness;
    }

    public String getWitnessName() {
        return this.witnessName;
    }

    public String getServer() {
        return serverName;
    }

    public boolean wasNPCAttacked() {
        if (npcAttacked != null)
            return true;
        return false;
    }

    public String getAttackedName() {
        return this.attackedName;
    }

    public NPC getRelatedNPC() {
        return npcAttacked;
    }

    public OfflinePlayer getRelatedPlayer() {
        return Bukkit.getOfflinePlayer(playerTargeted);
    }

    public UUID getRelatedPlayerUUID() {
        return playerTargeted;
    }

    public String getWantedReason() {
        if (wantedReason == WANTED_REASONS.PLUGIN)
            return pluginReason;
        return wantedReason.toString();
    }

    public WANTED_REASONS getWantedReasonEnum() {
        return wantedReason;
    }

    public Date getFirstOffenseDate() {
        Date lowestDate = new Date(Long.MAX_VALUE);
        for (Date offense : offenseDate)
            if (lowestDate.getTime() > offense.getTime())
                lowestDate = offense;

        return lowestDate;
    }

    public Date getLastOffenseDate() {
        Date highestDate = new Date(0);
        for (Date offense : offenseDate)
            if (highestDate.getTime() < offense.getTime())
                highestDate = offense;
        return highestDate;
    }

    public List<Date> getAllOffenseDates() {
        return offenseDate;
    }

    public int getOffenseCount() {
        return offenseDate.size();
    }

    public Double getBountyValue() {
        return bountyWorth;
    }

    public void addOffense(Date date, Double bounty) {
        this.offenseDate.add(date);
        this.bountyWorth += bounty;
    }
}
