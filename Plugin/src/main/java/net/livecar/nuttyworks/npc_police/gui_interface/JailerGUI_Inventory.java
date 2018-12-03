package net.livecar.nuttyworks.npc_police.gui_interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class JailerGUI_Inventory implements Listener {

    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;

    private String[] optionNames;
    private ItemStack[] optionIcons;
    private Object[] optionCustomData;

    private Inventory inventory = null;

    public JailerGUI_Inventory(String name, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        this.optionCustomData = new Object[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public JailerGUI_Inventory setOption(int position, ItemStack icon, Object customData, String[] info) {

        for (int line = 0; line < info.length; line++)
            info[line] = ChatColor.translateAlternateColorCodes('&', info[line]);

        if (info.length == 1) {
            return setOption(position, icon, customData, info[0], "");
        } else {
            String[] sLines = new String[info.length - 1];
            System.arraycopy(info, 1, sLines, 0, info.length - 1);
            return setOption(position, icon, customData, info[0], sLines);
        }
    }

    public JailerGUI_Inventory setOption(int position, ItemStack icon, Object customData, String name, String... info) {
        for (int line = 0; line < info.length; line++)
            info[line] = ChatColor.translateAlternateColorCodes('&', info[line]);

        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        optionCustomData[position] = customData;
        return this;
    }

    public void open(Player player) {
        inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            destroy();
        }
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
        inventory = null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().hashCode() == inventory.hashCode()) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                Plugin plugin = this.plugin;
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot], optionIcons[slot], optionCustomData[slot]);
                handler.onOptionClick(e);
                if (e.willClose()) {
                    final Player p = (Player) event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            p.closeInventory();
                        }
                    }, 1);
                }
                if (e.willDestroy()) {
                    destroy();
                }
            }
        }
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event);
    }

    public class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private ItemStack item;
        private boolean close;
        private boolean destroy;
        private Object customObject;

        public OptionClickEvent(Player player, int position, String name, ItemStack clickeditem, Object CustomData) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.item = clickeditem;
            this.customObject = CustomData;
            this.close = true;
            this.destroy = false;
        }

        public Player getPlayer() {
            return player;
        }

        public int getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        public ItemStack getItem() {
            return item;
        }

        public Object getCustom() {
            return customObject;
        }

        public boolean willClose() {
            return close;
        }

        public boolean willDestroy() {
            return destroy;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }
}
