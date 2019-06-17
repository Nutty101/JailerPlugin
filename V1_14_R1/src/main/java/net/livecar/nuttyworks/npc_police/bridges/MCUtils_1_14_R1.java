package net.livecar.nuttyworks.npc_police.bridges;

import org.bukkit.*;
import org.bukkit.block.data.type.Chest;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

public class MCUtils_1_14_R1 extends MCUtilsBridge {

    private Particle particleType = Particle.NOTE;

    @Override
    public Map<String,Material> getGuiMenuItems()
    {
        Map<String,Material> guiItems = new LinkedHashMap<>();
        guiItems.put("player_payment_menuitem",Material.GOLD_INGOT);
        guiItems.put("pay_others_menuitem",Material.IRON_BARS);
        guiItems.put("list_wanted_menuiten",Material.REDSTONE);
        guiItems.put("list_escaped_menuitem",Material.PAPER);
        guiItems.put("close_menu_menuitem",Material.REDSTONE_BLOCK);
        return guiItems;
    };

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
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta im = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        im.setOwningPlayer(player);
        im.setDisplayName(player.getName());
        playerHead.setItemMeta(im);
        return playerHead;
    }

    @Override
    public Double getSolidLevel(Material material) {

        if (!material.isSolid())
            return 0.0D;

        switch (material) {

            case SANDSTONE_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case ACACIA_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case SPRUCE_STAIRS:
            case STONE_BRICK_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case GRANITE_STAIRS:
            case JUNGLE_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case STONE_STAIRS:
                return 100.0;

            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return 33.3;


            case BLACK_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
                return 10.00;

            case BLACK_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS_PANE:
            case GRAY_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS_PANE:
            case WHITE_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS_PANE:
                return 5.00;

            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_SIGN:
            case OAK_WALL_SIGN:
                return 0.0;

            case ICE:
            case FROSTED_ICE:
            case BLUE_ICE:
            case PACKED_ICE:
                return 33.0;

            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case OAK_FENCE:
            case SPRUCE_FENCE:
                return 15.0;

            case IRON_BARS:
                return 20.0;

            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return 10.0;

            case BREWING_STAND:
                return 20.0;

            case COBBLESTONE_WALL:
            case MOSSY_COBBLESTONE_WALL:
                return 75.0;

            case ANVIL:
                return 95.0;

            case BARRIER:
                return 0.0;

            case BLACK_WALL_BANNER:
            case BLUE_WALL_BANNER:
            case BROWN_WALL_BANNER:
            case CYAN_WALL_BANNER:
            case GRAY_WALL_BANNER:
            case GREEN_WALL_BANNER:
            case LIGHT_BLUE_WALL_BANNER:
            case LIGHT_GRAY_WALL_BANNER:
            case LIME_WALL_BANNER:
            case MAGENTA_WALL_BANNER:
            case ORANGE_WALL_BANNER:
            case PINK_WALL_BANNER:
            case PURPLE_WALL_BANNER:
            case RED_WALL_BANNER:
            case WHITE_WALL_BANNER:
            case YELLOW_WALL_BANNER:
                return 75.0;

            case BLACK_BANNER:
            case BLUE_BANNER:
            case BROWN_BANNER:
            case CYAN_BANNER:
            case GRAY_BANNER:
            case GREEN_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIME_BANNER:
            case MAGENTA_BANNER:
            case ORANGE_BANNER:
            case PINK_BANNER:
            case PURPLE_BANNER:
            case RED_BANNER:
            case WHITE_BANNER:
            case YELLOW_BANNER:
                return 75.0;

            case LADDER:
                return 65.00;

            default:
                if (material.isOccluding())
                    return 100.00;

                return 0.00;
        }
    }

    @Override
    public boolean isSameChest(Location chestLocation, Location clickedLocation)
    {

        if (chestLocation.getBlock().getLocation().equals(clickedLocation.getBlock().getLocation()) && (chestLocation.getBlock().getBlockData() instanceof Chest))
            return true;

        if (!(chestLocation.getBlock().getBlockData() instanceof Chest) ||  !(clickedLocation.getBlock().getBlockData() instanceof Chest))
        {
         return false;
        }

        Chest locChest = (Chest)chestLocation.getBlock().getBlockData();

        if (locChest.getType() == Chest.Type.SINGLE)
            return false;

        switch (locChest.getFacing())
        {
            case NORTH:
                if (locChest.getType() == Chest.Type.LEFT)
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(1,0,0)))
                        return true;
                if (locChest.getType() == Chest.Type.RIGHT )
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(-1,0,0)))
                        return true;
                break;
            case EAST:
                if (locChest.getType() == Chest.Type.LEFT)
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(0,0,1)))
                        return true;
                if (locChest.getType() == Chest.Type.RIGHT )
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(0,0,-1)))
                        return true;
                break;
            case SOUTH:
                if (locChest.getType() == Chest.Type.LEFT)
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(-1,0,0)))
                        return true;
                if (locChest.getType() == Chest.Type.RIGHT )
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(1,0,0)))
                        return true;
                break;
            case WEST:
                if (locChest.getType() == Chest.Type.LEFT)
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(0,0,-1)))
                        return true;
                if (locChest.getType() == Chest.Type.RIGHT )
                    if (clickedLocation.getBlock().getLocation().equals(chestLocation.getBlock().getLocation().clone().add(0,0,1)))
                        return true;
                break;
        }
        return false;
    }

    private Double getEntityMaxY(Entity entity)
    {
        return ((CraftEntity) entity).getHandle().getBoundingBox().maxY;
    }

    private Double getEntityMinY(Entity entity)
    {
        return ((CraftEntity) entity).getHandle().getBoundingBox().minY;
    }
    @Override
    public LineOfSight hasLineOfSight(LivingEntity entityA, Player player, int maxDistance, Player debug) {

        LineOfSight losResults = new LineOfSight();

        losResults.distance = entityA.getLocation().distanceSquared(player.getLocation());

        if (losResults.distance > 2304)
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
            losResults.visability = 100.0;
            losResults = testLineOfSight(losResults, entityA, new Location(entBLoc.getWorld(), entBLoc.getX(), getEntityMinY(player)+(entityInterval*cnt), entBLoc.getZ()),maxDistance, debug);
            if (losResults.visability > 0.00)
                return losResults;
        }

        return losResults;
    }

    private LineOfSight testLineOfSight(LineOfSight losResults, LivingEntity entityA, Location entityBLoc,  int maxDistance, Player debug) {

        Vector viewDirection = entityBLoc.clone().subtract(entityA.getEyeLocation()).toVector().normalize();
        Location curLocation = entityA.getEyeLocation();

        int cnt = 0;

        Double distance = entityA.getLocation().distanceSquared(entityBLoc);

        while (cnt<25)
        {

            RayTraceResult result = entityA.getWorld().rayTraceBlocks(curLocation, viewDirection, distance+90, FluidCollisionMode.ALWAYS, false);

            if (result == null) {
                losResults.visability = 100.0;
                return losResults;
            }

            if(result.getHitBlock() != null) {
                if (entityA.getLocation().distanceSquared(result.getHitBlock().getLocation()) > distance) {
                    if (debug != null) {
                        Location hitLoc = new Location(result.getHitBlock().getWorld(), result.getHitPosition().getX(), result.getHitPosition().getY(), result.getHitPosition().getZ());
                        showLineOfSight(entityA.getEyeLocation(), entityBLoc,debug);
                    }
                    return losResults;
                }

                if (debug != null) {
                    Location hitLoc = new Location(result.getHitBlock().getWorld(), result.getHitPosition().getX(), result.getHitPosition().getY(), result.getHitPosition().getZ());
                    showLineOfSight(entityA.getEyeLocation(), hitLoc,debug);
                }

                losResults.visability -= materialBlocking(result.getHitBlock().getType());
                if (losResults.visability < 0.0)
                {
                    return losResults;
                } else {
                    curLocation = new Location(result.getHitBlock().getWorld(), result.getHitPosition().getX(),result.getHitPosition().getY(),result.getHitPosition().getZ());
                    curLocation.setDirection(entityA.getEyeLocation().getDirection());
                }
            }

            cnt++;
        }

        return losResults;
    }

    private void showLineOfSight(Location entityALoc, Location entityBLoc, Player debug) {

        Vector viewDirection = entityBLoc.clone().subtract(entityALoc).toVector().normalize();
        double distance = entityALoc.distanceSquared(entityBLoc);

        Location lo = entityALoc.clone();
        Location prior;

        int maxIterations = 0;

        while (true) {

            if (lo.distanceSquared(entityALoc) > distance + 4) {
                return;
            }

            if (lo.distanceSquared(entityBLoc) < 0.5)
                return;

            prior = lo.clone();
            lo.add(viewDirection);


            maxIterations++;
            if (maxIterations > 500) {

                return;
            }

            if (debug != null)
                debug.spawnParticle(Particle.VILLAGER_HAPPY, lo, 1);
        }
    }

    private double materialBlocking(Material material)
    {
        switch (material) {
            case BLACK_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
                return 8.0;

            case BLACK_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS_PANE:
            case GRAY_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS_PANE:
            case WHITE_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS_PANE:
                return 8.0;

            case GLASS_PANE:
            case GLASS:
                return 5;

            case ICE:
                return 20.0;

            case FROSTED_ICE:
            case BLUE_ICE:
            case PACKED_ICE:
                return 100.0;

            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case OAK_FENCE:
            case SPRUCE_FENCE:
                return 25.0;

            case IRON_BARS:
                return 20.0;

            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return 10.0;

            case BARRIER:
                return 0.0;

            case LADDER:
                return 65.00;

            default:
                return 100.00;
        }
    }
}
