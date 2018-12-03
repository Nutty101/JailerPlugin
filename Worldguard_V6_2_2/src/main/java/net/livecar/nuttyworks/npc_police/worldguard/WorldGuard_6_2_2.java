package net.livecar.nuttyworks.npc_police.worldguard;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.livecar.nuttyworks.npc_police.api.Enumerations.KICK_TYPE;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WorldGuard_6_2_2 extends VersionBridge implements Listener {
    private WorldGuardPlugin getWorldGuardPlugin;

    public WorldGuard_6_2_2() {
        getWorldGuardPlugin = WGBukkit.getPlugin();
    }

    @Override
    public void registerFlags() {
        getWorldGuardPlugin.getFlagRegistry().register(CELL_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(ARREST_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(PVP_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(MURDER_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(ASSAULT_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(EXTENDEDJAIL_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(REGIONGUARD_FLAG);

        getWorldGuardPlugin.getFlagRegistry().register(AUTOFLAG_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(AUTOFLAG_BOUNTY_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(AUTOFLAG_BOUNTY_COOLDOWN);
        getWorldGuardPlugin.getFlagRegistry().register(AUTOFLAG_GUARDSIGHT_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(AUTOFLAG_CAUGHT_FLAG);

        // 2.1.1 -- Wanted addition
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_DENYMIN_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_DENYMAX_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_NPC_SETTING_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_KICK_TYPE_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_KICK_LOCATION_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_CHANGE_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(WANTED_FORCED_FLAG);

        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_DAMAGE_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_PVP_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_MURDER_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_ESCAPED_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_WANTED_FLAG);
        getWorldGuardPlugin.getFlagRegistry().register(BOUNTY_MAXIMUM_FLAG);

    }

    @Override
    public void registerHandlers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getWorldGuardPlugin.getSessionManager().registerHandler(WorldGuard_6_RegionalHandler.FACTORY, null);
            }
        }.runTask(Bukkit.getServer().getPluginManager().getPlugin("NPC_Police"));
    }

    @Override
    public void unregisterFlags() {
        // WGBukkit.getPlugin().getSessionManager().unregisterHandler(WG_ChunkFlag.FACTORY);
    }

    @Override
    public String getCurrentRegion(Location loc) {
        Vector v = new Vector(loc.getX(), loc.getBlockY(), loc.getZ());
        return getWorldGuardPlugin.getRegionManager(loc.getWorld()).getApplicableRegionsIDs(v).get(0);
    }

    @Override
    public List<String> getCurrentRegions(Location loc) {
        Vector v = new Vector(loc.getX(), loc.getBlockY(), loc.getZ());
        return getWorldGuardPlugin.getRegionManager(loc.getWorld()).getApplicableRegionsIDs(v);
    }

    @Override
    public List<String> getWorldRegions(World world) {

        List<String> regionList = new ArrayList<String>();
        regionList.addAll(getWorldGuardPlugin.getRegionManager(world).getRegions().keySet());
        return regionList;
    }

    @Override
    public Location[] getRegionBounds(World world, String regionName) {
        ProtectedRegion boundRegion = getWorldGuardPlugin.getRegionManager(world).getRegion(regionName);
        if (boundRegion == null)
            return new Location[0];

        Location[] boundLocs = new Location[2];
        boundLocs[0] = new Location(world, boundRegion.getMinimumPoint().getBlockX(), boundRegion.getMinimumPoint().getBlockY(), boundRegion.getMinimumPoint().getBlockZ());
        boundLocs[1] = new Location(world, boundRegion.getMaximumPoint().getBlockX(), boundRegion.getMaximumPoint().getBlockY(), boundRegion.getMaximumPoint().getBlockZ());
        boundRegion = null;

        return boundLocs;
    }

    @Override
    public boolean isInRegion(Location loc, String region) {
        Vector v = new Vector(loc.getX(), loc.getBlockY(), loc.getZ());
        return getWorldGuardPlugin.getRegionManager(loc.getWorld()).getApplicableRegionsIDs(v).contains(region);
    }

    @Override
    public boolean isInCell(Location loc) {
        for (String regionName : getCurrentRegions(loc)) {
            if (getWorldGuardPlugin.getRegionManager(loc.getWorld()).getRegion(regionName).getFlag(CELL_FLAG).booleanValue())
                return true;
        }
        return false;
    }

    @Override
    public boolean regionArrestable(Location loc) {
        for (String regionName : getCurrentRegions(loc)) {
            if (getWorldGuardPlugin.getRegionManager(loc.getWorld()).getRegion(regionName).getFlag(CELL_FLAG).booleanValue())
                return true;
        }
        return false;
    }

    @Override
    public RegionSettings getRelatedRegionFlags(Location loc) {
        RegionSettings regionFlags = new RegionSettings();
        List<String> regionList = getCurrentRegions(loc);
        regionList.add("__global__");
        for (String regionName : regionList) {

            if (regionName == null || regionName.isEmpty())
                continue;

            ProtectedRegion localRegion = getWorldGuardPlugin.getRegionManager(loc.getWorld()).getRegion(regionName);

            if (localRegion == null)
                continue;

            if (regionFlags.regionName == null || regionFlags.regionName.trim().equals(""))
                regionFlags.regionName = regionName;


            if (localRegion.getFlag(AUTOFLAG_FLAG) != null)
                regionFlags.region_AutoFlagStatus = localRegion.getFlag(AUTOFLAG_FLAG);

            if (localRegion.getFlag(AUTOFLAG_BOUNTY_FLAG) != null)
                regionFlags.autoFlag_Bounty = localRegion.getFlag(AUTOFLAG_BOUNTY_FLAG);
            if (localRegion.getFlag(AUTOFLAG_BOUNTY_COOLDOWN) != null) {
                regionFlags.regionName = regionName;
                regionFlags.autoFlag_CoolDown = localRegion.getFlag(AUTOFLAG_BOUNTY_COOLDOWN);
            }

            if (localRegion.getFlag(AUTOFLAG_GUARDSIGHT_FLAG) != null)
                regionFlags.autoFlag_RequiresSight = localRegion.getFlag(AUTOFLAG_GUARDSIGHT_FLAG) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
            if (localRegion.getFlag(AUTOFLAG_CAUGHT_FLAG) != null)
                regionFlags.autoFlag_CaughtNotice = localRegion.getFlag(AUTOFLAG_CAUGHT_FLAG);
            if (localRegion.getFlag(EXTENDEDJAIL_FLAG) != null)
                regionFlags.extendsJail = localRegion.getFlag(EXTENDEDJAIL_FLAG);

            if (localRegion.getFlag(REGIONGUARD_FLAG) != null)
                regionFlags.regionGuard = localRegion.getFlag(REGIONGUARD_FLAG);
            if (localRegion.getFlag(CELL_FLAG) != null)
                regionFlags.isCell = localRegion.getFlag(CELL_FLAG);
            if (localRegion.getFlag(ARREST_FLAG) != null)
                regionFlags.noArrest = localRegion.getFlag(ARREST_FLAG);
            if (localRegion.getFlag(PVP_FLAG) != null)
                regionFlags.monitorPVP = localRegion.getFlag(PVP_FLAG) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
            if (localRegion.getFlag(MURDER_FLAG) != null)
                regionFlags.monitorMurder = localRegion.getFlag(MURDER_FLAG) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
            if (localRegion.getFlag(ASSAULT_FLAG) != null)
                regionFlags.monitorAssaults = localRegion.getFlag(ASSAULT_FLAG) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;

            if (localRegion.getFlag(BOUNTY_DAMAGE_FLAG) != null)
                regionFlags.bounty_Damage = localRegion.getFlag(BOUNTY_DAMAGE_FLAG);
            if (localRegion.getFlag(BOUNTY_PVP_FLAG) != null)
                regionFlags.bounty_PVP = localRegion.getFlag(BOUNTY_PVP_FLAG);
            if (localRegion.getFlag(BOUNTY_MURDER_FLAG) != null)
                regionFlags.bounty_Murder = localRegion.getFlag(BOUNTY_MURDER_FLAG);
            if (localRegion.getFlag(BOUNTY_ESCAPED_FLAG) != null)
                regionFlags.bounty_Escaped = localRegion.getFlag(BOUNTY_ESCAPED_FLAG);
            if (localRegion.getFlag(BOUNTY_WANTED_FLAG) != null)
                regionFlags.bounty_Wanted = localRegion.getFlag(BOUNTY_WANTED_FLAG);
            if (localRegion.getFlag(BOUNTY_MAXIMUM_FLAG) != null)
                regionFlags.bounty_Maximum = localRegion.getFlag(BOUNTY_MAXIMUM_FLAG);

            //2.1.1+
            if (localRegion.getFlag(WANTED_DENYMIN_FLAG) != null) {
                if (WANTED_SETTING.contains(localRegion.getFlag(WANTED_DENYMIN_FLAG))) {
                    regionFlags.wanted_DenyMin = WANTED_SETTING.valueOf(localRegion.getFlag(WANTED_DENYMIN_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_DENYMAX_FLAG) != null) {
                if (WANTED_SETTING.contains(localRegion.getFlag(WANTED_DENYMAX_FLAG))) {
                    regionFlags.wanted_DenyMax = WANTED_SETTING.valueOf(localRegion.getFlag(WANTED_DENYMAX_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_NPC_SETTING_FLAG) != null) {
                if (WANTED_SETTING.contains(localRegion.getFlag(WANTED_NPC_SETTING_FLAG))) {
                    regionFlags.wanted_NPC_Setting = WANTED_SETTING.valueOf(localRegion.getFlag(WANTED_NPC_SETTING_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_CHANGE_FLAG) != null) {
                if (WANTED_SETTING.contains(localRegion.getFlag(WANTED_CHANGE_FLAG))) {
                    regionFlags.wanted_Change = WANTED_SETTING.valueOf(localRegion.getFlag(WANTED_CHANGE_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_FORCED_FLAG) != null) {
                if (WANTED_SETTING.contains(localRegion.getFlag(WANTED_FORCED_FLAG))) {
                    regionFlags.wanted_Forced = WANTED_SETTING.valueOf(localRegion.getFlag(WANTED_FORCED_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_KICK_TYPE_FLAG) != null) {
                if (KICK_TYPE.contains(localRegion.getFlag(WANTED_KICK_TYPE_FLAG))) {
                    regionFlags.wanted_Kick_Type = KICK_TYPE.valueOf(localRegion.getFlag(WANTED_KICK_TYPE_FLAG).toUpperCase());
                }
            }

            if (localRegion.getFlag(WANTED_KICK_LOCATION_FLAG) != null) {
                regionFlags.wanted_Kick_Location = localRegion.getFlag(WANTED_KICK_LOCATION_FLAG);
            }

        }
        return regionFlags;
    }

    @Override
    public boolean hasRegion(World worldname, String regionName) {
        return getWorldGuardPlugin.getRegionManager(worldname).hasRegion(regionName);
    }

    @Override
    public boolean hasRegion(String worldname, String regionName) {
        return hasRegion(Bukkit.getServer().getWorld(worldname), regionName);
    }
}
