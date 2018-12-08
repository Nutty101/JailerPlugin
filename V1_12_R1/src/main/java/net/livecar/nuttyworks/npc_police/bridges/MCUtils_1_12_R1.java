package net.livecar.nuttyworks.npc_police.bridges;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MCUtils_1_12_R1 extends MCUtilsBridge {

    private Particle particleType = Particle.NOTE;

    @Override
    public void PlayOutParticle(Location partLocation, Player player) {

        player.spawnParticle(particleType, partLocation.clone().add(0, 1, 0), 1);
    }

    @Override
    public void PlayOutParticle(String particle, Location partLocation, Player player) {

        Particle part = Particle.valueOf(particle.toString());
        if (part == null)
            return;

        player.spawnParticle(part, partLocation.clone().add(0, 1, 0), 1);
    }

    @Override
    public ItemStack getMainHand(Player plr) {
        return plr.getInventory().getItemInMainHand();
    }

    @Override
    public ItemStack getSecondHand(Player plr) {
        return plr.getInventory().getItemInOffHand();
    }

    @Override
    public Material getMaterialFromString(String material) {
        return Material.getMaterial(material);
    }

    @Override
    public ItemStack createPlayerHead(OfflinePlayer player) {
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1);
        SkullMeta im = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        im.setOwningPlayer(player);
        im.setDisplayName(player.getName());
        playerHead.setItemMeta(im);
        return playerHead;
    }

    @Override
    public Double getSolidLevel(Material material) {
        if (!material.isSolid()) {
            return Double.valueOf(0.0D);
        }
        switch (material) {
            case BIRCH_DOOR:
                return Double.valueOf(33.3D);
            case BIRCH_FENCE:
                return Double.valueOf(10.0D);
            case COCOA:
                return Double.valueOf(0.0D);
            case COOKED_MUTTON:
                return Double.valueOf(0.0D);
            case COOKIE:
                return Double.valueOf(0.0D);
            case DAYLIGHT_DETECTOR:
                return Double.valueOf(33.0D);
            case DIAMOND_BARDING:
                return Double.valueOf(15.0D);
            case DIAMOND_SWORD:
                return Double.valueOf(10.0D);
            case DOUBLE_PLANT:
                return Double.valueOf(20.0D);
            case DOUBLE_STEP:
                return Double.valueOf(5.0D);
            case EMERALD:
                return Double.valueOf(10.0D);
            case ENDER_STONE:
                return Double.valueOf(20.0D);
            case GLOWSTONE:
                return Double.valueOf(75.0D);
            case GOLD_BLOCK:
                return Double.valueOf(95.0D);
            case GOLD_CHESTPLATE:
                return Double.valueOf(0.0D);
            case GOLD_HELMET:
                return Double.valueOf(0.0D);
            case GOLD_LEGGINGS:
                return Double.valueOf(0.0D);
            case GRAVEL:
                return Double.valueOf(5.0D);
            case GREEN_RECORD:
                return Double.valueOf(33.3D);
            case HOPPER_MINECART:
                return Double.valueOf(0.0D);
            case IRON_BOOTS:
                return Double.valueOf(75.0D);
            case IRON_DOOR:
                return Double.valueOf(75.0D);
            case IRON_DOOR_BLOCK:
                return Double.valueOf(75.0D);
            case IRON_LEGGINGS:
                return Double.valueOf(0.0D);
            case IRON_ORE:
                return Double.valueOf(10.0D);
            case IRON_PICKAXE:
                return Double.valueOf(10.0D);
            case IRON_PLATE:
                return Double.valueOf(10.0D);
            case IRON_SPADE:
                return Double.valueOf(10.0D);
            case IRON_SWORD:
                return Double.valueOf(10.0D);
            case IRON_TRAPDOOR:
                return Double.valueOf(10.0D);
            case ITEM_FRAME:
                return Double.valueOf(10.0D);
            case JACK_O_LANTERN:
                return Double.valueOf(10.0D);
            case JUKEBOX:
                return Double.valueOf(10.0D);
            case JUNGLE_DOOR:
                return Double.valueOf(10.0D);
        }
        return Double.valueOf(100.0D);
    }

    @Override
    public boolean isSameChest(Location chestLocation, Location clickedLocation) {

        if (chestLocation.getBlock().getLocation().equals(clickedLocation.getBlock().getLocation()))
            return true;

        if (chestLocation.getBlock().getType() != Material.CHEST && chestLocation.getBlock().getType() != Material.TRAPPED_CHEST )
            return false;

        if (clickedLocation.getBlock().getType() != Material.CHEST && clickedLocation.getBlock().getType() != Material.TRAPPED_CHEST )
            return false;

        Chest chestInstance = (Chest)chestLocation.getBlock().getState();
        InventoryHolder chestInv = chestInstance.getInventory().getHolder();

        if (chestInv instanceof DoubleChest) {
            DoubleChest doubleChest = ((DoubleChest) chestInv);
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();

            if (leftChest.getLocation().getBlock().getLocation().equals(clickedLocation.getBlock().getLocation()))
                return true;

            if (rightChest.getLocation().getBlock().getLocation().equals(clickedLocation.getBlock().getLocation()))
                return true;
        }
        return false;
    }
}
