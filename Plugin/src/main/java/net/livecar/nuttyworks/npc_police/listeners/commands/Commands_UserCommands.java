package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class Commands_UserCommands {
    @CommandInfo(
            name = "mystats",
            group = "Player Commands",
            badArgumentsMessage = "command_mystats_args",
            helpMessage = "command_mystats_help",
            arguments = {""},
            permission = "npcpolice.stats.mystats",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean userCommand_MyStats(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Arrest_Record plrRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());
        policeRef.getMessageManager.sendMessage(sender, "player_stats.mystats_information", plrRecord);
        return true;
    }

    @CommandInfo(
            name = "userstats",
            group = "Player Commands",
            badArgumentsMessage = "command_userstats_args",
            helpMessage = "command_userstats_help",
            arguments = {"--player", "<PLAYERNAME>"},
            permission = "npcpolice.stats.userstats",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean userCommand_UserStats(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        policeRef.getMessageManager.sendMessage(sender, "player_stats.mystats_information", playerRecord);
        return true;
    }

    @CommandInfo(
            name = "changebounty",
            group = "Player Commands",
            badArgumentsMessage = "command_changebounty_args",
            helpMessage = "command_changebounty_help",
            arguments = {"--player|#", "<PLAYERNAME>", "#"},
            permission = "npcpolice.admin.changebounty",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_ChangeBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        if (inargs.length > 0 && policeRef.getUtilities.isNumeric(inargs[1])) {
            playerRecord.changeBounty(JAILED_BOUNTY.MANUAL, Integer.parseInt(inargs[1]));
            if (playerRecord.getBounty() < 0)
                playerRecord.setBounty(0);

            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_changebounty", playerRecord);
            if (playerRecord.isOnline())
                policeRef.getMessageManager.sendMessage(playerRecord.getPlayer(), "general_messages.player_bounty_changed", playerRecord);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_changebounty_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "clearrecord",
            group = "Player Commands",
            badArgumentsMessage = "command_clearrecord_args",
            helpMessage = "command_clearrecord_help",
            arguments = {"--player", "<PLAYERNAME>"},
            permission = "npcpolice.admin.clearrecord",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_ClearRecord(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_clearrecord_usage", playerRecord);
            return true;
        }

        playerRecord.clearRecord();
        policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_changebounty", playerRecord);
        if (playerRecord.isOnline())
            policeRef.getMessageManager.sendMessage(playerRecord.getPlayer(), "general_messages.player_bounty_changed", playerRecord);
        return true;
    }

    @CommandInfo(
            name = "clearwanted",
            group = "Player Commands",
            badArgumentsMessage = "command_clearwanted_args",
            helpMessage = "command_clearwanted_help",
            arguments = {"--player", "<PLAYERNAME>"},
            permission = "npcpolice.admin.clearwanted",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_ClearWanted(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_clearwanted_usage", playerRecord);
            return true;
        }

        playerRecord.clearWanted();
        policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_changebounty", playerRecord);
        return true;
    }

    @CommandInfo(
            name = "setbounty",
            group = "Player Commands",
            badArgumentsMessage = "command_setbounty_args",
            helpMessage = "command_setbounty_help",
            arguments = {"--player|#", "<PLAYERNAME>", "#"},
            permission = "npcpolice.admin.setbounty",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_SetBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        if (inargs.length > 0 && policeRef.getUtilities.isNumeric(inargs[1])) {
            playerRecord.setBounty(Integer.parseInt(inargs[1]), JAILED_BOUNTY.MANUAL);
            if (playerRecord.getBounty() < 0)
                playerRecord.setBounty(0);

            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_changebounty", playerRecord);
            if (playerRecord.isOnline())
                policeRef.getMessageManager.sendMessage(playerRecord.getPlayer(), "general_messages.player_bounty_changed", playerRecord);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setbounty_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "setjailseconds",
            group = "Player Commands",
            badArgumentsMessage = "command_setbounty_args",
            helpMessage = "command_setjailseconds_help",
            arguments = {"--player|#", "<PLAYERNAME>", "#"},
            permission = "npcpolice.admin.setjailseconds",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_SetJailSeconds(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        if (inargs.length > 0 && policeRef.getUtilities.isNumeric(inargs[1])) {
            playerRecord.setTime(Integer.parseInt(inargs[1]), JAILED_BOUNTY.MANUAL );

            if (playerRecord.getTime() < 0)
                playerRecord.setTime(0);

            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setjailseconds", playerRecord);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setjailseconds_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "setjailexpires",
            group = "Player Commands",
            badArgumentsMessage = "command_setjailexpires_args",
            helpMessage = "command_setjailexpires_help",
            arguments = {"--player|#", "<PLAYERNAME>", "#"},
            permission = "npcpolice.admin.setjailexpires",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_SetJailexpires(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        if (inargs.length > 0 && policeRef.getUtilities.isNumeric(inargs[1])) {
            playerRecord.setJailedExpires(new Date((new Date()).getTime() + Long.parseLong(inargs[1]) * 1000L));

            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setjailexpires", playerRecord);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setjailexpires_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "setstatus",
            group = "Player Commands",
            badArgumentsMessage = "command_setstatus_args",
            helpMessage = "command_setstatus_help",
            arguments = {"--player|WANTED|FREE", "<PLAYERNAME>", "WANTED|FREE"},
            permission = "npcpolice.admin.setstatus",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_SetStatus(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setstatus_usage", playerRecord);
            return true;
        }
        if (inargs.length > 0) {
            CURRENT_STATUS wantedStatus = CURRENT_STATUS.valueOf(inargs[1].toUpperCase());
            if (wantedStatus != null && (wantedStatus == CURRENT_STATUS.FREE || wantedStatus == CURRENT_STATUS.WANTED)) {
                policeRef.getPlayerManager.setPlayerStatus(playerRecord.getPlayer(), wantedStatus, WANTED_REASONS.USERCOMMAND);
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setstatus_changed", playerRecord);
            } else {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setstatus_bad", policeRef.getMessageManager.getResultMessage("result_Messages.wanted")[0] + ", " + policeRef.getMessageManager.getResultMessage("result_Messages.free")[0]);
            }
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setstatus_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "setlevel",
            group = "Player Commands",
            badArgumentsMessage = "command_setlevel_args",
            helpMessage = "command_setlevel_help",
            arguments = {"--player|NONE|MINIMUM|LOW|MEDIUM|HIGH", "<PLAYERNAME>", "NONE|MINIMUM|LOW|MEDIUM|HIGH"},
            permission = "npcpolice.admin.setlevel",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_SetLevel(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player))
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());

        else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setlevel_usage", playerRecord);
            return true;
        }
        if (inargs.length > 0) {
            WANTED_LEVEL wantedStatus = null;

            try {
                wantedStatus = WANTED_LEVEL.valueOf(inargs[1].toUpperCase());
            } catch (Exception err) {
            }

            if (wantedStatus != null) {
                policeRef.getPlayerManager.setPlayerWantedLevel(playerRecord.getPlayer(), wantedStatus, JAILED_BOUNTY.MANUAL);
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setlevel_changed", playerRecord);
            } else {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setlevel_bad", "NONE|MINIMUM|LOW|MEDIUM|HIGH");
            }
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_setlevel_usage", playerRecord);
        }
        return true;
    }

    @CommandInfo(
            name = "jailplayer",
            group = "Player Commands",
            badArgumentsMessage = "command_jailplayer_args",
            helpMessage = "command_jailplayer_help",
            arguments = {"--player", "<PLAYERNAME>", "#|--jail", "<jail>", "#"},
            permission = "npcpolice.admin.jailplayer",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean jailConfig_JailPlayer(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player)) {
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());
        } else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.command_jailplayer_args", playerRecord);
            return true;
        }

        if (selectedJail == null) {
            if (sender instanceof Player)
                selectedJail = policeRef.getJailManager.getJailAtLocation(((Player) sender).getLocation());
        }

        int bounty = 0;
        if (inargs.length > 1) {
            if (policeRef.getUtilities.isNumeric(inargs[1])) {
                bounty = Integer.parseInt(inargs[1]);
            }
        }

        if (selectedJail == null) {
            if (!playerRecord.isOnline()) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.command_jailplayer_args", playerRecord);
                return true;
            }

            playerRecord.sendPlayerToJail(bounty);
        } else {
            if (!playerRecord.isOnline()) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.command_jailplayer_args", playerRecord);
                return true;
            }

            playerRecord.sendPlayerToJail(selectedJail.jailName, bounty);
        }
        return true;
    }

    @CommandInfo(
            name = "unjailplayer",
            group = "Player Commands",
            badArgumentsMessage = "command_unjailplayer_args",
            helpMessage = "command_unjailplayer_help",
            arguments = {"--player", "<PLAYERNAME>"},
            permission = "npcpolice.admin.unjailplayer",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_UnJailPlayer(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (playerRecord == null && (sender instanceof Player)) {
            playerRecord = policeRef.getPlayerManager.getPlayer(((Player) sender).getUniqueId());
        } else if (playerRecord == null && !(sender instanceof Player)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.command_unjailplayer_args", playerRecord);
            return true;
        }

        if (!playerRecord.isOnline()) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.command_unjailplayer_args", playerRecord);
            return true;
        }

        if (playerRecord.getCurrentStatus() != CURRENT_STATUS.FREE) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.command_unjailplayer_notjailed", playerRecord);
            return true;
        }

        playerRecord.releasePlayer();
        policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_unjailplayer_released", playerRecord);

        return true;
    }
}
