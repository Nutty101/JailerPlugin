package net.livecar.nuttyworks.npc_police.thirdpartyplugins.jobs_reborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class JobsReborn_Plugin {

    private NPC_Police getStorageReference = null;

    @SuppressWarnings("deprecation")
    public JobsReborn_Plugin(NPC_Police policeRef) {
        getStorageReference = policeRef;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                getStorageReference.pluginInstance, new BukkitRunnable() {
                    @Override
                    public void run() {
                        onStart();
                    }
                }
        );
    }

    private void onStart() {
        // Not listening at this moment for the events. Leaving this empty.

    }

    public int JobCount(String jobName) {
        if (JobExists(jobName)) {
            return Jobs.getJob(jobName).getTotalPlayers();
        } else {
            return -1;
        }
    }

    public boolean JobAtMax(String jobName) {
        if (JobExists(jobName)) {
            return (Jobs.getJob(jobName).getTotalPlayers() >= Jobs.getJob(jobName).getMaxSlots());
        } else {
            return true;
        }
    }

    public boolean JobExists(String jobName) {

        for (Job oJob : Jobs.getJobs()) {
            if (oJob.getName().equalsIgnoreCase(jobName))
                return true;
        }
        ;
        return false;
    }

    public boolean playerHasPoliceJob(OfflinePlayer player) {

        JobsPlayer playerJobs = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());

        for (Job oJob : Jobs.getJobs()) {
            for (JobInfo jobInfo : oJob.getJobInfo(ActionType.CUSTOMKILL))
            {
                if (jobInfo.isInLevelRange(1))
                {

                }
            }
        }




        return false;
    }

}
