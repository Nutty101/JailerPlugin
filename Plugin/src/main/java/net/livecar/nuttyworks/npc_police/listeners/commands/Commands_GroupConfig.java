package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_GroupConfig {
    @CommandInfo(
            name = "setwantedgroup",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setwantedgroup_args",
            helpMessage = "command_setwantedgroup_help",
            arguments = {"--world|<groups>", "<world>", "<groups>"},
            permission = "npcpolice.settings.groups.wanted",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean settingConfig_WantedGroup(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        boolean groupFound = false;
        if (policeRef.getPermissionManager == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", "Invalid");
            return true;
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            //Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length == 0) {
            worldConfig.setWantedGroup("");
        } else {
            for (String groupName : policeRef.getPermissionManager.getGroups()) {
                if (groupName.equalsIgnoreCase(inargs[1])) {
                    groupFound = true;
                }
            }

            if (!groupFound) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", inargs[1]);
                return true;
            }

            worldConfig.setWantedGroup(inargs[1]);
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setjailedgroup",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setjailedgroup_args",
            helpMessage = "command_setjailedgroup_help",
            arguments = {"--world|<groups>", "<world>", "<groups>"},
            permission = "npcpolice.settings.groups.jailed",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean settingConfig_JailedGroup(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        boolean groupFound = false;
        if (policeRef.getPermissionManager == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", "Invalid");
            return true;
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            //Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length == 0) {
            worldConfig.setJailedGroup("");
        } else {
            for (String groupName : policeRef.getPermissionManager.getGroups()) {
                if (groupName.equalsIgnoreCase(inargs[1])) {
                    groupFound = true;
                }
            }

            if (!groupFound) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", inargs[1]);
                return true;
            }

            worldConfig.setJailedGroup(inargs[1]);
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setescapedgroup",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setescapedgroup_args",
            helpMessage = "command_setescapedgroup_help",
            arguments = {"--world|<groups>", "<world>", "<groups>"},
            permission = "npcpolice.settings.groups.escaped",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean settingConfig_EscapedGroup(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        boolean groupFound = false;
        if (policeRef.getPermissionManager == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", "Invalid");
            return true;
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            //Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length == 0) {
            worldConfig.setEscapedGroup("");
        } else {
            for (String groupName : policeRef.getPermissionManager.getGroups()) {
                if (groupName.equalsIgnoreCase(inargs[1])) {
                    groupFound = true;
                }
            }

            if (!groupFound) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_group_invalid", inargs[1]);
                return true;
            }

            worldConfig.setEscapedGroup(inargs[1]);
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }
}
