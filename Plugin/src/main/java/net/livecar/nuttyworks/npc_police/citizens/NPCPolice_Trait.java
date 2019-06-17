package net.livecar.nuttyworks.npc_police.citizens;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.NPC_AWARDS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class NPCPolice_Trait extends Trait {

    public boolean hasMenu = false;
    public boolean isGuard = false;
    public int monitorPVP = -1;
    public int maxDistance_Guard = -1;
    public int minBountyAttack = -1;
    public int lineOfSightAttack = -1;
    public WANTED_SETTING wantedSetting = WANTED_SETTING.NONE;
    public NPC_AWARDS awardStyle = NPC_AWARDS.NONE;
    public Double bountyMurder = -1.0D;
    public Double bountyAssault = -1.0D;
    public int timeMurder = -1;
    public int timeAssault = -1;
    public int idleRandomLookIntervalMin = 0;
    public int idleRandomLookIntervalMax = 0;
    public int idleRandomLookDegrees = 0;

    private LocalDateTime lastViewChange;
    private Float lastYawPosition = 0F;
    private Location lastPosition = null;

    public NPCPolice_Trait() {
        super("npcpolice");
    }

    public void randomLook(NPC_Police pluginRef)
    {

        if (!npc.isSpawned())
            return;

        int randomLookIntervalMin = 0;
        int randomLookIntervalMax = 0;
        int randomLookDegrees = 0;

        if (pluginRef.getJailManager.containsWorld(npc.getEntity().getWorld().toString()))
        {
            if (pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Degrees() > 0)
                randomLookDegrees = pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Degrees();

            if (pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Min() > 0)
                randomLookIntervalMin = pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Min();

            if (pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Max() > 0)
                randomLookIntervalMin = pluginRef.getJailManager.getWorldSettings(npc.getEntity().getWorld().toString()).getRandomLook_Max();
        }

        if (pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Degrees() > 0) {
            randomLookDegrees = pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Degrees();
        }
        if (this.idleRandomLookDegrees > 0)
            randomLookDegrees = this.idleRandomLookDegrees;

        if (pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Min() > 0) {
            randomLookIntervalMin = pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Min();
        }
        if (this.idleRandomLookIntervalMin > 0)
            randomLookIntervalMin = this.idleRandomLookIntervalMin;

        if (pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Max() > 0) {
            randomLookIntervalMin = pluginRef.getJailManager.getWorldSettings("_GlobalSettings").getRandomLook_Max();
        }
        if (this.idleRandomLookIntervalMax > 0)
            randomLookIntervalMax = this.idleRandomLookIntervalMax;

        if (randomLookDegrees == 0 || randomLookIntervalMin ==0 || randomLookIntervalMax ==0 )
            return;

        LocalDateTime curTime = LocalDateTime.now();

        if (lastViewChange == null)
            lastViewChange = LocalDateTime.now();
        if (lastPosition == null)
            lastPosition = npc.getEntity().getLocation();

        long curInterval = lastViewChange.until(curTime, ChronoUnit.SECONDS);

        if (curInterval <= randomLookIntervalMin)
            return;

        int rndSeconds = ((int)Math.random() * (randomLookIntervalMax - randomLookIntervalMin) +1) + randomLookIntervalMin;

        if (curInterval < rndSeconds)
            return;

        if (lastPosition.distanceSquared(npc.getEntity().getLocation()) > 5) {
            lastPosition = npc.getEntity().getLocation();
            lastYawPosition = npc.getEntity().getLocation().getYaw();
        }

        int degreeChange = (int)(Math.random() * randomLookDegrees) / 2;
        net.citizensnpcs.util.Util.assumePose(npc.getEntity(),  lastYawPosition+degreeChange, npc.getEntity().getLocation().getPitch());
        lastViewChange = LocalDateTime.now();
        
    }

    @Override
    public void load(DataKey key)
    {

        hasMenu = key.getBoolean("hasMenu", false);
        isGuard = key.getBoolean("isGuard", false);
        monitorPVP = key.getInt("monitorPVP", -1);
        maxDistance_Guard = key.getInt("maxDistance_Guard", -1);
        minBountyAttack = key.getInt("minBountyAttack", -1);
        lineOfSightAttack = key.getInt("lineOfSightAttack", -1);

        if (!key.getString("wantedSetting", "").equals("")) {
            String wantedStr = key.getString("wantedSetting", "");
            for (WANTED_SETTING wanted : WANTED_SETTING.values()) {
                if (wanted.name().equals(wantedStr.toUpperCase())) {
                    wantedSetting = wanted;
                }
            }
        }

        if (!key.getString("awardStyle", "").equals("")) {
            String awardStr = key.getString("awardStyle", "");
            for (NPC_AWARDS style : NPC_AWARDS.values()) {
                if (style.name().equals(awardStr.toUpperCase())) {
                    awardStyle = style;
                }
            }
        }

        bountyMurder = key.getDouble("bountyMurder", bountyMurder);
        bountyAssault = key.getDouble("bountyAssault", bountyAssault);

        timeMurder = key.getInt("timeMurder", timeMurder);
        timeAssault = key.getInt("timeAssault", timeAssault);
        idleRandomLookIntervalMin = key.getInt("idleRandomLookIntervalMin", idleRandomLookIntervalMin);
        idleRandomLookIntervalMax = key.getInt("idleRandomLookIntervalMax", idleRandomLookIntervalMax);
        idleRandomLookDegrees = key.getInt("idleRandomLookDegrees", idleRandomLookDegrees);


        if (key.keyExists("time_murder"))
        {
            timeMurder = key.getInt("time_murder", timeMurder);
            key.removeKey("time_murder");
        }
        if (key.keyExists("time_assault"))
        {
            timeMurder = key.getInt("time_assault", timeMurder);
            key.removeKey("time_assault");
        }

        if (key.keyExists("bounty_murder"))
        {
            bountyMurder = key.getDouble("bounty_murder", bountyMurder);
            key.removeKey("bounty_murder");
        }
        if (key.keyExists("bounty_assault"))
        {
            bountyAssault = key.getDouble("bounty_assault", bountyMurder);
            key.removeKey("bounty_assault");
        }


    }

    @Override
    public void save(DataKey key) {

        key.setBoolean("hasMenu", hasMenu);
        key.setBoolean("isGuard", isGuard);
        key.setInt("monitorPVP", monitorPVP);
        key.setInt("maxDistance_Guard", maxDistance_Guard);
        key.setInt("minBountyAttack", minBountyAttack);
        key.setInt("lineOfSightAttack", lineOfSightAttack);

        key.setString("wantedSetting", wantedSetting.toString().toLowerCase());
        key.setString("awardStyle", awardStyle.toString().toLowerCase());

        key.setDouble("bountyMurder", bountyMurder);
        key.setDouble("bountyAssault", bountyAssault);

        key.setInt("timeMurder", timeMurder);
        key.setInt("timeAssault", timeAssault);
        key.setInt("idleRandomLookIntervalMin", idleRandomLookIntervalMin);
        key.setInt("idleRandomLookIntervalMax", idleRandomLookIntervalMax);
        key.setInt("idleRandomLookDegrees", idleRandomLookDegrees);

    }
}
