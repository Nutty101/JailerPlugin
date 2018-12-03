package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_TimesConfig {

    @CommandInfo(
            name = "settimejailed",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_settimejailed_args",
            helpMessage = "command_settimejailed_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.times.jailed",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_Jailed(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            if (selectedJail != null)
                selectedJail.times_Jailed = Double.MIN_VALUE;
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Jailed(Double.MIN_VALUE);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            if (selectedJail != null)
                selectedJail.times_Jailed = Double.parseDouble(inargs[1]);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Jailed(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "settimeescaped",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_settimeescaped_args",
            helpMessage = "command_settimeescaped_help",
            arguments = {"--world|#", "<world>", "#"},
            permission = "npcpolice.settings.times.escaped",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_Escaped(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Escaped(Double.MIN_VALUE);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Escaped(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "settimewanted",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_settimewanted_args",
            helpMessage = "command_settimewanted_help",
            arguments = {"--world|#", "<world>", "#"},
            permission = "npcpolice.settings.times.wanted",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_Wanted(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Wanted(Double.MIN_VALUE);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_Wanted(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "settimecellday",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_settimecellday_args",
            helpMessage = "command_settimecellday_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.times.cellday",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_CellDay(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_CellDay(Double.MIN_VALUE);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_CellDay(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }


        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "settimecellnight",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_settimecellnight_args",
            helpMessage = "command_settimecellnight_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.times.cellnight",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_CellNight(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            if (selectedJail != null)
                selectedJail.times_CellNight = Double.MIN_VALUE;
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_CellNight(Double.MIN_VALUE);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            if (selectedJail != null)
                selectedJail.times_CellNight = Double.parseDouble(inargs[1]);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setTimeInterval_CellNight(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }
}
