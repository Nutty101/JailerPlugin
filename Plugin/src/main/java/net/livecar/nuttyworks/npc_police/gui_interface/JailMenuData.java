package net.livecar.nuttyworks.npc_police.gui_interface;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;

public class JailMenuData {

    private Action menuAction;
    private NPCPolice_Trait trait;
    private Arrest_Record plrData;
    private NPC npc;
    private Jail_Setting jailSetting;
    public JailMenuData(Action menuaction) {
        this.menuAction = menuaction;
    }

    public JailMenuData(Action menuaction, Arrest_Record playerdata) {
        this.menuAction = menuaction;
        this.plrData = playerdata;
    }

    public JailMenuData(Action menuaction, NPC npc) {
        this.menuAction = menuaction;
        this.npc = npc;
    }

    public JailMenuData(Action menuaction, Arrest_Record playerdata, Jail_Setting jailSetting, NPC npc, NPCPolice_Trait trait) {
        this.menuAction = menuaction;
        this.plrData = playerdata;
        this.npc = npc;
        this.jailSetting = jailSetting;
        this.trait = trait;
    }

    public Action getMenuAction() {
        return this.menuAction;
    }

    public NPCPolice_Trait getTrait() {
        return this.trait;
    }

    public Arrest_Record GetPlayerData() {
        return this.plrData;
    }

    public Jail_Setting GetJailData() {
        return this.jailSetting;
    }

    public NPC getNPC() {
        return this.npc;
    }

    public enum Action {
        Close, PayOwn, PayPlayer, Arrested, Warrants, Escaped, MainMenu
    }
}
