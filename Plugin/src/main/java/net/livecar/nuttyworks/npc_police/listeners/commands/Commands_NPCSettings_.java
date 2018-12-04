package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_NPCSettings_ {

    @CommandInfo(
            name = "setnpcpvp",
            group = "NPC Defaults",
            badArgumentsMessage = "command_setnpcpvp_args",
            helpMessage = "command_setnpcpvp_help",
            arguments = {"--world", "<world>"},
            permission = "npcpolice.settings.npc.pvp",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean serverWorldConfig_NPCPVP(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (policeRef.getJailManager.getWorldSettings(serverWorld).getMonitorPVP() == STATE_SETTING.NOTSET)
            policeRef.getJailManager.getWorldSettings(serverWorld).setMonitorPVP(STATE_SETTING.FALSE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getMonitorPVP() == STATE_SETTING.FALSE)
            policeRef.getJailManager.getWorldSettings(serverWorld).setMonitorPVP(STATE_SETTING.TRUE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getMonitorPVP() == STATE_SETTING.TRUE)
            if (serverWorld.equalsIgnoreCase("_GlobalSettings"))
                policeRef.getJailManager.getWorldSettings(serverWorld).setMonitorPVP(STATE_SETTING.FALSE);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setMonitorPVP(STATE_SETTING.NOTSET);

        policeRef.getMessageManager.sendMessage(sender, "serverworld_settings", policeRef.getJailManager.getWorldSettings(serverWorld));
        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setnpcmaxdistance",
            group = "NPC Defaults",
            badArgumentsMessage = "command_setnpcmaxdistance_args",
            helpMessage = "command_setnpcmaxdistance_help",
            arguments = {"--world|#", "<world>", "#"},
            permission = "npcpolice.settings.npc.distance",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean serverWorldConfig_NPCMaxDistance(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMaximum_GuardDistance(-1);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMaximum_GuardDistance(Integer.parseInt(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setnpclosattack",
            group = "NPC Defaults",
            badArgumentsMessage = "command_npclosattack_args",
            helpMessage = "command_setnpclosattack_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.settings.npc.losattack",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCLosAttack(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (policeRef.getJailManager.getWorldSettings(serverWorld).getLOSAttackSetting() == STATE_SETTING.NOTSET)
            policeRef.getJailManager.getWorldSettings(serverWorld).setLOSAttackSetting(STATE_SETTING.FALSE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getLOSAttackSetting() == STATE_SETTING.FALSE)
            policeRef.getJailManager.getWorldSettings(serverWorld).setLOSAttackSetting(STATE_SETTING.TRUE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getLOSAttackSetting() == STATE_SETTING.TRUE)
            if (serverWorld.equalsIgnoreCase("_GlobalSettings"))
                policeRef.getJailManager.getWorldSettings(serverWorld).setLOSAttackSetting(STATE_SETTING.FALSE);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setLOSAttackSetting(STATE_SETTING.NOTSET);

        policeRef.getMessageManager.sendMessage(sender, "serverworld_settings", policeRef.getJailManager.getWorldSettings(serverWorld));
        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }


    @CommandInfo(
            name = "setnpcminbounty",
            group = "NPC Defaults",
            badArgumentsMessage = "command_setnpcminbounty_args",
            helpMessage = "command_setnpcminbounty_help",
            arguments = {"--world|#", "<world>", "#"},
            permission = "npcpolice.settings.npc.minbounty",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean serverWorldConfig_NPCSetMinBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMinimum_WantedBounty(-1);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMinimum_WantedBounty(Integer.parseInt(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setnpcmaxwarning",
            group = "NPC Defaults",
            badArgumentsMessage = "command_setnpcmaxwarning_args",
            helpMessage = "command_setnpcmaxwarning_help",
            arguments = {"--world|#", "<world>", "#"},
            permission = "npcpolice.settings.npc.warning",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean serverWorldConfig_NPCMaxWarning(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setWarning_MaximumDamage(-1);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setWarning_MaximumDamage(Integer.parseInt(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setnpconlyassigned",
            group = "NPC Defaults",
            badArgumentsMessage = "command_setnpconlyassigned_args",
            helpMessage = "command_setnpconlyassigned_help",
            arguments = {"--world", "<world>"},
            permission = "npcpolice.settings.npc.assigned",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean serverWorldConfig_NPCProtectOnlyAssigned(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (policeRef.getJailManager.getWorldSettings(serverWorld).getProtect_OnlyAssigned() == STATE_SETTING.NOTSET)
            policeRef.getJailManager.getWorldSettings(serverWorld).setProtect_OnlyAssigned(STATE_SETTING.FALSE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getProtect_OnlyAssigned() == STATE_SETTING.FALSE)
            policeRef.getJailManager.getWorldSettings(serverWorld).setProtect_OnlyAssigned(STATE_SETTING.TRUE);
        else if (policeRef.getJailManager.getWorldSettings(serverWorld).getProtect_OnlyAssigned() == STATE_SETTING.TRUE)
            if (serverWorld.equalsIgnoreCase("_GlobalSettings"))
                policeRef.getJailManager.getWorldSettings(serverWorld).setProtect_OnlyAssigned(STATE_SETTING.FALSE);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setProtect_OnlyAssigned(STATE_SETTING.NOTSET);


        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

}
