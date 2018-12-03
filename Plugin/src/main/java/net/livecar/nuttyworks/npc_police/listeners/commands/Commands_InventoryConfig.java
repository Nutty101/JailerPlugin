package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

public class Commands_InventoryConfig {

    @CommandInfo(
            name = "setinvonarrest",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setinvonarrest_args",
            helpMessage = "command_setinvonarrest_help",
            arguments = {"--jail", "<jail>"},
            permission = "npcpolice.settings.inventory.arrest",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean inventorySettings_setinvonarrest(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (selectedJail != null) {
            if (selectedJail.onArrest_InventoryAction == STATE_SETTING.NOTSET)
                selectedJail.onArrest_InventoryAction = STATE_SETTING.FALSE;
            else if (selectedJail.onArrest_InventoryAction == STATE_SETTING.FALSE)
                selectedJail.onArrest_InventoryAction = STATE_SETTING.TRUE;
            else if (selectedJail.onArrest_InventoryAction == STATE_SETTING.TRUE)
                selectedJail.onArrest_InventoryAction = STATE_SETTING.NOTSET;
        } else {
            if (policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction() == STATE_SETTING.NOTSET)
                policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction(STATE_SETTING.FALSE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction() == STATE_SETTING.FALSE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction(STATE_SETTING.TRUE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction() == STATE_SETTING.TRUE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onArrest_InventoryAction(STATE_SETTING.NOTSET);
        }

        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setinvonescape",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setinvonescape_args",
            helpMessage = "command_setinvonescape_help",
            arguments = {"--jail", "<jail>"},
            permission = "npcpolice.settings.inventory.escape",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean inventorySettings_setinvonescape(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (selectedJail != null) {
            if (selectedJail.onEscape_InventoryAction == STATE_SETTING.NOTSET)
                selectedJail.onEscape_InventoryAction = STATE_SETTING.FALSE;
            else if (selectedJail.onEscape_InventoryAction == STATE_SETTING.FALSE)
                selectedJail.onEscape_InventoryAction = STATE_SETTING.TRUE;
            else if (selectedJail.onEscape_InventoryAction == STATE_SETTING.TRUE)
                selectedJail.onEscape_InventoryAction = STATE_SETTING.NOTSET;
        } else {
            if (policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction() == STATE_SETTING.NOTSET)
                policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction(STATE_SETTING.FALSE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction() == STATE_SETTING.FALSE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction(STATE_SETTING.TRUE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction() == STATE_SETTING.TRUE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onEscape_InventoryAction(STATE_SETTING.NOTSET);
        }

        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }

    @CommandInfo(
            name = "setinvonfree",
            group = "Configuration Defaults",
            badArgumentsMessage = "command_setinvonfree_args",
            helpMessage = "command_setinvonfree_help",
            arguments = {"--jail", "<jail>"},
            permission = "npcpolice.settings.inventory.free",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean inventorySettings_setinvonfree(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (!policeRef.getJailManager.containsWorld(serverWorld)) {
            // Add the world to the settings
            policeRef.getJailManager.addWorldSetting(serverWorld, new World_Setting(serverWorld));
        }

        if (selectedJail != null) {
            if (selectedJail.onFree_InventoryAction == STATE_SETTING.NOTSET)
                selectedJail.onFree_InventoryAction = STATE_SETTING.FALSE;
            else if (selectedJail.onFree_InventoryAction == STATE_SETTING.FALSE)
                selectedJail.onFree_InventoryAction = STATE_SETTING.TRUE;
            else if (selectedJail.onFree_InventoryAction == STATE_SETTING.TRUE)
                selectedJail.onFree_InventoryAction = STATE_SETTING.NOTSET;
        } else {
            if (policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction() == STATE_SETTING.NOTSET)
                policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction(STATE_SETTING.FALSE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction() == STATE_SETTING.FALSE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction(STATE_SETTING.TRUE);
            else if (policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction() == STATE_SETTING.TRUE)
                policeRef.getJailManager.getWorldSettings(serverWorld).onFree_InventoryAction(STATE_SETTING.NOTSET);
        }

        if (selectedJail != null)
            policeRef.getCommandManager.registeredCommands.get("jail").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        else
            policeRef.getCommandManager.registeredCommands.get("world").invokeCommand(policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        return true;
    }
}
