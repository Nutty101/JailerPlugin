package net.livecar.nuttyworks.npc_police.thirdpartyplugins.sentinel;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.battlemanagers.BattleManager_Interface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelCurrentTarget;
import org.mcmonkey.sentinel.SentinelPlugin;
import org.mcmonkey.sentinel.SentinelTrait;

public class Sentinel_Plugin implements BattleManager_Interface {
    public int[] version;
    private NPC_Police getStorageReference;

    @SuppressWarnings("deprecation")
    public Sentinel_Plugin(NPC_Police policeRef) {
        getStorageReference = policeRef;

        version = new int[]{0, 0, 0, 0};

        String verString = Bukkit.getServer().getPluginManager().getPlugin("Sentinel").getDescription().getVersion();
        if (verString.contains(" ")) {
            verString = verString.substring(0, verString.indexOf(" "));
        }

        String[] versionSplit = verString.split("\\.");

        if (versionSplit.length > 0 && getStorageReference.getUtilities.isNumeric(versionSplit[0]))
            version[0] = Integer.parseInt(versionSplit[0]);

        if (versionSplit.length > 1 && getStorageReference.getUtilities.isNumeric(versionSplit[1]))
            version[1] = Integer.parseInt(versionSplit[1]);

        if (versionSplit.length > 2 && getStorageReference.getUtilities.isNumeric(versionSplit[2]))
            version[2] = Integer.parseInt(versionSplit[2]);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                getStorageReference.pluginInstance, new BukkitRunnable() {
                    @Override
                    public void run() {
                        onStart();
                    }
                }
        );
    }

    public boolean meetsMinVersion() {
        if (version[0] == 0 && version[1] < 7)
            return false;
        return true;
    }

    public String getVersionString() {
        return Bukkit.getServer().getPluginManager().getPlugin("Sentinel").getDescription().getVersion();
    }

    public void clearTarget(NPC npc, Player player) {
        if (!npc.hasTrait(SentinelTrait.class)) {
            return;
        }

        SentinelTrait sentTrait = npc.getTrait(SentinelTrait.class);
        if (sentTrait == null)
            return;

        SentinelCurrentTarget target = new SentinelCurrentTarget();
        target.targetID = player.getUniqueId();
        if (sentTrait.currentTargets.contains(target)) {
            sentTrait.currentTargets.remove(target);
            sentTrait.chasing = null;
            npc.getNavigator().cancelNavigation();
        }
    }

    public void addTarget(NPC npc, Player player) {
        if (!npc.hasTrait(SentinelTrait.class)) {
            return;
        }

        SentinelTrait sentTrait = npc.getTrait(SentinelTrait.class);
        if (sentTrait == null)
            return;

        SentinelCurrentTarget target = new SentinelCurrentTarget();
        target.targetID = player.getUniqueId();
        if (!sentTrait.currentTargets.contains(target)) {
            sentTrait.currentTargets.add(target);
        }
    }

    private void onStart() {

    }

    @SuppressWarnings("deprecation")
    public void alertOpToIssues(Player player) {
        boolean enforceSet = SentinelPlugin.instance.getConfig().getBoolean("random.enforce damage", false);
        boolean workaround = SentinelPlugin.instance.getConfig().getBoolean("random.workaround damage", false);

        if (enforceSet || workaround) {
            final Player plr = player;
            Bukkit.getServer().getScheduler().runTaskLater(
                    getStorageReference.pluginInstance, new BukkitRunnable() {
                        @Override
                        public void run() {
                            getStorageReference.getMessageManager.sendMessage(plr, "general_messages.sentinel_issue");
                        }
                    }, 10
            );
        }
    }
}
