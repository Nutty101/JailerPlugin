package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.BetonQuest_Interface;
import pl.betoncraft.betonquest.BetonQuest;

public class BetonQuest_Plugin_V1_9 implements BetonQuest_Interface {
    @SuppressWarnings("deprecation")
    public BetonQuest_Plugin_V1_9() {
        String[] versionParts = Bukkit.getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion().split("\\.");

        if (versionParts.length == 3) {
            int verID = Integer.parseInt(versionParts[2]);
            if (verID < 4) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("NPC_Police"), new BukkitRunnable() {
                    public void run() {
                        BetonQuest_Plugin_V1_9.this.onStart();
                    }
                });

            } else {
                BetonQuest_Plugin_V1_9.this.onStart();
            }
        }
    }


    private void onStart() {
        //Create Events
        BetonQuest.getInstance().registerEvents("np-addbounty", Event_AddBounty.class);
        BetonQuest.getInstance().registerEvents("np-arrestplayer", Event_ArrestPlayer.class);
        BetonQuest.getInstance().registerEvents("np-clearbounty", Event_ClearBounty.class);
        BetonQuest.getInstance().registerEvents("np-clearwanted", Event_ClearWanted.class);
        BetonQuest.getInstance().registerEvents("np-clearlockedinv", Event_ClearLockedInventory.class);
        BetonQuest.getInstance().registerEvents("np-returnlockedinv", Event_ReturnLockedInventory.class);
        BetonQuest.getInstance().registerEvents("np-setstatus", Event_SetCurrentStatus.class);

        //Create Objectives
        BetonQuest.getInstance().registerObjectives("np-bountychanged", Objective_BountyChanged.class);
        BetonQuest.getInstance().registerObjectives("np-npcspotted", Objective_NPCSpotted.class);
        BetonQuest.getInstance().registerObjectives("np-statuschanged", Objective_StatusChanged.class);

        //Create Conditions
        BetonQuest.getInstance().registerConditions("np-currentbounty", Condition_CurrentBounty.class);
        BetonQuest.getInstance().registerConditions("np-currentstatus", Condition_CurrentStatus.class);
        BetonQuest.getInstance().registerConditions("np-distancetojail", Condition_DistanceToJail.class);
        BetonQuest.getInstance().registerConditions("np-haslockedinv", Condition_HasLockedInventory.class);
        BetonQuest.getInstance().registerConditions("np-priorstats", Condition_PriorWantedStatus.class);
        BetonQuest.getInstance().registerConditions("np-seclastarrest", Condition_SecondsSinceArrest.class);
        BetonQuest.getInstance().registerConditions("np-seclastescape", Condition_SecondsSinceEscape.class);
    }

}
