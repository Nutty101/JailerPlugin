package net.livecar.nuttyworks.npc_police.worldguard;

import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.KICK_TYPE;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;

public class RegionSettings {
    public String regionName = "";

    public CURRENT_STATUS region_AutoFlagStatus = null;
    public Double autoFlag_Bounty = null;
    public Double autoFlag_CoolDown = null;
    public STATE_SETTING autoFlag_RequiresSight = STATE_SETTING.NOTSET;
    public String autoFlag_CaughtNotice = "";

    public boolean isCell = false;
    public boolean noArrest = false;
    public boolean regionGuard = false;
    public String extendsJail = "";

    public STATE_SETTING monitorPVP = STATE_SETTING.NOTSET;
    public STATE_SETTING monitorMurder = STATE_SETTING.NOTSET;
    public STATE_SETTING monitorAssaults = STATE_SETTING.NOTSET;

    public Double bounty_Damage = null;
    public Double bounty_PVP = null;
    public Double bounty_Murder = null;
    public Double bounty_Escaped = null;
    public Double bounty_Wanted = null;
    public Double bounty_Maximum = null;

    //2.1.1++
    public WANTED_SETTING wanted_DenyMin = WANTED_SETTING.NONE;
    public WANTED_SETTING wanted_DenyMax = WANTED_SETTING.HIGH;
    public WANTED_SETTING wanted_NPC_Setting = null;
    public WANTED_SETTING wanted_Change = null;
    public WANTED_SETTING wanted_Forced = null;
    public KICK_TYPE wanted_Kick_Type = KICK_TYPE.NOTSET;
    public String wanted_Kick_Location = "";

}
