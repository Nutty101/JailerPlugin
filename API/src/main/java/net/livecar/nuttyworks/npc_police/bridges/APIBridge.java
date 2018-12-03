package net.livecar.nuttyworks.npc_police.bridges;

import net.livecar.nuttyworks.npc_police.api.InvalidJailException;
import net.livecar.nuttyworks.npc_police.api.InvalidWorldException;
import net.livecar.nuttyworks.npc_police.api.managers.JailManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public abstract class APIBridge {
    abstract public PlayerManager getPlayerManager(OfflinePlayer player);

    abstract public List<String> getWorldJailNames(World world) throws InvalidWorldException;

    abstract public JailManager getJailManager(String jailName) throws InvalidJailException;

    abstract public JailManager getJailManager(Location jailLocation) throws InvalidJailException;

    abstract public void disableMonitoringDamage(boolean disable);

    abstract public void processCustomDamageEvent(EntityDamageByEntityEvent event);

    abstract public RegionSettings getRegionSettings(Location location);
}
