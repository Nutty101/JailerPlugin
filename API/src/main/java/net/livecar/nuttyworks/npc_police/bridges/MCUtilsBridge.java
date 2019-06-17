package net.livecar.nuttyworks.npc_police.bridges;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MCUtilsBridge {

    public Map<String,Material> getGuiMenuItems()
    {
        Map<String,Material> guiItems = new LinkedHashMap<>();
        guiItems.put("player_payment_menuitem",Material.GOLD_INGOT);
        guiItems.put("pay_others_menuitem",Material.IRON_FENCE);
        guiItems.put("list_wanted_menuiten",Material.REDSTONE);
        guiItems.put("list_escaped_menuitem",Material.PAPER);
        guiItems.put("close_menu_menuitem",Material.REDSTONE_BLOCK);
        return guiItems;
    };

    abstract public ItemStack getMainHand(Player plr);

    abstract public ItemStack getSecondHand(Player plr);

    abstract public Material getMaterialFromString(String material);

    abstract public ItemStack createPlayerHead(OfflinePlayer player);

    abstract public Double getSolidLevel(Material material);

    abstract public void PlayOutParticle(Location partLocation, Player player);

    abstract public void PlayOutParticle(String particleType, Location partLocation, Player player);

    abstract public boolean isSameChest(Location chestLocation, Location clickedLocation);

    abstract public LineOfSight hasLineOfSight(LivingEntity entityA, Player player, int maxDistance, Player debug);

    public enum SLABTYPE {
        TOP,
        BOTTOM,
        DOUBLE,
        NONSLAB
    }

}
