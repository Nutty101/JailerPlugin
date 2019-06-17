package net.livecar.nuttyworks.npc_police.jails;

import net.livecar.nuttyworks.npc_police.api.Enumerations;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Jail_Setting {
    public UUID jail_ID = null;
    public World jailWorld = null;
    public String jailName = null;
    public String displayName = null;
    public String regionName = null;

    public Double bounty_PVP = -1.0D;
    public Double bounty_Escaped = -1.0D;

    //Wanted Level Settings
    public WANTED_LEVEL minWanted = WANTED_LEVEL.NONE;
    public WANTED_LEVEL maxWanted = WANTED_LEVEL.HIGH;

    public Double times_Jailed = Double.MIN_VALUE;
    public Double times_CellDay = Double.MIN_VALUE;
    public Double times_CellNight = Double.MIN_VALUE;

    //Jail inventory settings
    public STATE_SETTING onArrest_InventoryAction = STATE_SETTING.NOTSET;
    public STATE_SETTING onEscape_InventoryAction = STATE_SETTING.NOTSET;
    public STATE_SETTING onFree_InventoryAction = STATE_SETTING.NOTSET;

    public Location lockedInventoryLocation = null;
    public Location freeSpawnPoint = null;

    // Escaped Enabled
    public Enumerations.ESCAPE_SETTING escapeSetting = Enumerations.ESCAPE_SETTING.NOTSET;
    public Integer escapeLastSeenInJail = -1;

    // Player Notices
    public int escaped_Distance = -1;
    public Double escaped_Delay = -1.0D;

    public List<Location> cellLocations = null;

    public List<String> onPlayer_Arrest = null;
    public List<String> onPlayer_Escaped = null;
    public List<String> onPlayer_Released = null;

    public Jail_Setting() {
        jail_ID = UUID.randomUUID();
    }

    public Jail_Setting(String jailWorld, String jailName, String regionName, String longName) {
        jail_ID = UUID.randomUUID();
        this.jailName = jailName;
        this.jailWorld = Bukkit.getServer().getWorld(jailWorld);
        this.displayName = longName;
        this.regionName = regionName;
        this.cellLocations = new ArrayList<Location>();
        this.onPlayer_Arrest = new ArrayList<String>();
        this.onPlayer_Escaped = new ArrayList<String>();
        this.onPlayer_Released = new ArrayList<String>();
    }

}
