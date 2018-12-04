package net.livecar.nuttyworks.npc_police.battlemanagers;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

public interface BattleManager_Interface {
    public boolean meetsMinVersion();

    public String getVersionString();

    public void clearTarget(NPC npc, Player player);

    public void addTarget(NPC npc, Player player);

    public void alertOpToIssues(Player player);
}
