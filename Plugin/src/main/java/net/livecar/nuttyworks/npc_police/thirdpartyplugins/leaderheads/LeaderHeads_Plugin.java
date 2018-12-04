package net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("unused")
public class LeaderHeads_Plugin {

    HashMap<String, List<Entry<?, Double>>> dataSets = null;
    private LeaderHeads_RecentArrests lh_RecentArrests = null;
    private LeaderHeads_RecentEscapes lh_RecentEscapes = null;
    private LeaderHeads_MostMurders lh_MostMurders = null;
    private LeaderHeads_MostEscapes lh_MostEscapes = null;
    private LeaderHeads_MostArrests lh_MostArrests = null;
    private LeaderHeads_BountyCurrent lh_BountyCurrent = null;
    private LeaderHeads_BountyTotal lh_BountyTotal = null;
    private NPC_Police getStorageReference = null;
    private int statMonitorID = -1;

    @SuppressWarnings("deprecation")
    public LeaderHeads_Plugin(NPC_Police policeRef) {
        getStorageReference = policeRef;
        dataSets = new HashMap<String, List<Entry<?, Double>>>();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                policeRef.pluginInstance, new BukkitRunnable() {
                    @Override
                    public void run() {
                        onStart();
                    }
                }
        );
    }

    public void setLeaderBoardStats(String statName, List<Entry<?, Double>> resultList) {
        if (dataSets.containsKey(statName))
            dataSets.remove(statName);
        dataSets.put(statName, resultList);
    }

    public void onStart() {
        lh_RecentArrests = new LeaderHeads_RecentArrests(getStorageReference);
        lh_RecentEscapes = new LeaderHeads_RecentEscapes(getStorageReference);
        lh_MostMurders = new LeaderHeads_MostMurders(getStorageReference);
        lh_MostEscapes = new LeaderHeads_MostEscapes(getStorageReference);
        lh_MostArrests = new LeaderHeads_MostArrests(getStorageReference);
        lh_BountyCurrent = new LeaderHeads_BountyCurrent(getStorageReference);
        lh_BountyTotal = new LeaderHeads_BountyTotal(getStorageReference);

        statMonitorID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                getStorageReference.pluginInstance, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getStorageReference.getDatabaseManager.requestUpdatedStats();
                        } catch (Exception e) {
                        }
                    }
                }, 30L, 20 * 30L
        );
    }
}
