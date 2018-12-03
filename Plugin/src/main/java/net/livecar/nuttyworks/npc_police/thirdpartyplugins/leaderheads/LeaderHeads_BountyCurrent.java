package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.livecar.nuttyworks.npc_police.NPC_Police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeads_BountyCurrent extends DataCollector {
    private NPC_Police getStorageReference = null;

    public LeaderHeads_BountyCurrent(NPC_Police policeRef) {
        super("npstats-bounty", "NPC_Police", BoardType.DEFAULT, "&bHighest Bounty", "npstats-bounty", Arrays.asList(null, null, "{amount}", null), true, String.class);
        getStorageReference = policeRef;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {
        if (getStorageReference.getLeaderHeadsPlugin.dataSets.containsKey("BOUNTIES"))
            return getStorageReference.getLeaderHeadsPlugin.dataSets.get("BOUNTIES");
        else
            return new ArrayList<Entry<?, Double>>();
    }
}
