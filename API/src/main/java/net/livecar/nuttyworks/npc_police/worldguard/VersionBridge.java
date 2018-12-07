package net.livecar.nuttyworks.npc_police.worldguard;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class VersionBridge implements Listener {

    // Jail Configuration Flags
    public static final BooleanFlag CELL_FLAG = new BooleanFlag("np-jailcell");
    public static final StringFlag EXTENDEDJAIL_FLAG = new StringFlag("np-extendedjail");

    //Bounty Modification flags
    public static final DoubleFlag AUTOFLAG_BOUNTY_FLAG = new DoubleFlag("np-af-bounty");
    public static final DoubleFlag AUTOFLAG_BOUNTY_COOLDOWN = new DoubleFlag("np-af-cooldown");
    public static final DoubleFlag BOUNTY_DAMAGE_FLAG = new DoubleFlag("np-bnty-damage");
    public static final DoubleFlag BOUNTY_PVP_FLAG = new DoubleFlag("np-bnty-pvp");
    public static final DoubleFlag BOUNTY_MURDER_FLAG = new DoubleFlag("np-bnty-murder");
    public static final DoubleFlag BOUNTY_ESCAPED_FLAG = new DoubleFlag("np-bnty-escaped");
    public static final DoubleFlag BOUNTY_WANTED_FLAG = new DoubleFlag("np-bnty-wanted");
    public static final DoubleFlag BOUNTY_MAXIMUM_FLAG = new DoubleFlag("np-bnty-max");

    //Wanted level modification
    public static final StringFlag WANTED_CHANGE_FLAG = new StringFlag("np-wanted-change");
    public static final StringFlag WANTED_FORCED_FLAG = new StringFlag("np-wanted-force");
    public static final StringFlag WANTED_KICK_TYPE_FLAG = new StringFlag("np-kick-type");
    public static final StringFlag WANTED_KICK_LOCATION_FLAG = new StringFlag("np-kick-location");
    public static final StringFlag WANTED_DENYMIN_FLAG = new StringFlag("np-deny-wntmin");
    public static final StringFlag WANTED_DENYMAX_FLAG = new StringFlag("np-deny-wntmax");
    public static final StringFlag WANTED_NPC_SETTING_FLAG = new StringFlag("np-npc-wanted");

    //Activity Flags

    public static final BooleanFlag ARREST_FLAG = new BooleanFlag("np-noarrest");
    public static final BooleanFlag PVP_FLAG = new BooleanFlag("np-monitors-pvp");
    public static final BooleanFlag MURDER_FLAG = new BooleanFlag("np-mon-murder");
    public static final BooleanFlag ASSAULT_FLAG = new BooleanFlag("np-mon-assault");
    public static final BooleanFlag REGIONGUARD_FLAG = new BooleanFlag("np-regionguard");

    //Wanted Status Flags

    public static final CurrentStatusFlag AUTOFLAG_FLAG = new CurrentStatusFlag("np-af");
    public static final BooleanFlag AUTOFLAG_GUARDSIGHT_FLAG = new BooleanFlag("np-af-requiresight");
    public static final StringFlag AUTOFLAG_CAUGHT_FLAG = new StringFlag("np-af-caught");



    public static String replaceColorMacros(String str) {
        str = str.replace("&r", ChatColor.RED.toString());
        str = str.replace("&R", ChatColor.DARK_RED.toString());

        str = str.replace("&y", ChatColor.YELLOW.toString());
        str = str.replace("&Y", ChatColor.GOLD.toString());

        str = str.replace("&g", ChatColor.GREEN.toString());
        str = str.replace("&G", ChatColor.DARK_GREEN.toString());

        str = str.replace("&c", ChatColor.AQUA.toString());
        str = str.replace("&C", ChatColor.DARK_AQUA.toString());

        str = str.replace("&b", ChatColor.BLUE.toString());
        str = str.replace("&B", ChatColor.DARK_BLUE.toString());

        str = str.replace("&p", ChatColor.LIGHT_PURPLE.toString());
        str = str.replace("&P", ChatColor.DARK_PURPLE.toString());

        str = str.replace("&0", ChatColor.BLACK.toString());
        str = str.replace("&1", ChatColor.DARK_GRAY.toString());
        str = str.replace("&2", ChatColor.GRAY.toString());
        str = str.replace("&w", ChatColor.WHITE.toString());

        str = str.replace("&k", ChatColor.MAGIC.toString());
        str = str.replace("&l", ChatColor.BOLD.toString());
        str = str.replace("&m", ChatColor.STRIKETHROUGH.toString());
        str = str.replace("&n", ChatColor.UNDERLINE.toString());
        str = str.replace("&o", ChatColor.ITALIC.toString());

        str = str.replace("&x", ChatColor.RESET.toString());

        return str;
    }

    abstract public void registerFlags();

    abstract public void registerHandlers();

    abstract public void unregisterFlags();

    abstract public String getCurrentRegion(Location loc);

    abstract public List<String> getCurrentRegions(Location loc);

    abstract public List<String> getWorldRegions(World world);

    abstract public boolean isInRegion(Location loc, String region);

    abstract public boolean isInCell(Location loc);

    abstract public boolean regionArrestable(Location loc);

    abstract public Location[] getRegionBounds(World world, String regionName);

    abstract public RegionSettings getRelatedRegionFlags(Location loc);

    abstract public boolean hasRegion(World worldname, String regionName);

    abstract public boolean hasRegion(String worldname, String regionName);
}
