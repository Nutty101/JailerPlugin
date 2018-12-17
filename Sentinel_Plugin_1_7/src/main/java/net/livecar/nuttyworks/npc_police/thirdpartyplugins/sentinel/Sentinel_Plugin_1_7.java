package net.livecar.nuttyworks.npc_police.thirdpartyplugins.sentinel;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.battlemanagers.BattleManager_Interface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelCurrentTarget;
import org.mcmonkey.sentinel.SentinelPlugin;
import org.mcmonkey.sentinel.SentinelTrait;

public class Sentinel_Plugin_1_7 extends BattleManager_Interface {
    public int[] version;

    static public boolean isValidVersion()
    {
        try {
            Class.forName("org.mcmonkey.sentinel.targeting.SentinelTargetingHelper");
            return true;
        } catch (Exception e) {
            return false;
        }
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
        if (sentTrait.targetingHelper.currentTargets.contains(target)) {
            sentTrait.targetingHelper.currentTargets.remove(target);
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
        if (!sentTrait.targetingHelper.currentTargets.contains(target)) {
            sentTrait.targetingHelper.currentTargets.add(target);
        }
    }

    public boolean alertOpToIssues(Player player) {
        boolean enforceSet = SentinelPlugin.instance.getConfig().getBoolean("random.enforce damage", false);
        boolean workaround = SentinelPlugin.instance.getConfig().getBoolean("random.workaround damage", false);

        if (enforceSet || workaround) {
            return true;
        }
        return false;
    }
}
