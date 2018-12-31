package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.api.Enumerations;
import net.livecar.nuttyworks.npc_police.api.Enumerations.KICK_TYPE;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_BannedItemManager;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands_World {
    @CommandInfo(
            name = "world",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_world_args",
            helpMessage = "command_world_help",
            arguments = {"--world", ""},
            permission = "npcpolice.settings.show",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean worldConfig_ShowConfig(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
        return true;
    }

    @CommandInfo(
            name = "banneditems",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_banneditems_args",
            helpMessage = "command_banneditems_help",
            arguments = {"--world", ""},
            permission = "npcpolice.settings.banneditems",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean worldConfig_BannedItems(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        JailerGUI_BannedItemManager guiMenu = new JailerGUI_BannedItemManager("Banned Items", 54, policeRef, (Player) sender, worldConfig);
        int chestID = 0;
        for (int slotID = 0; slotID < worldConfig.bannedItems.length; slotID++) {
            if (slotID > 53)
                break;
            if (worldConfig.bannedItems[slotID] != null && worldConfig.bannedItems[slotID].getType() != Material.AIR) {
                guiMenu.setSlotItem(chestID, worldConfig.bannedItems[slotID]);
                chestID++;
            }
        }
        guiMenu.open();
        return true;
    }

    @CommandInfo(
            name = "kicktype",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_kicktype_args",
            helpMessage = "command_kicktype_help",
            arguments = {"onchange-server|onchange-world|onarrest-server|onarrest-world"},
            permission = "npcpolice.settings.kick.type",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean worldConfig_KickType(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length > 0) {
            if (inargs.length == 1) {
                worldConfig.setKickType(worldConfig.getKickType().next());
            }
        }
        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
        return true;
    }

    @CommandInfo(
            name = "sendlocation",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_sendlocation_args",
            helpMessage = "command_sendlocation_help",
            arguments = {"--world|<world>|<server>", "<world>|<server>", "<world>|<server>"},
            permission = "npcpolice.settings.kick.sendlocation",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean worldConfig_SendLocation(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length > 0) {
            if (inargs.length == 2 && (worldConfig.getKickType() == KICK_TYPE.ARREST_WORLD || worldConfig.getKickType() == KICK_TYPE.CHANGE_WORLD)) {
                for (org.bukkit.World svrWorld : Bukkit.getWorlds()) {
                    if (svrWorld.getName().equalsIgnoreCase(inargs[1])) {
                        worldConfig.setKickLocation(inargs[1]);
                        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
                        return true;
                    }
                }
            } else if (inargs.length == 2 && (worldConfig.getKickType() == KICK_TYPE.ARREST_SERVER || worldConfig.getKickType() == KICK_TYPE.CHANGE_SERVER)) {
                for (String server : policeRef.getBungeeListener.getServerList()) {
                    if (server.equalsIgnoreCase(inargs[1])) {
                        worldConfig.setKickLocation(inargs[1]);
                        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
                        return true;
                    }
                }
            }
        }
        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
        return true;
    }

    @CommandInfo(
            name = "setminwanted",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setminwanted_args",
            helpMessage = "command_setminwanted_help",
            arguments = {"--world|none|minimum|low|medium|high", "<world>", "none|minimum|low|medium|high"},
            permission = "npcpolice.settings.wanted.setminumum",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean worldConfig_SetMinWanted(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }


        if (selectedJail != null) {
            if (selectedJail.minWanted.next() == null) {
                selectedJail.minWanted = WANTED_LEVEL.GLOBAL;
            } else {
                if (selectedJail.minWanted.ordinal() > selectedJail.maxWanted.ordinal())
                    selectedJail.minWanted = WANTED_LEVEL.GLOBAL;
                else if (selectedJail.minWanted == selectedJail.minWanted.next())
                    selectedJail.minWanted = WANTED_LEVEL.GLOBAL;
                else
                    selectedJail.minWanted = selectedJail.minWanted.next();
            }
            // Need to run the jail settings
            policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", selectedJail);
            return true;

        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length == 1) {
            if (worldConfig.getMinimum_WantedLevel().next() == null)
                worldConfig.setMinimum_WantedLevel(WANTED_LEVEL.GLOBAL);
            else if (worldConfig.getMinimum_WantedLevel().next() == worldConfig.getMinimum_WantedLevel())
                worldConfig.setMinimum_WantedLevel(WANTED_LEVEL.GLOBAL);
            else
                worldConfig.setMinimum_WantedLevel(worldConfig.getMinimum_WantedLevel().next());

            if (worldConfig.getMaximum_WantedLevel().ordinal() < worldConfig.getMinimum_WantedLevel().ordinal())
                worldConfig.setMinimum_WantedLevel(WANTED_LEVEL.GLOBAL);

            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
            return true;

        } else if (inargs.length == 2) {
            if (WANTED_LEVEL.contains(inargs[2].toUpperCase())) {
                worldConfig.setMinimum_WantedLevel(WANTED_LEVEL.valueOf(inargs[2].toUpperCase()));
                policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
                return true;

            }
        }
        return false;
    }

    @CommandInfo(
            name = "setmaxwanted",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setmaxwanted_args",
            helpMessage = "command_setmaxwanted_help",
            arguments = {"--world|none|minimum|low|medium|high", "<world>", "none|minimum|low|medium|high"},
            permission = "npcpolice.settings.wanted.setminumum",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 2
    )
    public boolean worldConfig_SetMaxWanted(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (selectedJail != null) {
            if (selectedJail.maxWanted.next() == null) {
                selectedJail.maxWanted = WANTED_LEVEL.GLOBAL;
            } else {
                if (selectedJail.maxWanted.ordinal() < selectedJail.minWanted.ordinal())
                    selectedJail.maxWanted = selectedJail.minWanted;
                else if (selectedJail.maxWanted == selectedJail.maxWanted.next())
                    selectedJail.maxWanted = selectedJail.minWanted;
                else
                    selectedJail.maxWanted = selectedJail.maxWanted.next();
            }
            // Need to run the jail settings
            policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", selectedJail);
            return true;

        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);

        if (inargs.length == 1) {
            if (worldConfig.getMaximum_WantedLevel().next() == null) {
                worldConfig.setMaximum_WantedLevel(worldConfig.getMinimum_WantedLevel());
            } else {
                if (worldConfig.getMaximum_WantedLevel().ordinal() < worldConfig.getMinimum_WantedLevel().ordinal())
                    worldConfig.setMaximum_WantedLevel(worldConfig.getMinimum_WantedLevel());
                if (worldConfig.getMaximum_WantedLevel().next() == worldConfig.getMaximum_WantedLevel())
                    worldConfig.setMaximum_WantedLevel(worldConfig.getMinimum_WantedLevel());
                else
                    worldConfig.setMaximum_WantedLevel(worldConfig.getMaximum_WantedLevel().next());
            }

            if (serverWorld.equalsIgnoreCase("_GlobalSettings") && worldConfig.getMaximum_WantedLevel() == WANTED_LEVEL.GLOBAL) {
                worldConfig.setMaximum_WantedLevel(WANTED_LEVEL.NONE);
                if (worldConfig.getMaximum_WantedLevel().ordinal() < worldConfig.getMinimum_WantedLevel().ordinal())
                    worldConfig.setMaximum_WantedLevel(worldConfig.getMinimum_WantedLevel());
            }


            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
            return true;

        } else if (inargs.length == 2) {
            if (WANTED_LEVEL.contains(inargs[2].toUpperCase())) {
                worldConfig.setMaximum_WantedLevel(WANTED_LEVEL.valueOf(inargs[2].toUpperCase()));
                policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
                return true;

            }
        }
        return false;
    }

    @CommandInfo(
            name = "escapesetting",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_escapesetting_args",
            helpMessage = "command_escapesetting_help",
            arguments = {"--world|--jail", "<world>|<jail>"},
            permission = "npcpolice.settings.escapesetting",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean worldConfig_EscapeSetting(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (selectedJail != null) {
            if (selectedJail.escapeSetting.next() == null)
                selectedJail.escapeSetting = Enumerations.ESCAPE_SETTING.NOTSET;
            else
                selectedJail.escapeSetting = selectedJail.escapeSetting.next();

            // Need to run the jail settings
            policeRef.getMessageManager.sendMessage(sender, "jail_settings.jail_information", selectedJail);
            return true;

        }

        World_Setting worldConfig = policeRef.getJailManager.getWorldSettings(serverWorld);
        if (worldConfig.getEscapeSetting().next() == null)
            worldConfig.setEscapeSetting(Enumerations.ESCAPE_SETTING.NOTSET);
        else
            worldConfig.setEscapeSetting(worldConfig.getEscapeSetting().next());

        policeRef.getMessageManager.sendMessage(sender, "world_settings.settings_menu", selectedWorld);
        return true;
    }


}
