package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_GlobalSettings {
    @CommandInfo(
            name = "global",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_world_args",
            helpMessage = "command_global_help",
            arguments = {"--world", ""},
            permission = "npcpolice.settings.show",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean worldConfig_ShowConfig(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", policeRef.getJailManager.getWorldSettings("_GlobalSettings"));
        return true;
    }
}
