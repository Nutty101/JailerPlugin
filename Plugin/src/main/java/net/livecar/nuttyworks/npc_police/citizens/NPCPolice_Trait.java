package net.livecar.nuttyworks.npc_police.citizens;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.livecar.nuttyworks.npc_police.api.Enumerations.NPC_AWARDS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;

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
    public NPCPolice_Trait() {
        super("npcpolice");
    }
}
