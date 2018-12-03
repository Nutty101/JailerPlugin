package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.livecar.nuttyworks.npc_police.NPC_Police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeads_BountyTotal extends DataCollector {
    private NPC_Police getStorageReference = null;

    public LeaderHeads_BountyTotal(NPC_Police policeRef) {
        super("npstats-ttlbounty", "NPC_Police", BoardType.DEFAULT, "&bLifetime Bounties", "npstats-ttlbounty", Arrays.asList(null, null, "{amount}", null), true, String.class);
        getStorageReference = policeRef;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {
        if (getStorageReference.getLeaderHeadsPlugin.dataSets.containsKey("TOTALBOUNTIES"))
            return getStorageReference.getLeaderHeadsPlugin.dataSets.get("TOTALBOUNTIES");
        else
            return new ArrayList<Entry<?, Double>>();
    }
}
