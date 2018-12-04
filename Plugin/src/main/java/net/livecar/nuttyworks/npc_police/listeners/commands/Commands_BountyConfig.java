package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_BountyConfig {

    @CommandInfo(
            name = "setescapedbounty",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setescapedbounty_args",
            helpMessage = "command_setescapedbounty_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.bounties.escaped",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_EscapedBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            if (selectedJail != null)
                selectedJail.bounty_Escaped = -1.0D;
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Escaped(-1.0D);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            if (selectedJail != null)
                selectedJail.bounty_Escaped = Double.parseDouble(inargs[1]);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Escaped(Double.parseDouble(inargs[1]));
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
            name = "setpvpbounty",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setpvpbounty_args",
            helpMessage = "command_setpvpbounty_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.bounties.pvp",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_PVPBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            if (selectedJail != null)
                selectedJail.bounty_PVP = -1.0D;
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_PVP(-1.0D);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            if (selectedJail != null)
                selectedJail.bounty_PVP = Double.parseDouble(inargs[1]);
            else
                policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_PVP(Double.parseDouble(inargs[1]));
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
            name = "setdamagebounty",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setdamagebounty_args",
            helpMessage = "command_setdamagebounty_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.bounties.damage",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_DamageBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Damage(-1.0D);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Damage(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setmurderbounty",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setmurderbounty_args",
            helpMessage = "command_setmurderbounty_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.bounties.murder",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_MurderBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Murder(-1.0D);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Murder(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setmaxbounty",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setmaxbounty_args",
            helpMessage = "command_setmaxbounty_help",
            arguments = {"--world|--jail|#", "<world>|<jail>", "#"},
            permission = "npcpolice.settings.bounties.maximum",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean SettingConfig_MaxBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Maximum(-1.0D);
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setBounty_Maximum(Double.parseDouble(inargs[1]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }
}
