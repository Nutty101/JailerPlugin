package net.livecar.nuttyworks.npc_police.battlemanagers;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

public abstract class BattleManager_Interface {

    public abstract String getVersionString();

    public abstract void clearTarget(NPC npc, Player player);

    public abstract void addTarget(NPC npc, Player player);

    public abstract boolean alertOpToIssues(Player player);
}
