package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.livecar.nuttyworks.npc_police.NPC_Police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeads_MostEscapes extends DataCollector {
    private NPC_Police getStorageReference = null;

    public LeaderHeads_MostEscapes(NPC_Police policeRef) {
        super("npstats-escapes", "NPC_Police", BoardType.DEFAULT, "&bMost Escapes", "npstats-escapes", Arrays.asList(null, null, "{amount}", null), true, String.class);
        getStorageReference = policeRef;
    }


    @Override
    public List<Entry<?, Double>> requestAll() {
        if (getStorageReference.getLeaderHeadsPlugin.dataSets.containsKey("MOSTESCAPES"))
            return getStorageReference.getLeaderHeadsPlugin.dataSets.get("MOSTESCAPES");
        else
            return new ArrayList<Entry<?, Double>>();
    }
}