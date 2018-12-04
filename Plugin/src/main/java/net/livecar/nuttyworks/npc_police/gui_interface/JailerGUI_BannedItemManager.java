package net.livecar.nuttyworks.npc_police.gui_interface;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JailerGUI_BannedItemManager implements Listener {
    private Player owningPlayer;
    private NPC_Police getStorageReference;
    private Inventory inventory;
    private World_Setting worldConfig;

    public JailerGUI_BannedItemManager(String name, int size, NPC_Police policeRef, Player player, World_Setting worldConfig) {
        this.getStorageReference = policeRef;
        this.owningPlayer = player;
        policeRef.pluginInstance.getServer().getPluginManager().registerEvents(this, policeRef.pluginInstance);
        inventory = Bukkit.createInventory(player, size, name);
        this.worldConfig = worldConfig;
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
            worldConfig.bannedItems = inventory.getContents();
            getStorageReference.getMessageManager.sendMessage(owningPlayer, "general_messages.config_command_banneditems_updated");
            destroy();
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            if (event.getRawSlot() > 53)
                return;

            if (event.isShiftClick()) {
                if (event.getCurrentItem() != null)
                    event.getCurrentItem().setAmount(1);
            } else if (event.isRightClick()) {

                if (event.getCurrentItem().getAmount() > 3) {
                    event.getCurrentItem().setAmount(1);
                    getStorageReference.getMessageManager.sendMessage(this.owningPlayer, "general_messages.banneditem_1");
                    event.setCancelled(true);
                } else {
                    event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                    getStorageReference.getMessageManager.sendMessage(this.owningPlayer, "general_messages.banneditem_" + String.valueOf(event.getCurrentItem().getAmount()));
                    event.setCancelled(true);
                }
            } else if (event.isLeftClick()) {
                if (event.getCurrentItem() != null)
                    event.getCurrentItem().setAmount(1);
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
