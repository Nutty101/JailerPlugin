package net.livecar.nuttyworks.npc_police.bridges;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

public class MCUtils_1_9_R2 extends MCUtilsBridge {

    private EnumParticle particleType = EnumParticle.NOTE;

    @Override
    public void PlayOutParticle(Location partLocation, Player player) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particleType, false, (float) partLocation.getX(), (float) partLocation.getY(), (float) partLocation.getZ(), 0, 1, 0, (float) 0.1, 1, null);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void PlayOutParticle(String particle, Location partLocation, Player player) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle), false, (float) partLocation.getX(), (float) partLocation.getY(), (float) partLocation.getZ(), 0, 1, 0, (float) 0.1, 1, null);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
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
        im.setOwner(player.getName());
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

    private Double getEntityMaxY(Entity entity)
    {
        return ((CraftEntity) entity).getHandle().getBoundingBox().b;
    }

    private Double getEntityMinY(Entity entity)
    {
        return ((CraftEntity) entity).getHandle().getBoundingBox().e;
    }

    private boolean isHalfBlock(Material mat) {
        return mat.toString().contains("SLAB") || mat.toString().contains("STEP");
    }

    @SuppressWarnings("deprecation")
    private SLABTYPE getSlabType(Block block)
    {
        if (block.getType() == Material.DOUBLE_STEP || block.getType() == Material.DOUBLE_STONE_SLAB2 || block.getType() == Material.WOOD_DOUBLE_STEP)
            return SLABTYPE.DOUBLE;

        if (block.getType() == Material.STEP || block.getType() == Material.WOOD_STEP)
        {
            if (block.getData() < 8)
                return SLABTYPE.BOTTOM;
            else
                return SLABTYPE.TOP;
        }

        return SLABTYPE.NONSLAB;
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

    @Override
    public LineOfSight hasLineOfSight(LivingEntity entityA, Player player, int maxDistance, Player debug) {

        LineOfSight losResults = new LineOfSight();
        losResults.visability = 0.0;

        if (entityA.getLocation().distanceSquared(player.getLocation()) > 2304)
            return losResults;

        Vector entAVect = entityA.getLocation().subtract(player.getLocation().clone()).toVector().normalize();
        losResults.direction = entAVect.dot(entityA.getLocation().getDirection());

        //Traveling away
        if (losResults.direction > 0.0D)
            return losResults;

        Location entBLoc = player.getEyeLocation();

        int splitcnt = 5;
        Double entityHeight = (getEntityMaxY(player) - getEntityMinY(player));
        Double entityInterval = entityHeight/splitcnt;

        for (int cnt = 0;cnt < (splitcnt+1);cnt++) {
            losResults = testLineOfSight(losResults, entityA.getEyeLocation(), new Location(entBLoc.getWorld(), entBLoc.getX(), getEntityMinY(player)+(entityInterval*cnt), entBLoc.getZ()),maxDistance, debug);
            if (losResults.visability <= 0.00)
                return losResults;
        }

        return losResults;
    }

    private LineOfSight testLineOfSight(LineOfSight losResults, Location entityALoc, Location entityBLoc,  int maxDistance, Player debug) {

        Vector viewDirection = entityBLoc.clone().subtract(entityALoc).toVector().normalize();
        double distance = entityALoc.distanceSquared(entityBLoc);

        Location lo = entityALoc.clone();
        Location prior;

        int maxIterations = 0;

        while (true) {
            if (lo.distanceSquared(entityBLoc) < 0.5) {
                losResults.visability = 100.0;
                return losResults;
            } else if (lo.distanceSquared(entityALoc) > distance + 4) {
                losResults.visability = 0.0;
                return losResults;
            }

            losResults.visability -= getSolidLevel(lo.getBlock().getType());
            if (losResults.visability <= 0.0) {
                return losResults;
            }

            prior = lo.clone();
            lo.add(viewDirection);

            //Validate slabs?
            if(isHalfBlock(lo.getBlock().getType()))
            {

                Double priorY = prior.getY() - prior.getBlockY();
                Double newY = lo.getY() - lo.getBlockY();

                switch (getSlabType(lo.getBlock())) {
                    case TOP:
                        if ((newY > 0.499999999) || (priorY > 0.499999999)) {
                            losResults.visability = 0.0;
                            return losResults;
                        } else if ( prior.getY() != lo.getY() )
                            losResults.visability = 0.0;
                        return losResults;
                    case BOTTOM:
                        if ((newY < 0.5) || ((priorY < 0.5))) {
                            losResults.visability = 0.0;
                            return losResults;
                        } else if ( prior.getY() != lo.getY() )
                            losResults.visability = 0.0;
                        return losResults;
                    case DOUBLE:
                        losResults.visability = 0.0;
                        return losResults;
                    case NONSLAB:
                        losResults.visability = 0.0;
                        return losResults;
                }
            }

            maxIterations++;
            if (maxIterations > 500) {
                losResults.visability = 0.0;
                return losResults;
            }

            if (debug != null)
                spawnParticle(debug, lo);
        }
    }


    private void spawnParticle(Player plr, Location loc)
    {
        EnumParticle part = EnumParticle.VILLAGER_HAPPY;
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(part, false, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 1, 0, (float) 0.1, 1, null);
        ((CraftPlayer) plr).getHandle().playerConnection.sendPacket(packet);
    }
}

