package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.livecar.nuttyworks.npc_police.NPC_Police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeads_MostMurders extends DataCollector {
    private NPC_Police getStorageReference = null;

    public LeaderHeads_MostMurders(NPC_Police policeRef) {
        super("npstats-murders", "NPC_Police", BoardType.DEFAULT, "&bMost Murders", "npstats-murders", Arrays.asList(null, null, "{amount}", null), true, String.class);
        getStorageReference = policeRef;
    }


    @Override
    public List<Entry<?, Double>> requestAll() {
        if (getStorageReference.getLeaderHeadsPlugin.dataSets.containsKey("MOSTMURDERS"))
            return getStorageReference.getLeaderHeadsPlugin.dataSets.get("MOSTMURDERS");
        else
            return new ArrayList<Entry<?, Double>>();
    }
}