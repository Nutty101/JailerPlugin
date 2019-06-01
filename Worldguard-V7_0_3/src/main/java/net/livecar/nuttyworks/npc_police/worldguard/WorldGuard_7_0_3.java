package net.livecar.nuttyworks.npc_police.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
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

public class WorldGuard_7_0_3 extends VersionBridge implements Listener {

    public static boolean isValidVersion() {
        try {
            //Validate that BlockVector3 class exists
            Class.forName("com.sk89q.worldedit.math.BlockVector3");
        } catch (Exception e) {
            return false;
        }

        try {
            //Validate that getWorldByName method exists (New beta's do not have this function anymore)
            Class.forName("com.sk89q.worldguard.internal.platform.WorldGuardPlatform").getMethod("getWorldByName",(Class<?>[]) null);
            return false;
        } catch (Exception e) {
            return true;
        }
    }


    @Override
    public void registerFlags() {
        WorldGuard.getInstance().getFlagRegistry().register(CELL_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(ARREST_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(PVP_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(MURDER_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(ASSAULT_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(EXTENDEDJAIL_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(REGIONGUARD_FLAG);

        WorldGuard.getInstance().getFlagRegistry().register(AUTOFLAG_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(AUTOFLAG_BOUNTY_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(AUTOFLAG_BOUNTY_COOLDOWN);
        WorldGuard.getInstance().getFlagRegistry().register(AUTOFLAG_GUARDSIGHT_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(AUTOFLAG_CAUGHT_FLAG);

        // 2.1.1 -- Wanted addition
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_DENYMIN_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_DENYMAX_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_NPC_SETTING_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_KICK_TYPE_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_KICK_LOCATION_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_CHANGE_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(WANTED_FORCED_FLAG);

        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_DAMAGE_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_PVP_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_MURDER_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_ESCAPED_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_WANTED_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(BOUNTY_MAXIMUM_FLAG);

    }

    @Override
    public void registerHandlers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WorldGuard_7_0_3_RegionalHandler.FACTORY, null);
            }
        }.runTask(Bukkit.getServer().getPluginManager().getPlugin("NPC_Police"));
    }

    @Override
    public void unregisterFlags() {
        //Unused now
    }

    @Override
    public String getCurrentRegion(Location loc) {
        return getCurrentRegions(loc).get(0);
    }

    @Override
    public List<String> getCurrentRegions(Location loc) {
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getApplicableRegionsIDs(v);
    }

    @Override
    public List<String> getWorldRegions(World world) {
        List<String> regionList = new ArrayList<String>();
        regionList.addAll(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions().keySet());
        return regionList;
    }

    @Override
    public boolean isInRegion(Location loc, String region) {
        return getCurrentRegions(loc).contains(region);
    }

    @Override
    public boolean isInCell(Location loc) {
        for (String regionName : getCurrentRegions(loc)) {
            if (WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegion(regionName).getFlag(CELL_FLAG).booleanValue())
                return true;
        }
        return false;
    }

    @Override
    public boolean regionArrestable(Location loc) {
        for (String regionName : getCurrentRegions(loc)) {
            if (WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegion(regionName).getFlag(ARREST_FLAG).booleanValue())
                return true;
        }
        return false;
    }

    @Override
    public Location[] getRegionBounds(World world, String regionName) {
        ProtectedRegion boundRegion = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(regionName);
        if (boundRegion == null)
            return new Location[0];

        Location[] boundLocs = new Location[2];
        boundLocs[0] = new Location(world, boundRegion.getMinimumPoint().getBlockX(), boundRegion.getMinimumPoint().getBlockY(), boundRegion.getMinimumPoint().getBlockZ());
        boundLocs[1] = new Location(world, boundRegion.getMaximumPoint().getBlockX(), boundRegion.getMaximumPoint().getBlockY(), boundRegion.getMaximumPoint().getBlockZ());

        return boundLocs;
    }

    @Override
    public RegionSettings getRelatedRegionFlags(Location loc) {
        RegionSettings regionFlags = new RegionSettings();
        List<String> regionList = getCurrentRegions(loc);
        regionList.add("__global__");
        for (String regionName : regionList) {

            if (regionName == null || regionName.isEmpty())
                continue;

            ProtectedRegion localRegion = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegion(regionName);

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
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(worldname)).hasRegion(regionName);
    }

    @Override
    public boolean hasRegion(String worldname, String regionName) {
        return hasRegion(Bukkit.getServer().getWorld(worldname), regionName);
    }
}
