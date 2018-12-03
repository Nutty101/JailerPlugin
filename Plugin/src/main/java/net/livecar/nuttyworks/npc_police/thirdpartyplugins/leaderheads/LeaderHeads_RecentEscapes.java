package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.livecar.nuttyworks.npc_police.NPC_Police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeads_RecentEscapes extends DataCollector {
    private NPC_Police getStorageReference = null;

    public LeaderHeads_RecentEscapes(NPC_Police policeRef) {
        super("npstats-rescapes", "NPC_Police", BoardType.TIME, "&bRecent Escapes", "jailer-rescapes", Arrays.asList(null, null, "{time}", null), true, String.class);
        getStorageReference = policeRef;
    }


    @Override
    public List<Entry<?, Double>> requestAll() {
        if (getStorageReference.getLeaderHeadsPlugin.dataSets.containsKey("LASTESCAPES"))
            return getStorageReference.getLeaderHeadsPlugin.dataSets.get("LASTESCAPES");
        else
            return new ArrayList<Entry<?, Double>>();
    }
}