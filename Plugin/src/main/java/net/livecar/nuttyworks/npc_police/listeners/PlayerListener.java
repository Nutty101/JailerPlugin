package net.livecar.nuttyworks.npc_police.listeners;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_LockedInventory;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.listeners.commands.Pending_Command;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private NPC_Police getStorageReference = null;

    public PlayerListener(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Validate if we have checked bungeecord yet or not.
        if (getStorageReference.getBungeeListener.bungeeCordEnabled == STATE_SETTING.NOTSET) {
            getStorageReference.getBungeeListener.startBungeeChecks(event.getPlayer());
        }

        getStorageReference.getDatabaseManager.queueLoadPlayerRequest(event.getPlayer().getUniqueId());
        if (getStorageReference.getSentinelPlugin != null && event.getPlayer().isOp()) {

            if (getStorageReference.getSentinelPlugin.alertOpToIssues(event.getPlayer()))
            {
                final Player plr = event.getPlayer();
                Bukkit.getServer().getScheduler().runTaskLater(
                        getStorageReference.pluginInstance, () -> getStorageReference.getMessageManager.sendMessage(plr, "general_messages.sentinel_issue"), 10
                );

            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!getStorageReference.pluginInstance.isEnabled())
            return;

        Arrest_Record plrRecord = this.getStorageReference.getPlayerManager.getPlayer(event.getPlayer().getUniqueId());
        getStorageReference.getDatabaseManager.queueRemovePlayerRequest(plrRecord);
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return;
        }

        Player player = event.getPlayer();
        if (getStorageReference.getPlayerManager.pendingCommands.containsKey(player.getUniqueId())) {
            Pending_Command pendingCmd = getStorageReference.getPlayerManager.pendingCommands.get(player.getUniqueId());
            if (event.getClickedBlock().getType() == pendingCmd.blockType) {
                pendingCmd.commandString = pendingCmd.commandString + " " + String.valueOf(event.getClickedBlock().getLocation().getBlockX()) + "," + String.valueOf(event.getClickedBlock().getLocation().getBlockY()) + "," + String.valueOf(event.getClickedBlock().getLocation().getBlockZ());
                getStorageReference.getCommandManager.onCommand(event.getPlayer(), pendingCmd.commandString.split(" "));
                event.setCancelled(true);
                return;
            }
        }
        if (event.getClickedBlock().getType() == Material.CHEST) {
            Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(event.getPlayer().getUniqueId());
            if (plrRecord == null)
                return;

            if (plrRecord.getCurrentStatus() == CURRENT_STATUS.FREE && plrRecord.getLockedInventory() != null) {
                for (Jail_Setting jailSetting : getStorageReference.getJailManager.getWorldJails(event.getPlayer().getWorld().getName())) {
                    if (jailSetting.lockedInventoryLocation != null) {
                        if (event.getClickedBlock().getLocation().distanceSquared(jailSetting.lockedInventoryLocation) < 3.0D) {
                            if (this.getStorageReference.getVersionBridge.isSameChest(jailSetting.lockedInventoryLocation, event.getClickedBlock().getLocation())) {
                                JailerGUI_LockedInventory guiMenu = new JailerGUI_LockedInventory(jailSetting.displayName, 54, this.getStorageReference, player);
                                int chestID = 0;
                                for (int slotID = 0; slotID < plrRecord.getLockedInventory().length; slotID++) {
                                    if (slotID > 53) {
                                        break;
                                    }
                                    if ((plrRecord.getLockedInventory()[slotID] != null) && (plrRecord.getLockedInventory()[slotID].getType() != Material.AIR)) {
                                        guiMenu.setSlotItem(chestID, plrRecord.getLockedInventory()[slotID]);
                                        chestID++;
                                    }
                                }
                                guiMenu.open();
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NPC")) {
            NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
            NPCPolice_Trait trait = null;
            if ((npc != null) && (npc.hasTrait(NPCPolice_Trait.class))) {
                trait = npc.getTrait(NPCPolice_Trait.class);
            }

            if (trait == null)
                return;

            final Player player = event.getPlayer();

            // Check if the player is using an npc stick
            if (getStorageReference.getVersionBridge.getMainHand(player) != null && getStorageReference.getVersionBridge.getMainHand(player).getType() == Material.STICK && getStorageReference.getVersionBridge.getMainHand(player).getItemMeta().getDisplayName() != null && getStorageReference.getVersionBridge.getMainHand(player).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&eNPCPolice &2[&fNPCStick&2]"))) {
                // NPC settings.
                getStorageReference.getCommandManager.onCommand(player, new String[]{"npc"});
                return;
            }

            if (!player.hasPermission("npcpolice.gui.interact")) {
                return;
            } else {
                if (!trait.hasMenu) {
                    return;
                }
                getStorageReference.getGUIManager.Jailer_Menu(event, npc);
            }
        }
    }
}
