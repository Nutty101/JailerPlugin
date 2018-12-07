package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.particles.PlayParticle_ShowCell;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Date;
import java.util.Map;

public class Commands_JailConfig {

    @CommandInfo(
            name = "jail",
            group = "Jail Configuration",
            badArgumentsMessage = "command_jail_args",
            helpMessage = "command_jail_help",
            arguments = {"--jail", "<jail>"},
            permission = "npcpolice.config.jails.settings",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_ShowJail(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (selectedJail == null && (sender instanceof Player))
            selectedJail = policeRef.getJailManager.getJailAtLocation(((Player) sender).getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_cells_nojail");
            return true;
        }

        policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", selectedJail);
        return true;
    }

    @CommandInfo(
            name = "listjails",
            group = "Jail Configuration",
            badArgumentsMessage = "command_listjails_args",
            helpMessage = "command_listjails_help",
            arguments = {"--world|#", "<world>"},
            permission = "npcpolice.config.listjails.settings",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean jailConfig_ListJails(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {

        if (sender instanceof ConsoleCommandSender)
            return false;

        policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_listjails");

        int maxRecords = 10;
        if (inargs.length > 1)
            maxRecords = Integer.parseInt(inargs[1]);

        int cnt = 0;
        for (Map.Entry jailRecord : policeRef.getJailManager.getWorldJails(((Player)sender).getLocation())) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_listjails_detail",(Jail_Setting)jailRecord.getValue());
            cnt++;
            if (cnt >= maxRecords)
                break;
        }
        return true;
    }

    @CommandInfo(
            name = "rename",
            group = "Jail Configuration",
            badArgumentsMessage = "command_rename_args",
            helpMessage = "command_rename_help",
            arguments = {"--jail|displayname", "<jail>", "displayname"},
            permission = "npcpolice.config.jails.rename",
            allowConsole = true,
            minArguments = 1,
            maxArguments = 5
    )
    public boolean jailConfig_Rename(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (selectedJail == null && (sender instanceof Player))
            selectedJail = policeRef.getJailManager.getJailAtLocation(((Player) sender).getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_cells_nojail");
            return true;
        }

        StringBuilder longName = new StringBuilder();
        for (int cnt = 1; cnt < inargs.length; cnt++) {
            longName.append(inargs[cnt]);
            longName.append(" ");
        }

        selectedJail.displayName = longName.toString().trim();
        if (selectedJail.displayName.length() > 20)
            selectedJail.displayName = selectedJail.displayName.substring(0, 20);

        // Need to run the jail settings
        policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", selectedJail);
        return true;
    }

    @CommandInfo(
            name = "createjail",
            group = "Jail Configuration",
            badArgumentsMessage = "command_createjail_args",
            helpMessage = "command_createjail_help",
            arguments = {"jailshortname", "<region>", "displayname"},
            permission = "npcpolice.config.jails.create",
            allowConsole = false,
            minArguments = 3,
            maxArguments = 10
    )
    public boolean jailConfig_Create(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        String jailName = inargs[1];
        String regionName = inargs[2];
        StringBuilder longName = new StringBuilder();
        for (int cnt = 3; cnt < inargs.length; cnt++) {
            longName.append(inargs[cnt]);
            longName.append(" ");
        }
        String jailLong = longName.toString().trim();
        if (jailLong.length() > 25)
            jailLong = jailLong.substring(0, 25);

        if (jailName.length() > 10)
            jailName = jailName.substring(0, 10);

        Player player = (Player) sender;
        if (serverWorld.isEmpty())
            serverWorld = player.getWorld().getName();

        if (!policeRef.getWorldGuardPlugin.hasRegion(serverWorld, regionName)) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_invalidregion", regionName);
            return true;
        }

        if (policeRef.getJailManager.getJailByName(jailName) != null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_create_nameexists", jailName);
            return true;
        }

        if (policeRef.getJailManager.getJailByRegion(regionName) != null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_create_regionexists", jailName);
            return true;
        }

        Jail_Setting newJail = new Jail_Setting(serverWorld, jailName, regionName, longName.toString().trim());
        policeRef.getJailManager.putJail(serverWorld, newJail);

        // Need to run the jail settings
        policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", newJail);
        return true;
    }

    @CommandInfo(
            name = "deletejail",
            group = "Jail Configuration",
            badArgumentsMessage = "command_deletejail_args",
            helpMessage = "command_deletejail_help",
            arguments = {"<jail>"},
            permission = "npcpolice.config.jails.delete",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean jailConfig_DeleteJail(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (inargs.length == 1) {
            Player player = (Player) sender;

            if (selectedJail == null) {
                selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());
                if (selectedJail == null) {
                    policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_cells_nojail");
                    return true;
                }
            }

            policeRef.getJailManager.removeJail(player.getWorld(), selectedJail);
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_removed", selectedJail.jailName);
            return true;
        }

        String jailName = inargs[1];

        Jail_Setting jailSetting = policeRef.getJailManager.getJailByName(jailName);
        if (jailSetting == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_nojail", jailName);
            return true;
        }
        policeRef.getJailManager.removeJail(jailSetting.jailWorld, jailSetting);
        policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_removed", jailName);
        return true;
    }

    @CommandInfo(
            name = "gocell",
            group = "Jail Configuration",
            badArgumentsMessage = "command_gocell_args",
            helpMessage = "command_gocell_help",
            arguments = {"--jail|#", "<jail>", "#"},
            permission = "npcpolice.config.jails.gocell",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_GoCell(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (selectedJail == null) {
            selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());
            if (selectedJail == null) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_jail_cells_nojail");
                return true;
            }
        }

        if (policeRef.getUtilities.isNumeric(inargs[1])) {
            int cellIndex = Integer.parseInt(inargs[1]);
            if (cellIndex > selectedJail.cellLocations.size()) {
                policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
                return true;
            } else {
                player.teleport(selectedJail.cellLocations.get(cellIndex), TeleportCause.PLUGIN);
                return true;
            }
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
            return true;
        }
    }

    @CommandInfo(
            name = "addcell",
            group = "Jail Configuration",
            badArgumentsMessage = "command_addcell_args",
            helpMessage = "command_addcell_help",
            arguments = {""},
            permission = "npcpolice.config.jails.addcell",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_AddCell(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        Jail_Setting jailSetting = policeRef.getJailManager.getJailAtLocation(player.getLocation());
        if (jailSetting == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        jailSetting.cellLocations.add(player.getLocation().clone().add(0, 1, 0));
        policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", jailSetting);
        return true;
    }

    @CommandInfo(
            name = "removecell",
            group = "Jail Configuration",
            badArgumentsMessage = "command_removecell_args",
            helpMessage = "command_removecell_help",
            arguments = {"--jail|#", "<jail>", "#"},
            permission = "npcpolice.config.jails.removecell",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 5
    )
    public boolean jailConfig_RemoveCell(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        Jail_Setting jailSetting = policeRef.getJailManager.getJailAtLocation(player.getLocation());
        if (jailSetting == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        for (Location cellLocation : jailSetting.cellLocations) {
            if (cellLocation.distanceSquared(player.getLocation()) < 6) {
                jailSetting.cellLocations.remove(cellLocation);
                break;
            }
        }

        // Need to run the jail settings
        policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", jailSetting);
        return true;
    }

    @CommandInfo(
            name = "showcells",
            group = "Jail Configuration",
            badArgumentsMessage = "command_showcells_args",
            helpMessage = "command_showcells_help",
            arguments = {"--jail", "<jail>"},
            permission = "npcpolice.config.jails.showcells",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_ShowCell(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;
        if (selectedJail == null)
            selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        new PlayParticle_ShowCell(policeRef, player, selectedJail).runTaskTimer(policeRef.pluginInstance, 10, 20);
        return true;
    }

    @CommandInfo(
            name = "setregion",
            group = "Jail Configuration",
            badArgumentsMessage = "command_setregion_args",
            helpMessage = "command_setregion_help",
            arguments = {"--jail|<region>", "<jail>", "<region>"},
            permission = "npcpolice.config.jails.region",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean jailConfig_setRegion(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (selectedJail == null)
            selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        String regionName = inargs[1];
        if (!policeRef.getWorldGuardPlugin.hasRegion(player.getWorld(), regionName)) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_invalidregion", regionName);
            return true;
        }

        selectedJail.regionName = regionName;
        policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", selectedJail);
        return true;

    }

    @CommandInfo(
            name = "setexit",
            group = "Jail Configuration",
            badArgumentsMessage = "command_setexit_args",
            helpMessage = "command_setexit_help",
            arguments = {"--jail|clear", "<jail>"},
            permission = "npcpolice.config.jails.setexit",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean jailConfig_SetExit(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (selectedJail == null)
            selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        if (selectedJail.freeSpawnPoint != null)
            selectedJail.freeSpawnPoint = null;
        else
            selectedJail.freeSpawnPoint = player.getLocation().clone().add(0, 1, 0);

        policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setchestloc",
            group = "Jail Configuration",
            badArgumentsMessage = "command_setchestloc_args",
            helpMessage = "command_setchestloc_help",
            arguments = {"--jail|set", "<jail>|X,Y,Z"},
            permission = "npcpolice.config.jails.region",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean jailConfig_setChestloc(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        Player player = (Player) sender;

        if (selectedJail == null)
            selectedJail = policeRef.getJailManager.getJailAtLocation(player.getLocation());

        if (selectedJail == null) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_jail_cells_nojail");
            return true;
        }

        if (inargs.length == 1) {
            selectedJail.lockedInventoryLocation = null;
            policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", selectedJail);
            return true;
        } else if (inargs.length == 2 && inargs[1].equalsIgnoreCase("set")) {
            policeRef.getMessageManager.sendMessage(player, "general_messages.config_command_setchestloc_start");
            Pending_Command newPending = new Pending_Command();
            newPending.commandString = "setchestloc --jail " + selectedJail.jailName;
            newPending.timeoutMessage = "general_messages.config_command_blocklocation_timeout";
            newPending.timeOutTime = new Date(new Date().getTime() + 10000);
            newPending.blockType = Material.CHEST;
            policeRef.getPlayerManager.pendingCommands.put(player.getUniqueId(), newPending);
            return true;
        } else if (inargs.length == 2 && inargs[1].trim().contains(",")) {
            String[] parts = inargs[1].trim().split(",");
            if (parts.length == 3) {
                for (int part = 0; part < parts.length; part++)
                    if (!policeRef.getUtilities.isNumeric(parts[part])) {
                        policeRef.getMessageManager.sendMessage(player, "general_messages.config_command_setchestloc_args");
                        return true;
                    }
                Location newLocation = new Location(player.getWorld(), Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                if (newLocation.getBlock().getType() != Material.CHEST) {
                    policeRef.getMessageManager.sendMessage(player, "general_messages.config_command_setchestloc_nochest");
                    return true;
                }

                selectedJail.lockedInventoryLocation = newLocation;
            }
        }
        policeRef.getMessageManager.sendMessage(player, "jail_settings.jail_information", selectedJail);
        return true;

    }
}
