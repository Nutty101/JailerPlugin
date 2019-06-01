package net.livecar.nuttyworks.npc_police.citizens;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.NPC_AWARDS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class NPCPolice_Trait extends Trait {

    @Persist
    public boolean hasMenu = false;
    @Persist
    public boolean isGuard = false;
    @Persist
    public int monitorPVP = -1;
    @Persist
    public int maxDistance_Guard = -1;
    @Persist
    public int minBountyAttack = -1;
    @Persist
    public int lineOfSightAttack = -1;
    @Persist
    public WANTED_SETTING wantedSetting = WANTED_SETTING.NONE;
    @Persist
    public NPC_AWARDS award_Style = NPC_AWARDS.NONE;
    @Persist
    public Double bounty_murder = -1.0D;
    @Persist
    public Double bounty_assault = -1.0D;
    @Persist
    public int time_murder = -1;
    @Persist
    public int time_assault = -1;
    @Persist
    public int idleRandomLookIntervalMin = 0;
    @Persist
    public int idleRandomLookIntervalMax = 0;
    @Persist
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
}
