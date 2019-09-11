package net.livecar.nuttyworks.npc_police.utilities;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;

public class Utilities {
    private NPC_Police getStorageReference = null;

    public Utilities(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    public YamlConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        InputStream inputStream = null;
        Reader inputStreamReader = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e2) {
            return null;
        }

        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e2) {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            return null;
        }

        try {
            config.load(inputStreamReader);
            inputStreamReader.close();
            inputStream.close();
            return config;
        } catch (IOException | InvalidConfigurationException e1) {
            getStorageReference.getMessageManager.debugMessage(Level.SEVERE, "Utilities.loadConfiguration()|InvalidConfigurationException(" + file.getName() + ")|" + e1.getMessage());
        }

        if (inputStreamReader != null)
            try {
                inputStreamReader.close();
            } catch (IOException e) {
            }

        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        return null;
    }

    public boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Location Parse(String sLocation) {
        return Parse(sLocation, ",");
    }

    public Location Parse(String sLocation, String splitString) {
        String[] sSplitLocs = sLocation.split(splitString);
        if (sSplitLocs.length == 4) {
            try {
                return new Location(getStorageReference.pluginInstance.getServer().getWorld(sSplitLocs[3]), Double.valueOf(sSplitLocs[0]), Double.valueOf(sSplitLocs[1]), Double.valueOf(sSplitLocs[2]));
            } catch (Exception err) {
                getStorageReference.pluginInstance.getServer().broadcastMessage(err.getMessage());
                // Failed parsing the location, return null
                return null;
            }
        }
        return null;
    }

    public String locationToString(Location oLoc) {
        return locationToString(oLoc, ",");
    }

    public String locationToString(Location oLoc, String splitString) {
        return "" + oLoc.getBlockX() + splitString + oLoc.getBlockY() + splitString + oLoc.getBlockZ() + splitString + oLoc.getWorld().getName();
    }

    public String secondsToTime(long totalSeconds) {
        int timeSeconds = (int) totalSeconds % 60;
        int timeMinutes = (int) (totalSeconds % 3600) / 60;
        int timeHours = (int) totalSeconds / 3600;

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", timeHours, timeMinutes, timeSeconds);

    }

    public String serialzeItemStack(ItemStack[] itemStack) {
        if (itemStack == null)
            return "";

        YamlConfiguration config = new YamlConfiguration();
        for (int slot = 0; slot < itemStack.length; slot++)
            if (itemStack[slot] != null)
                config.set(String.valueOf(slot), itemStack[slot]);
        return config.saveToString();
    }

    public ItemStack[] deserialzeItemStack(String itemString) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(itemString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        List<ItemStack> inventory = new ArrayList<ItemStack>();

        for (String key : config.getKeys(false)) {
            inventory.add(config.getItemStack(key));
        }
        return inventory.toArray(new ItemStack[inventory.size()]);
    }

    public ItemStack[] addToInventory(ItemStack[] sourceInventory, ItemStack[] destinationInventory) {
        for (int inventorySlot = 0; inventorySlot < sourceInventory.length; inventorySlot++) {
            if (sourceInventory[inventorySlot] == null || sourceInventory[inventorySlot].getType() == Material.AIR)
                continue;

            ItemStack item = sourceInventory[inventorySlot];
            int emptySlot = -1;

            for (int lockedSlot = 0; lockedSlot < destinationInventory.length; lockedSlot++) {
                if (sourceInventory[inventorySlot] == null || sourceInventory[inventorySlot].getType() == Material.AIR)
                    continue;

                if (emptySlot == -1 && destinationInventory[lockedSlot] == null) {
                    emptySlot = lockedSlot;
                    continue;
                } else if (emptySlot == -1 && destinationInventory[lockedSlot].getType() == Material.AIR) {
                    emptySlot = lockedSlot;
                    continue;
                }

                if (destinationInventory[lockedSlot] != null) {
                    if (destinationInventory[lockedSlot].isSimilar(item)) // .getType()
                    // ==
                    // item.getType()
                    // &&
                    // destinationInventory[lockedSlot].getData().equals(item.getData())
                    // &&
                    // destinationInventory[lockedSlot].getDurability()
                    // ==
                    // item.getDurability()
                    // &&
                    // destinationInventory[lockedSlot].getAmount()
                    // <
                    // destinationInventory[lockedSlot].getType().getMaxStackSize())
                    {
                        if ((destinationInventory[lockedSlot].getAmount() + item.getAmount()) > (destinationInventory[lockedSlot].getType().getMaxStackSize())) {
                            int leftOver = Math.abs(sourceInventory[inventorySlot].getType().getMaxStackSize() - (destinationInventory[lockedSlot].getAmount() + item.getAmount()));
                            destinationInventory[lockedSlot].setAmount(item.getAmount() - leftOver);
                            item.setAmount(item.getAmount() - (item.getAmount() - leftOver));
                        } else {
                            destinationInventory[lockedSlot].setAmount(destinationInventory[lockedSlot].getAmount() + item.getAmount());
                            sourceInventory[inventorySlot] = null;
                            break;
                        }
                    }
                }

                if (emptySlot != -1 && (destinationInventory[emptySlot] == null || destinationInventory[emptySlot].getType() == Material.AIR) && item != null) {
                    destinationInventory[emptySlot] = item.clone();
                    emptySlot = -1;
                    sourceInventory[inventorySlot] = null;
                    continue;
                }
            }
        }
        return sourceInventory;
    }

    public boolean isItemSimular(ItemStack sourceItem, ItemStack compareItem) {

        if (sourceItem.getType() != compareItem.getType())
            return false;

        // 1 Count = Only compare the type
        if (compareItem.getAmount() == 1)
            return true;

        // 2 = Compare lore
        if (sourceItem.getItemMeta().hasLore() && compareItem.getItemMeta().hasLore()) {
            if (sourceItem.getItemMeta().getLore().size() != compareItem.getItemMeta().getLore().size())
                return false;

            for (int n = 0; n < sourceItem.getItemMeta().getLore().size(); n++) {
                if (!sourceItem.getItemMeta().getLore().get(n).equalsIgnoreCase(compareItem.getItemMeta().getLore().get(n)))
                    return false;
            }
        }

        if (compareItem.getAmount() == 2)
            return true;

        //3 = Compare enchants
        if (sourceItem.getItemMeta().hasEnchants() != compareItem.getItemMeta().hasEnchants())
            return false;

        if (sourceItem.getItemMeta().hasEnchants() && compareItem.getItemMeta().hasEnchants()) {
            if (sourceItem.getItemMeta().getEnchants().size() != compareItem.getItemMeta().getEnchants().size())
                return false;

            for (Entry<Enchantment, Integer> chant : sourceItem.getItemMeta().getEnchants().entrySet()) {
                if (!compareItem.getItemMeta().getEnchants().containsKey(chant.getKey())) {
                    return false;
                }
            }
        }

        if (compareItem.getAmount() == 3)
            return true;

        //4 = Compare Names
        if (compareItem.getItemMeta().hasDisplayName() != sourceItem.getItemMeta().hasDisplayName())
            return false;

        if (compareItem.getItemMeta().hasDisplayName() && sourceItem.getItemMeta().hasDisplayName()) {
            if (!compareItem.getItemMeta().getDisplayName().equalsIgnoreCase(sourceItem.getItemMeta().getDisplayName()))
                return false;
        }

        return true;
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

}
