package net.livecar.nuttyworks.npc_police.gui_interface;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JailerGUI_LockedInventory implements Listener {
    private Player owningPlayer;
    private NPC_Police getStorageReference = null;
    private Inventory inventory = null;

    public JailerGUI_LockedInventory(String name, int size, NPC_Police policeRef, Player player) {
        this.getStorageReference = policeRef;
        this.owningPlayer = player;
        policeRef.pluginInstance.getServer().getPluginManager().registerEvents(this, policeRef.pluginInstance);
        inventory = Bukkit.createInventory(player, size, name);
    }

    public void setSlotItem(int index, ItemStack item) {
        inventory.setItem(index, item);
    }

    public void open() {
        owningPlayer.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(event.getPlayer().getUniqueId());
            plrRecord.clearLockedInventory();
            plrRecord.addToLockedInventory(event.getInventory().getContents());
            destroy();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                    if (event.getRawSlot() > 53) {
                        event.setCancelled(true);
                    }
                    break;
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                    if (event.getRawSlot() < 54)
                        event.setCancelled(true);
                    break;
                default:
                    break;

            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            for (int slotID : event.getRawSlots()) {
                if (slotID < 54)
                    event.setCancelled(true);
            }
        }
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        owningPlayer = null;
        inventory = null;
        getStorageReference = null;
    }
}
