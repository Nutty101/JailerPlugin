package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands_Admin {
    @CommandInfo(
            name = "reload",
            group = "Admin Commands",
            badArgumentsMessage = "command_reload_args",
            helpMessage = "command_reload_help",
            arguments = {""},
            permission = "npcpolice.admin.reload",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_ReloadConfig(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        policeRef.getDefaultConfigs();
        policeRef.getJailManager.loadJailSettings();
        policeRef.getLanguageManager.loadLanguages(true);
        if (sender instanceof Player)
            policeRef.getMessageManager.sendMessage(sender, "general_messages.configs_reloaded", ((Player) sender).getDisplayName().toString());
        if (!(sender instanceof Player))
            policeRef.getMessageManager.sendMessage(sender, "console_messages.configs_reloaded");
        return true;
    }

    @CommandInfo(
            name = "version",
            group = "Admin Commands",
            badArgumentsMessage = "command_version_args",
            helpMessage = "command_version_help",
            arguments = {""},
            permission = "npcpolice.admin.version",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_CurrentVersion(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        // Configuration Commands
        if (sender instanceof Player) {
            policeRef.getMessageManager.sendJsonRaw((Player) sender, "[{\"text\":\"--\",\"color\":\"dark_aqua\"},{\"text\":\"[\",\"color\":\"white\"},{\"text\":\"NPC Police By Nutty101\",\"color\":\"yellow\"},{\"text\":\"]\",\"color\":\"white\"},{\"text\":\"-----------------------\",\"color\":\"dark_aqua\"}]");
            policeRef.getMessageManager.sendJsonRaw((Player) sender, "[{\"text\":\"Version\",\"color\":\"green\"},{\"text\":\":\",\"color\":\"yellow\"},{\"text\":\" " + policeRef.pluginInstance.getDescription().getVersion() + " \",\"color\":\"white\"}]");
            policeRef.getMessageManager.sendJsonRaw((Player) sender, "[{\"text\":\"Plugin Link\",\"color\":\"dark_green\"},{\"text\":\": \",\"color\":\"yellow\"},{\"text\":\"https://www.spigotmc.org/resources/npc-police.9553/\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/npc-police.9553/\"}}]");
        } else {
            sender.sendMessage("--[NPC Police By Nutty101]-----------------------");
            sender.sendMessage("Version: " + policeRef.pluginInstance.getDescription().getVersion());
            sender.sendMessage("Plugin Link: https://www.spigotmc.org/resources/npc-police.9553");
        }
        return true;
    }

    @CommandInfo(
            name = "save",
            group = "Admin Commands",
            badArgumentsMessage = "command_save_args",
            helpMessage = "command_save_help",
            arguments = {""},
            permission = "npcpolice.admin.save",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_SaveConfig(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        policeRef.getDefaultConfigs();
        policeRef.getJailManager.saveJailSettings();
        return true;
    }
}
