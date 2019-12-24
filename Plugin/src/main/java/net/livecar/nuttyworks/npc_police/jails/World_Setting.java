package net.livecar.nuttyworks.npc_police.jails;

import net.livecar.nuttyworks.npc_police.api.Enumerations;
import net.livecar.nuttyworks.npc_police.api.Enumerations.KICK_TYPE;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World_Setting {

    //Command groups
    public List<String> onPlayer_Arrest = null;
    public List<String> onPlayer_Escaped = null;
    public List<String> onPlayer_Released = null;
    public List<String> onPlayer_Wanted = null;
    public List<String> onNPC_Warning = null;
    public List<String> onNPC_AlertGuards = null;
    public List<String> onNPC_NoGuards = null;
    public List<String> onNPC_Murder = null;
    public List<String> onBounty_Maximum = null;

    public HashMap<String, Jail_Setting> jail_Configs = null;
    public ItemStack[] bannedItems = null;

    // World Name
    private String world_Name = "";

    // Notification settings
    private int bounty_Notice = 60;

    // Wanted groups
    private String groups_Wanted = "";
    private String groups_Jailed = "";
    private String groups_Escaped = "";

    // Wanted Level Settings
    private WANTED_LEVEL wanted_Minimum = WANTED_LEVEL.GLOBAL;
    private WANTED_LEVEL wanted_Maximum = WANTED_LEVEL.GLOBAL;
    private KICK_TYPE wanted_KickType = KICK_TYPE.NOTSET;
    private String wanted_KickLocation = "";

    // Enabled features
    private Enumerations.ESCAPE_SETTING escapeSetting = Enumerations.ESCAPE_SETTING.NOTSET;
    private Integer escapeLastSeenInJail = -1;
    private STATE_SETTING arrestOnRespawn = STATE_SETTING.NOTSET;

    // Bounty Settings
    private Double bounty_Damage = -1.0D;
    private Double bounty_Escaped = -1.0D;
    private Double bounty_Murder = -1.0D;
    private Double bounty_PVP = -1.0D;
    private Double bounty_Maximum = -1.0D;

    // Timed bounty modifiers
    private Double times_Jailed = Double.MIN_VALUE;
    private Double times_Escaped = Double.MIN_VALUE;
    private Double times_Wanted = Double.MIN_VALUE;
    private Double times_CellDay = Double.MIN_VALUE;
    private Double times_CellNight = Double.MIN_VALUE;

    // NPC settings
    private int npc_maxDamageWarning = -1;
    private int npc_maxDistanceGuard = -1;
    private int npc_minBountyWanted = -1;
    private STATE_SETTING npc_monitorPVP = STATE_SETTING.NOTSET;
    private STATE_SETTING npc_losAttack = STATE_SETTING.NOTSET;
    private STATE_SETTING npc_protect_OnlyAssigned = STATE_SETTING.NOTSET;

    private int npc_RandomLookDegrees = 0;
    private int npc_RandomLookMin = 0;
    private int npc_RandomLookMax = 0;

    // Player Notices
    private int escaped_Distance = -1;
    private Double escaped_Delay = -1.0D;
    private int jailed_Distance = -1;
    private Double jailed_Delay = -1.0D;
    private int murder_Distance = -1;
    private Double murder_Delay = -1.0D;
    private int theft_Distance = -1;
    private Double theft_Delay = -1.0D;

    // Jail inventory settings
    private STATE_SETTING onArrest_InventoryAction = STATE_SETTING.NOTSET;
    private STATE_SETTING onEscape_InventoryAction = STATE_SETTING.NOTSET;
    private STATE_SETTING onFree_InventoryAction = STATE_SETTING.NOTSET;
    private int lockedInventory_MaxLife = -1;

    public World_Setting(String worldName) {
        jail_Configs = new HashMap<String, Jail_Setting>();
        world_Name = worldName;
        bannedItems = new ItemStack[60];

        onPlayer_Arrest = new ArrayList<String>();
        onPlayer_Escaped = new ArrayList<String>();
        onPlayer_Released = new ArrayList<String>();
        onPlayer_Wanted = new ArrayList<String>();
        onNPC_Warning = new ArrayList<String>();
        onNPC_AlertGuards = new ArrayList<String>();
        onNPC_NoGuards = new ArrayList<String>();
        onNPC_Murder = new ArrayList<String>();
        onBounty_Maximum = new ArrayList<String>();
    }

    public World_Setting() {
        jail_Configs = new HashMap<String, Jail_Setting>();
        world_Name = "_GlobalSettings";
        bannedItems = new ItemStack[60];

        this.groups_Escaped = "";
        this.groups_Jailed = "";
        this.groups_Wanted = "";

        this.bounty_Damage = 76D;
        this.bounty_Escaped = 1500D;
        this.bounty_Murder = 2400D;
        this.bounty_Maximum = -1D;
        this.bounty_PVP = 10D;

        this.times_CellDay = 0D;
        this.times_CellNight = 0D;
        this.times_Escaped = 1D;
        this.times_Jailed = -20D;
        this.times_Wanted = 0D;

        this.onArrest_InventoryAction = STATE_SETTING.FALSE;
        this.onEscape_InventoryAction = STATE_SETTING.NOTSET;
        this.onFree_InventoryAction = STATE_SETTING.TRUE;

        this.npc_maxDamageWarning = 3;
        this.npc_maxDistanceGuard = 25;
        this.npc_minBountyWanted = 0;

        this.npc_monitorPVP = STATE_SETTING.FALSE;
        this.npc_protect_OnlyAssigned = STATE_SETTING.FALSE;
        this.npc_losAttack = STATE_SETTING.FALSE;

        this.wanted_Minimum = WANTED_LEVEL.NONE;
        this.wanted_Maximum = WANTED_LEVEL.HIGH;

        this.bounty_Notice = 60;

        this.escaped_Distance = 125;
        this.escaped_Delay = 0.2D;
        this.jailed_Distance = 125;
        this.jailed_Delay = 0.4D;
        this.murder_Distance = 125;
        this.murder_Delay = 0.3D;
        this.theft_Distance = 125;
        this.theft_Delay = 0.3D;

        onPlayer_Arrest = new ArrayList<String>();
        onPlayer_Escaped = new ArrayList<String>();
        onPlayer_Released = new ArrayList<String>();
        onPlayer_Wanted = new ArrayList<String>();
        onNPC_Warning = new ArrayList<String>();
        onNPC_AlertGuards = new ArrayList<String>();
        onNPC_NoGuards = new ArrayList<String>();
        onNPC_Murder = new ArrayList<String>();
        onBounty_Maximum = new ArrayList<String>();
    }


    public String getWorldName() {
        return world_Name;
    }

    public int getBounty_Notice() {
        return bounty_Notice;
    }

    public void setBounty_Notice(int bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_Notice = bounty;
    }

    public String getWantedGroup() {
        return groups_Wanted;
    }

    public void setWantedGroup(String wantedGroup) {
        this.groups_Wanted = wantedGroup;
    }

    public String getJailedGroup() {
        return groups_Jailed;
    }

    public void setJailedGroup(String jailedGroup) {
        this.groups_Jailed = jailedGroup;
    }

    public String getEscapedGroup() {
        return groups_Escaped;
    }

    public void setEscapedGroup(String escapedGroup) {
        this.groups_Escaped = escapedGroup;
    }

    public WANTED_LEVEL getMinimum_WantedLevel() {
        return wanted_Minimum;
    }

    public void setMinimum_WantedLevel(WANTED_LEVEL wantedLevel) {
        if (wantedLevel == WANTED_LEVEL.GLOBAL && world_Name.equalsIgnoreCase("_GlobalSettings"))
            wanted_Minimum = WANTED_LEVEL.NONE;
        else
            wanted_Minimum = wantedLevel;
    }

    public WANTED_LEVEL getMaximum_WantedLevel() {
        return wanted_Maximum;
    }

    public void setMaximum_WantedLevel(WANTED_LEVEL wantedLevel) {
        if (wantedLevel == WANTED_LEVEL.GLOBAL && world_Name.equalsIgnoreCase("_GlobalSettings"))
            wanted_Maximum = this.wanted_Minimum;
        else
            wanted_Maximum = wantedLevel;
    }

    public KICK_TYPE getKickType() {
        return wanted_KickType;
    }

    public void setKickType(KICK_TYPE kick) {
        wanted_KickType = kick;
    }

    public String getKickLocation() {
        return wanted_KickLocation;
    }

    public void setKickLocation(String kickloc) {
        wanted_KickLocation = kickloc;
    }

    public Double getBounty_Damage() {
        return bounty_Damage;
    }

    public void setBounty_Damage(Double bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_Damage = bounty;
    }

    public Double getBounty_Escaped() {
        return bounty_Escaped;
    }

    public void setBounty_Escaped(Double bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_Escaped = bounty;
    }

    public Double getBounty_Murder() {
        return bounty_Murder;
    }

    public void setBounty_Murder(Double bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_Murder = bounty;
    }

    public Double getBounty_PVP() {
        return bounty_PVP;
    }

    public void setBounty_PVP(Double bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_PVP = bounty;
    }

    public Double getBounty_Maximum() {
        return bounty_Maximum;
    }

    public void setBounty_Maximum(Double bounty) {
        if (bounty == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        bounty_Maximum = bounty;
    }

    public int getRandomLook_Degrees() {
        return this.npc_RandomLookDegrees;
    }

    public void setRandomLook_Degrees(int degrees) {
        if (degrees < 0 || degrees > 360) {
            this.npc_RandomLookDegrees = 0;
            return;
        }
        this.npc_RandomLookDegrees = degrees;
    }

    public int getRandomLook_Min() {
        return this.npc_RandomLookMin;
    }

    public void setRandomLook_Min(int seconds) {
        if (seconds < 0 || seconds > 360) {
            this.npc_RandomLookMin = 0;
            return;
        }
        this.npc_RandomLookMin = seconds;
    }

    public int getRandomLook_Max() {
        return this.npc_RandomLookMax;
    }

    public void setRandomLook_Max(int seconds) {
        if (seconds < 0 || seconds > 360) {
            this.npc_RandomLookMax = 0;
            return;
        }
        this.npc_RandomLookMax = seconds;
    }


    public Double getTimeInterval_Jailed() {
        return times_Jailed;
    }

    public void setTimeInterval_Jailed(Double time) {
        if (time == Double.MIN_VALUE && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        times_Jailed = time;
    }

    public Double getTimeInterval_Escaped() {
        return times_Escaped;
    }

    public void setTimeInterval_Escaped(Double time) {
        if (time == Double.MIN_VALUE && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        times_Escaped = time;
    }

    public Double getTimeInterval_Wanted() {
        return times_Wanted;
    }

    public void setTimeInterval_Wanted(Double time) {
        if (time == Double.MIN_VALUE && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        times_Wanted = time;
    }

    public Double getTimeInterval_CellDay() {
        return times_CellDay;
    }

    public void setTimeInterval_CellDay(Double time) {
        if (time == Double.MIN_VALUE && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        times_CellDay = time;
    }

    public Double getTimeInterval_CellNight() {
        return times_CellNight;
    }

    public void setTimeInterval_CellNight(Double time) {
        if (time == Double.MIN_VALUE && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        times_CellNight = time;
    }


    public int getWarning_MaximumDamage() {
        return this.npc_maxDamageWarning;
    }

    public void setWarning_MaximumDamage(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_maxDamageWarning = amount;
    }

    public int getMaximum_GardDistance() {
        return this.npc_maxDistanceGuard;
    }

    public void setMaximum_GuardDistance(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_maxDistanceGuard = amount;
    }

    public int getMinumum_WantedBounty() {
        return this.npc_minBountyWanted;
    }

    public void setMinimum_WantedBounty(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_minBountyWanted = amount;
    }

    public STATE_SETTING getMonitorPVP() {
        return this.npc_monitorPVP;
    }

    public void setMonitorPVP(STATE_SETTING monitor) {
        if (monitor == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_monitorPVP = monitor;
    }

    public STATE_SETTING getLOSAttackSetting() {
        return this.npc_losAttack;
    }

    public void setLOSAttackSetting(STATE_SETTING monitor) {
        if (monitor == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_losAttack = monitor;
    }

    public STATE_SETTING getProtect_OnlyAssigned() {
        return this.npc_protect_OnlyAssigned;
    }

    public void setProtect_OnlyAssigned(STATE_SETTING monitor) {
        if (monitor == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        npc_protect_OnlyAssigned = monitor;
    }


    public int getEscaped_Distance() {
        return this.escaped_Distance;
    }

    public void setEscaped_Distance(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        escaped_Distance = amount;
    }

    public Double getEscaped_Delay() {
        return escaped_Delay;
    }

    public void setEscaped_Delay(Double time) {
        if (time == -1.0D && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        escaped_Delay = time;
    }


    public int getJailed_Distance() {
        return this.jailed_Distance;
    }

    public void setJailed_Distance(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        jailed_Distance = amount;
    }

    public Double getJailed_Delay() {
        return jailed_Delay;
    }

    public void setJailed_Delay(Double time) {
        if (time == -1.0D && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        jailed_Delay = time;
    }


    public int getMurder_Distance() {
        return this.murder_Distance;
    }

    public void setMurder_Distance(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        murder_Distance = amount;
    }

    public Double getMurder_Delay() {
        return murder_Delay;
    }

    public void setMurder_Delay(Double time) {
        if (time == -1.0D && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        murder_Delay = time;
    }


    public int getTheft_Distance() {
        return this.theft_Distance;
    }

    public void setTheft_Distance(int amount) {
        if (amount == -1 && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        theft_Distance = amount;
    }

    public Double getTheft_Delay() {
        return theft_Delay;
    }

    public void setTheft_Delay(Double time) {
        if (time == -1.0D && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        theft_Delay = time;
    }


    public STATE_SETTING onArrest_InventoryAction() {
        return this.onArrest_InventoryAction;
    }

    public void onArrest_InventoryAction(STATE_SETTING action) {
        if (action == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            onArrest_InventoryAction = STATE_SETTING.FALSE;
        else
            onArrest_InventoryAction = action;
    }

    public STATE_SETTING onEscape_InventoryAction() {
        return this.onEscape_InventoryAction;
    }

    public void onEscape_InventoryAction(STATE_SETTING action) {
        if (action == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            onEscape_InventoryAction = STATE_SETTING.FALSE;
        else
            onEscape_InventoryAction = action;
    }


    public STATE_SETTING onFree_InventoryAction() {
        return this.onFree_InventoryAction;
    }

    public void onFree_InventoryAction(STATE_SETTING action) {
        if (action == STATE_SETTING.NOTSET && world_Name.equalsIgnoreCase("_GlobalSettings"))
            onFree_InventoryAction = STATE_SETTING.FALSE;
        else
            onFree_InventoryAction = action;
    }

    public Enumerations.ESCAPE_SETTING getEscapeSetting()
    {
        return this.escapeSetting;
    }

    public void setEscapeSetting(Enumerations.ESCAPE_SETTING enabled)
    {
        this.escapeSetting = enabled;
    }

    public int getEscapeLastSeen()
    {
        return this.escapeLastSeenInJail;
    }

    public void setEscapeLastSeen(int escapeLastSeenInJail)
    {
        if (escapeLastSeenInJail == -1.0D && world_Name.equalsIgnoreCase("_GlobalSettings"))
            return;
        this.escapeLastSeenInJail = escapeLastSeenInJail;
    }
    
    public STATE_SETTING getArrestOnRespawn() {return this.arrestOnRespawn;}
    public void setArrestOnRespawn(STATE_SETTING setting) {this.arrestOnRespawn = setting;}
    
}
