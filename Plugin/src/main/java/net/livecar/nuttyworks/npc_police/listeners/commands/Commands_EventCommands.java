package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_BannedItemManager;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands_EventCommands {
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

}
