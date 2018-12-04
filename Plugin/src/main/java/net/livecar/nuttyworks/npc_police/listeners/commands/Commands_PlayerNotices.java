package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands_PlayerNotices {

    @CommandInfo(
            name = "setescapenotice",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setescapenotice_args",
            helpMessage = "command_setescapenotice_help",
            arguments = {"--jail|--world|#", "<jail>|<world>|#", "#", "#"},
            permission = "npcpolice.config.notices.escaped",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean SettingConfig_Escaped(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (serverWorld == null) {
            serverWorld = player.getWorld().getName();
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            if (selectedJail != null) {
                selectedJail.escaped_Distance = -1;
                selectedJail.escaped_Delay = -1.0D;
            } else {
                policeRef.getJailManager.getWorldSettings(serverWorld).setEscaped_Distance(-1);
                policeRef.getJailManager.getWorldSettings(serverWorld).setEscaped_Delay(-1.0D);
            }
        } else if (inargs.length < 3) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_notice_bardargs");
            return true;
        } else if (policeRef.getUtilities.isNumeric(inargs[1]) && policeRef.getUtilities.isNumeric(inargs[2].replace(".", ""))) {
            if (selectedJail != null) {
                selectedJail.escaped_Distance = Integer.parseInt(inargs[1]);
                selectedJail.escaped_Delay = Double.parseDouble(inargs[2]);
            } else {
                policeRef.getJailManager.getWorldSettings(serverWorld).setEscaped_Distance(Integer.parseInt(inargs[1]));
                policeRef.getJailManager.getWorldSettings(serverWorld).setEscaped_Delay(Double.parseDouble(inargs[2]));
            }
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
            name = "setjailednotice",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setjailednotice_args",
            helpMessage = "command_setjailednotice_help",
            arguments = {"--jail|--world|#", "<jail>|<world>|#", "#", "#"},
            permission = "npcpolice.config.notices.jailed",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean SettingConfig_Arrest(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (serverWorld == null) {
            serverWorld = player.getWorld().getName();
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setJailed_Distance(-1);
            policeRef.getJailManager.getWorldSettings(serverWorld).setJailed_Delay(-1.0D);
        } else if (inargs.length < 3) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_notice_bardargs");
            return true;
        } else if (policeRef.getUtilities.isNumeric(inargs[1]) && policeRef.getUtilities.isNumeric(inargs[2])) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setJailed_Distance(Integer.parseInt(inargs[1]));
            policeRef.getJailManager.getWorldSettings(serverWorld).setJailed_Delay(Double.parseDouble(inargs[2]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setmurdernotice",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setmurdernotice_args",
            helpMessage = "command_setmurdernotice_help",
            arguments = {"--jail|--world|#", "<jail>|<world>|#", "#", "#"},
            permission = "npcpolice.config.notices.murder",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean SettingConfig_Murder(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (serverWorld == null) {
            serverWorld = player.getWorld().getName();
        }

        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (inargs.length == 1) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMurder_Distance(-1);
            policeRef.getJailManager.getWorldSettings(serverWorld).setMurder_Delay(-1.0D);
        } else if (inargs.length < 3) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_notice_bardargs");
            return true;
        } else if (policeRef.getUtilities.isNumeric(inargs[1]) && policeRef.getUtilities.isNumeric(inargs[2].replace(".", ""))) {
            policeRef.getJailManager.getWorldSettings(serverWorld).setMurder_Distance(Integer.parseInt(inargs[1]));
            policeRef.getJailManager.getWorldSettings(serverWorld).setMurder_Delay(Double.parseDouble(inargs[2]));
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_nonumeric");
            return true;
        }

        policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }
}
