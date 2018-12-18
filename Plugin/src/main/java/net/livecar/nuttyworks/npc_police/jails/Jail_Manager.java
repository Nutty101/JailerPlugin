package net.livecar.nuttyworks.npc_police.jails;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.*;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Jail_Manager {
    private HashMap<String, World_Setting> world_Configurations = null;
    private NPC_Police getStorageReference = null;

    public Jail_Manager(NPC_Police policeRef) {
        getStorageReference = policeRef;
        world_Configurations = new HashMap<String, World_Setting>();
    }

    public boolean containsWorld(String world) {
        if (world_Configurations.containsKey(world))
            return true;
        return false;
    }

    public Jail_Setting[] getWorldJails(String world) {
        if (!containsWorld(world))
            return new Jail_Setting[0];
        return world_Configurations.get(world).jail_Configs.values().toArray(new Jail_Setting[world_Configurations.get(world).jail_Configs.values().size()]);
    }

    public List<Map.Entry<Double, Jail_Setting>> getWorldJails(Location location) {
        if (!containsWorld(location.getWorld().getName()))
            return new ArrayList<>();

        List<Map.Entry<Double, Jail_Setting>> list = new ArrayList<>();

        for (Jail_Setting jailSetting : world_Configurations.get(location.getWorld().getName()).jail_Configs.values().toArray(new Jail_Setting[world_Configurations.get(location.getWorld().getName()).jail_Configs.values().size()]))
        {
            Location[] regionBounds = getStorageReference.getWorldGuardPlugin.getRegionBounds(location.getWorld(),jailSetting.regionName);
            if (regionBounds.length == 0)
                continue;

            Double distToRegion = location.distanceSquared(new Location(location.getWorld(),regionBounds[0].getX()+((regionBounds[1].getX()-regionBounds[0].getX())/2),regionBounds[0].getY()+((regionBounds[1].getY()-regionBounds[0].getY())/2),regionBounds[0].getZ()+((regionBounds[1].getZ()-regionBounds[0].getZ())/2)));
            Map.Entry<Double,Jail_Setting> entry = new AbstractMap.SimpleEntry<Double, Jail_Setting>(distToRegion, jailSetting);
            list.add(entry);
        }
        list.sort(Map.Entry.comparingByKey());

        return list;
    }

    public World_Setting getWorldSettings() {
        return getWorldSettings("_GlobalSettings");
    }

    public World_Setting getWorldSettings(String world) {
        if (containsWorld(world))
            return world_Configurations.get(world);
        return null;
    }

    public World_Setting getGlobalSettings() {
        return world_Configurations.get("_GlobalSettings");
    }

    public void addWorldSetting(String world, World_Setting worldSetting) {
        if (!containsWorld(world)) {
            world_Configurations.put(world, worldSetting);
            return;
        }
    }

    public Jail_Setting getJailAtLocation(Location location) {
        RegionSettings regionFlags = getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(location);
        if (regionFlags != null && !regionFlags.extendsJail.trim().equals("")) {
            Jail_Setting tmpJail = getJailByName(regionFlags.extendsJail);
            if (tmpJail != null)
                return tmpJail;
        }

        List<String> locationRegions = getStorageReference.getWorldGuardPlugin.getCurrentRegions(location);
        for (World_Setting worldConfig : world_Configurations.values()) {
            for (Jail_Setting jailSetting : worldConfig.jail_Configs.values()) {

                if (locationRegions.contains(jailSetting.regionName))
                    return jailSetting;
            }
        }
        return null;
    }

    public Jail_Setting getJailByRegion(String region) {
        for (World_Setting worldConfig : world_Configurations.values()) {
            for (Jail_Setting jailSetting : worldConfig.jail_Configs.values()) {
                if (jailSetting.regionName.toLowerCase().equalsIgnoreCase(region.toLowerCase()))
                    return jailSetting;
            }
        }
        return null;
    }

    public Jail_Setting getJailByName(String name) {
        for (World_Setting worldConfig : world_Configurations.values()) {
            for (Jail_Setting jailSetting : worldConfig.jail_Configs.values()) {
                if (jailSetting.jailName.toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                    return jailSetting;
            }
        }
        return null;
    }

    public Jail_Setting getJailByID(UUID id) {
        for (World_Setting worldConfig : world_Configurations.values()) {
            for (Jail_Setting jailSetting : worldConfig.jail_Configs.values()) {
                if (jailSetting.jail_ID.equals(id))
                    return jailSetting;
            }
        }
        return null;
    }

    public boolean putJail(String world, Jail_Setting jailSetting) {
        if (!containsWorld(world))
            world_Configurations.put(world, new World_Setting(world));

        if (!world_Configurations.get(world).jail_Configs.containsKey(jailSetting.jailName.toLowerCase())) {
            world_Configurations.get(world).jail_Configs.put(jailSetting.jailName.toLowerCase(), jailSetting);
            return true;
        } else {
            // Remove the jail and re-add it
            world_Configurations.get(world).jail_Configs.remove(jailSetting.jailName.toLowerCase());
            world_Configurations.get(world).jail_Configs.put(jailSetting.jailName.toLowerCase(), jailSetting);
            return true;
        }
    }

    public void removeJail(World world, Jail_Setting jailSetting) {
        if (!containsWorld(world.getName()))
            return;

        if (world_Configurations.get(world.getName()).jail_Configs.containsKey(jailSetting.jailName.toLowerCase())) {
            world_Configurations.get(world.getName()).jail_Configs.remove(jailSetting.jailName.toLowerCase());
        }
    }

    public boolean getProtectOnlyTraits(World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getProtect_OnlyAssigned() == STATE_SETTING.TRUE ? true : false;
        if (world_Configurations.get(world.getName()).getProtect_OnlyAssigned() != STATE_SETTING.NOTSET)
            return world_Configurations.get(world.getName()).getProtect_OnlyAssigned() == STATE_SETTING.FALSE ? false : true;
        return getStorageReference.getJailManager.getGlobalSettings().getProtect_OnlyAssigned() == STATE_SETTING.TRUE ? true : false;
    }

    public int getMaxWarningDamage(World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getWarning_MaximumDamage();
        if (world_Configurations.get(world.getName()).getWarning_MaximumDamage() > -1)
            return world_Configurations.get(world.getName()).getWarning_MaximumDamage();
        return getStorageReference.getJailManager.getGlobalSettings().getWarning_MaximumDamage();
    }

    public int getMaxDistance(World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getMaximum_GardDistance();

        if (world_Configurations.get(world.getName()).getMaximum_GardDistance() > -1)
            return world_Configurations.get(world.getName()).getMaximum_GardDistance();

        return getStorageReference.getJailManager.getGlobalSettings().getMaximum_GardDistance();
    }

    public int getMaxDistance(World world, NPCPolice_Trait trait) {
        if (trait == null)
            return getMaxDistance(world);

        if (trait.maxDistance_Guard > -1)
            return trait.maxDistance_Guard;

        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getMaximum_GardDistance();

        if (world_Configurations.get(world.getName()).getMaximum_GardDistance() > -1)
            return world_Configurations.get(world.getName()).getMaximum_GardDistance();

        return getStorageReference.getJailManager.getGlobalSettings().getMaximum_GardDistance();
    }

    public int getMinBountyWanted(World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getMinumum_WantedBounty();
        if (world_Configurations.get(world.getName()).getMinumum_WantedBounty() > -1)
            return world_Configurations.get(world.getName()).getMinumum_WantedBounty();
        return getStorageReference.getJailManager.getGlobalSettings().getMinumum_WantedBounty();
    }

    public int getMinBountyWanted(World world, NPCPolice_Trait trait) {
        if (trait != null && trait.minBountyAttack > -1)
            return trait.minBountyAttack;

        return getMinBountyWanted(world);
    }

    public STATE_SETTING getLOSSetting(World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getStorageReference.getJailManager.getGlobalSettings().getLOSAttackSetting();
        if (world_Configurations.get(world.getName()).getLOSAttackSetting() != STATE_SETTING.NOTSET)
            return world_Configurations.get(world.getName()).getLOSAttackSetting() == STATE_SETTING.NOTSET ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
        return getStorageReference.getJailManager.getGlobalSettings().getLOSAttackSetting();
    }

    public STATE_SETTING getLOSSetting(World world, NPCPolice_Trait trait) {
        if (trait != null && trait.lineOfSightAttack > -1)
            return trait.lineOfSightAttack == 0 ? STATE_SETTING.FALSE : STATE_SETTING.TRUE;
        return getLOSSetting(world);
    }

    public String getJailGroup(JAILED_GROUPS type, World world) {
        if (!world_Configurations.containsKey(world.getName()))
            return getJailGroup(type);

        switch (type) {
            case ESCAPED:
                if (getStorageReference.getJailManager.getWorldSettings(world.getName()).getEscapedGroup().isEmpty())
                    return getJailGroup(type);
                return getStorageReference.getJailManager.getWorldSettings(world.getName()).getEscapedGroup();
            case JAILED:
                if (getStorageReference.getJailManager.getWorldSettings(world.getName()).getJailedGroup().isEmpty())
                    return getJailGroup(type);
                return getStorageReference.getJailManager.getWorldSettings(world.getName()).getJailedGroup();
            case WANTED:
                if (getStorageReference.getJailManager.getWorldSettings(world.getName()).getWantedGroup().isEmpty())
                    return getJailGroup(type);
                return getStorageReference.getJailManager.getWorldSettings(world.getName()).getWantedGroup();
            default:
                return "";
        }
    }

    public String getJailGroup(JAILED_GROUPS type) {
        switch (type) {
            case ESCAPED:
                return getStorageReference.getJailManager.getGlobalSettings().getEscapedGroup();
            case JAILED:
                return getStorageReference.getJailManager.getGlobalSettings().getJailedGroup();
            case WANTED:
                return getStorageReference.getJailManager.getGlobalSettings().getWantedGroup();
            default:
                return "";
        }
    }

    public WANTED_LEVEL getMinWantedLevel(World world) {
        return world_Configurations.get(world.getName()).getMinimum_WantedLevel();
    }

    public WANTED_LEVEL getMinWantedLevel(World world, Jail_Setting currentJail) {
        if (world_Configurations.containsKey(world.getName())) {
            return getMinWantedLevel(world);
        }

        if (currentJail == null)
            return getMinWantedLevel(world);

        return currentJail.minWanted;
    }

    public WANTED_LEVEL getMaxWantedLevel(World world) {
        return world_Configurations.get(world.getName()).getMaximum_WantedLevel();
    }

    public WANTED_LEVEL getMaxWantedLevel(World world, Jail_Setting currentJail) {
        if (world_Configurations.containsKey(world.getName())) {
            return getMaxWantedLevel(world);
        }

        if (currentJail == null)
            return getMaxWantedLevel(world);

        return currentJail.maxWanted;

    }

    public Double getBountySetting(JAILED_BOUNTY type) {
        switch (type) {
            case BOUNTY_DAMAGE:
                return getStorageReference.getJailManager.getGlobalSettings().getBounty_Damage();
            case BOUNTY_ESCAPED:
                return getStorageReference.getJailManager.getGlobalSettings().getBounty_Escaped();
            case BOUNTY_PVP:
                return getStorageReference.getJailManager.getGlobalSettings().getBounty_PVP();
            case BOUNTY_MURDER:
                return getStorageReference.getJailManager.getGlobalSettings().getBounty_Murder();
            case TIMES_CELLOUT_NIGHT:
                return getStorageReference.getJailManager.getGlobalSettings().getTimeInterval_CellNight();
            case TIMES_CELLOUT_DAY:
                return getStorageReference.getJailManager.getGlobalSettings().getTimeInterval_CellDay();
            case TIMES_ESCAPED:
                return getStorageReference.getJailManager.getGlobalSettings().getTimeInterval_Escaped();
            case TIMES_JAILED:
                return getStorageReference.getJailManager.getGlobalSettings().getTimeInterval_Jailed();
            case TIMES_WANTED:
                return getStorageReference.getJailManager.getGlobalSettings().getTimeInterval_Wanted();
            default:
                return 0.0D;
        }
    }

    public Double getBountySetting(JAILED_BOUNTY type, World world, NPCPolice_Trait npcTrait) {
        if (npcTrait != null) {
            switch (type) {
                case BOUNTY_DAMAGE:
                    if (npcTrait.bounty_assault > -1)
                        return npcTrait.bounty_assault;
                    break;
                case BOUNTY_MURDER:
                    if (npcTrait.bounty_murder > -1)
                        return npcTrait.bounty_murder;
                    break;
                default:
                    break;
            }
        }

        return getBountySetting(type, world);
    }

    public Double getBountySetting(JAILED_BOUNTY type, World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getBountySetting(type);
        }
        switch (type) {
            case BOUNTY_DAMAGE:
                if (world_Configurations.get(world.getName()).getBounty_Damage() > -1)
                    return world_Configurations.get(world.getName()).getBounty_Damage();
                break;
            case BOUNTY_ESCAPED:
                if (world_Configurations.get(world.getName()).getBounty_Escaped() > -1)
                    return world_Configurations.get(world.getName()).getBounty_Escaped();
                break;
            case BOUNTY_PVP:
                if (world_Configurations.get(world.getName()).getBounty_PVP() > -1)
                    return world_Configurations.get(world.getName()).getBounty_PVP();
            case BOUNTY_MURDER:
                if (world_Configurations.get(world.getName()).getBounty_Murder() > -1)
                    return world_Configurations.get(world.getName()).getBounty_Murder();
                break;
            case TIMES_CELLOUT_NIGHT:
                if (world_Configurations.get(world.getName()).getTimeInterval_CellNight() != Double.MIN_VALUE)
                    return world_Configurations.get(world.getName()).getTimeInterval_CellNight();
                break;
            case TIMES_CELLOUT_DAY:
                if (world_Configurations.get(world.getName()).getTimeInterval_CellDay() != Double.MIN_VALUE)
                    return world_Configurations.get(world.getName()).getTimeInterval_CellDay();
                break;
            case TIMES_ESCAPED:
                if (world_Configurations.get(world.getName()).getTimeInterval_Escaped() != Double.MIN_VALUE)
                    return world_Configurations.get(world.getName()).getTimeInterval_Escaped();
                break;
            case TIMES_JAILED:
                if (world_Configurations.get(world.getName()).getTimeInterval_Jailed() != Double.MIN_VALUE)
                    return world_Configurations.get(world.getName()).getTimeInterval_Jailed();
                break;
            case TIMES_WANTED:
                if (world_Configurations.get(world.getName()).getTimeInterval_Wanted() != Double.MIN_VALUE)
                    return world_Configurations.get(world.getName()).getTimeInterval_Wanted();
                break;
            default:
                break;
        }
        return getBountySetting(type);
    }

    public Double getBountySetting(JAILED_BOUNTY type, World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getBountySetting(type);
        }
        if (currentJail == null)
            return getBountySetting(type, world);

        switch (type) {
            case BOUNTY_ESCAPED:
                if (currentJail.bounty_Escaped > -1.0D)
                    return currentJail.bounty_Escaped;
                break;
            case BOUNTY_PVP:
                if (currentJail.bounty_PVP != Double.MIN_VALUE)
                    return currentJail.bounty_PVP;
            case TIMES_CELLOUT_NIGHT:
                if (currentJail.times_CellNight != Double.MIN_VALUE)
                    return currentJail.times_CellNight;
                break;
            case TIMES_CELLOUT_DAY:
                if (currentJail.times_CellDay != Double.MIN_VALUE)
                    return currentJail.times_CellDay;
                break;
            case TIMES_JAILED:
                if (currentJail.times_Jailed != Double.MIN_VALUE)
                    return currentJail.times_Jailed;
                break;
            default:
                break;
        }

        return getBountySetting(type, world);
    }

    public List<String> getProcessedCommands(COMMAND_LISTS type) {
        switch (type) {
            case NPC_ALERTGUARDS:
                if (!getStorageReference.getJailManager.getGlobalSettings().onNPC_AlertGuards.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onNPC_AlertGuards;
                break;
            case NPC_MURDERED:
                if (!getStorageReference.getJailManager.getGlobalSettings().onNPC_Murder.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onNPC_Murder;
                break;
            case NPC_NOGUARDS:
                if (!getStorageReference.getJailManager.getGlobalSettings().onNPC_NoGuards.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onNPC_NoGuards;
                break;
            case NPC_WARNING:
                if (!getStorageReference.getJailManager.getGlobalSettings().onNPC_Warning.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onNPC_Warning;
                break;
            case PLAYER_JAILED:
                if (!getStorageReference.getJailManager.getGlobalSettings().onPlayer_Arrest.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onPlayer_Arrest;
                break;
            case PLAYER_ESCAPED:
                if (!getStorageReference.getJailManager.getGlobalSettings().onPlayer_Escaped.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onPlayer_Escaped;
                break;
            case PLAYER_RELEASED:
                if (!getStorageReference.getJailManager.getGlobalSettings().onPlayer_Released.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onPlayer_Released;
                break;
            case PLAYER_WANTED:
                if (!getStorageReference.getJailManager.getGlobalSettings().onPlayer_Wanted.isEmpty())
                    return getStorageReference.getJailManager.getGlobalSettings().onPlayer_Wanted;
                break;
            default:
                break;
        }
        return new ArrayList<String>();
    }

    public List<String> getProcessedCommands(COMMAND_LISTS type, World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getProcessedCommands(type);
        }

        switch (type) {
            case NPC_ALERTGUARDS:
                if (world_Configurations.get(world.getName()).onNPC_AlertGuards.size() > 0) {
                    return world_Configurations.get(world.getName()).onNPC_AlertGuards;
                }
                break;
            case NPC_MURDERED:
                if (world_Configurations.get(world.getName()).onNPC_Murder.size() > 0) {
                    return world_Configurations.get(world.getName()).onNPC_Murder;
                }
                break;
            case NPC_NOGUARDS:
                if (world_Configurations.get(world.getName()).onNPC_NoGuards.size() > 0) {
                    return world_Configurations.get(world.getName()).onNPC_NoGuards;
                }
                break;
            case NPC_WARNING:
                if (world_Configurations.get(world.getName()).onNPC_Warning.size() > 0) {
                    return world_Configurations.get(world.getName()).onNPC_Warning;
                }
                break;
            case PLAYER_JAILED:
                if (world_Configurations.get(world.getName()).onPlayer_Arrest.size() > 0) {
                    return world_Configurations.get(world.getName()).onPlayer_Arrest;
                }
                break;
            case PLAYER_ESCAPED:
                if (world_Configurations.get(world.getName()).onPlayer_Escaped.size() > 0) {
                    return world_Configurations.get(world.getName()).onPlayer_Escaped;
                }
                break;
            case PLAYER_RELEASED:
                if (world_Configurations.get(world.getName()).onPlayer_Released.size() > 0) {
                    return world_Configurations.get(world.getName()).onPlayer_Released;
                }
                break;
            case PLAYER_WANTED:
                if (world_Configurations.get(world.getName()).onPlayer_Wanted.size() > 0) {
                    return world_Configurations.get(world.getName()).onPlayer_Wanted;
                }
            case BOUNTY_MAXIMUM:
                if (world_Configurations.get(world.getName()).onBounty_Maximum.size() > 0) {
                    return world_Configurations.get(world.getName()).onBounty_Maximum;
                }
                break;
            default:
                break;

        }

        return getProcessedCommands(type);
    }

    public List<String> getProcessedCommands(COMMAND_LISTS type, World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getProcessedCommands(type);
        }

        switch (type) {
            case PLAYER_JAILED:
                if (currentJail.onPlayer_Arrest.size() > 0) {
                    return currentJail.onPlayer_Arrest;
                }
                break;
            case PLAYER_ESCAPED:
                if (currentJail.onPlayer_Escaped.size() > 0) {
                    return currentJail.onPlayer_Escaped;
                }
                break;
            case PLAYER_RELEASED:
                if (currentJail.onPlayer_Released.size() > 0) {
                    return currentJail.onPlayer_Released;
                }
                break;
            default:
                break;
        }

        return getProcessedCommands(type, world);
    }

    public DistanceDelaySetting getNoticeSetting(NOTICE_SETTING type) {
        switch (type) {
            case JAILED:
                return new DistanceDelaySetting(getStorageReference.getJailManager.getGlobalSettings().getJailed_Distance(), getStorageReference.getJailManager.getGlobalSettings().getJailed_Delay());
            case ESCAPED:
                return new DistanceDelaySetting(getStorageReference.getJailManager.getGlobalSettings().getEscaped_Distance(), getStorageReference.getJailManager.getGlobalSettings().getEscaped_Delay());
            case MURDER:
                return new DistanceDelaySetting(getStorageReference.getJailManager.getGlobalSettings().getMurder_Distance(), getStorageReference.getJailManager.getGlobalSettings().getMurder_Delay());
            case THEFT:
                return new DistanceDelaySetting(getStorageReference.getJailManager.getGlobalSettings().getTheft_Distance(), getStorageReference.getJailManager.getGlobalSettings().getTheft_Delay());
            default:
                break;

        }
        return new DistanceDelaySetting(0, 0.0D);
    }

    public DistanceDelaySetting getNoticeSetting(NOTICE_SETTING type, World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getNoticeSetting(type);
        }

        switch (type) {
            case JAILED:
                if (world_Configurations.get(world.getName()).getJailed_Distance() > -1)
                    return new DistanceDelaySetting(world_Configurations.get(world.getName()).getJailed_Distance(), world_Configurations.get(world.getName()).getJailed_Delay());
                break;
            case ESCAPED:
                if (world_Configurations.get(world.getName()).getEscaped_Distance() > -1)
                    return new DistanceDelaySetting(world_Configurations.get(world.getName()).getEscaped_Distance(), world_Configurations.get(world.getName()).getEscaped_Delay());
                break;
            case MURDER:
                if (world_Configurations.get(world.getName()).getMurder_Distance() > -1)
                    return new DistanceDelaySetting(world_Configurations.get(world.getName()).getMurder_Distance(), world_Configurations.get(world.getName()).getMurder_Delay());
                break;
            case THEFT:
                if (world_Configurations.get(world.getName()).getTheft_Distance() > -1)
                    return new DistanceDelaySetting(world_Configurations.get(world.getName()).getTheft_Distance(), world_Configurations.get(world.getName()).getTheft_Delay());
                break;
            default:
                break;

        }
        return getNoticeSetting(type);
    }

    public DistanceDelaySetting getNoticeSetting(NOTICE_SETTING type, World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return getNoticeSetting(type);
        }
        if (currentJail == null)
            return getNoticeSetting(type, world);

        switch (type) {
            case ESCAPED:
                if (currentJail.escaped_Distance > -1)
                    return new DistanceDelaySetting(currentJail.escaped_Distance, currentJail.escaped_Delay);
                break;
            default:
                break;
        }

        return getNoticeSetting(type, world);
    }

    public boolean onArrestTakeInventory() {
        return getStorageReference.getJailManager.getGlobalSettings().onArrest_InventoryAction() == STATE_SETTING.TRUE ? true : false;
    }

    public boolean onArrestTakeInventory(World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onArrestTakeInventory();
        }
        if (world_Configurations.get(world.getName()).onArrest_InventoryAction() == STATE_SETTING.NOTSET)
            return onArrestTakeInventory();

        return world_Configurations.get(world.getName()).onArrest_InventoryAction() == STATE_SETTING.TRUE ? true : false;
    }

    public boolean onArrestTakeInventory(World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onArrestTakeInventory();
        }
        if (currentJail == null)
            return onArrestTakeInventory(world);

        if (currentJail.onArrest_InventoryAction != STATE_SETTING.NOTSET)
            return currentJail.onArrest_InventoryAction == STATE_SETTING.TRUE ? true : false;

        return onArrestTakeInventory(world);
    }

    public Location onFreeStatus(Jail_Setting currentJail) {
        if (currentJail == null)
            return null;

        return currentJail.freeSpawnPoint;
    }

    public STATE_SETTING onEsapeReturnInventory() {
        return getStorageReference.getJailManager.getGlobalSettings().onEscape_InventoryAction();
    }

    public STATE_SETTING onEsapeReturnInventory(World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onEsapeReturnInventory();
        }
        if (world_Configurations.get(world.getName()).onEscape_InventoryAction() == STATE_SETTING.NOTSET)
            return onEsapeReturnInventory();

        return world_Configurations.get(world.getName()).onEscape_InventoryAction();
    }

    public STATE_SETTING onEsapeReturnInventory(World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onEsapeReturnInventory();
        }
        if (currentJail == null)
            return onEsapeReturnInventory(world);

        if (currentJail.onEscape_InventoryAction != STATE_SETTING.NOTSET)
            return currentJail.onEscape_InventoryAction;

        return onEsapeReturnInventory(world);
    }

    public boolean onFreeReturnInventory() {
        return getStorageReference.getJailManager.getWorldSettings("_GlobalSettings").onFree_InventoryAction() == STATE_SETTING.TRUE ? true : false;
    }

    public boolean onFreeReturnInventory(World world) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onFreeReturnInventory();
        }
        if (world_Configurations.get(world.getName()).onFree_InventoryAction() == STATE_SETTING.NOTSET)
            return onFreeReturnInventory();

        return world_Configurations.get(world.getName()).onFree_InventoryAction() == STATE_SETTING.TRUE ? true : false;
    }

    public boolean onFreeReturnInventory(World world, Jail_Setting currentJail) {
        if (!world_Configurations.containsKey(world.getName())) {
            return onFreeReturnInventory();
        }
        if (currentJail == null)
            return onFreeReturnInventory(world);

        if (currentJail.onFree_InventoryAction != STATE_SETTING.NOTSET)
            return currentJail.onFree_InventoryAction == STATE_SETTING.TRUE ? true : false;

        return onFreeReturnInventory(world);
    }

    public void loadJailSettings() {
        world_Configurations = new HashMap<String, World_Setting>();

        File jailConfigFile = new File(getStorageReference.pluginInstance.getDataFolder(), "jail_settings.yml");

        YamlConfiguration jail_Settings = getStorageReference.getUtilities.loadConfiguration(jailConfigFile);

        if (jail_Settings != null) {
            for (String worldName : jail_Settings.getKeys(false)) {
            /* if (!worldName.equalsIgnoreCase("_GlobalSettings") && getStorageReference.pluginInstance.getServer().getWorld(worldName) == null)
            {
               continue;
            } */

                loadWorldConfig(worldName, jail_Settings);

            }
        }

        //Log to the console that we loaded worlds.
        String worldList = "";
        for (World_Setting worldConfig : world_Configurations.values()) {
            worldList += "[" + worldConfig.getWorldName() + "] ";
        }

        getStorageReference.getMessageManager.consoleMessage("console_messages.plugin_loadedworld", worldList);

        if (!world_Configurations.containsKey("_GlobalSettings")) {
            World_Setting setupWorld = new World_Setting();
            if (getStorageReference.getDefaultConfig.getBoolean("global_settings.groups.enabled", false)) {
                setupWorld.setEscapedGroup(getStorageReference.getDefaultConfig.getString("global_settings.groups.wanted_criminals", "WANTEDCRIMINALS"));
                setupWorld.setJailedGroup(getStorageReference.getDefaultConfig.getString("global_settings.groups.jailed_criminals", "JAILED"));
                setupWorld.setWantedGroup(getStorageReference.getDefaultConfig.getString("global_settings.groups.escaped_criminals", "FUGITIVES"));
            }

            setupWorld.setBounty_Damage(getStorageReference.getDefaultConfig.getDouble("global_settings.bounty.damage", 76.0));
            setupWorld.setBounty_Escaped(getStorageReference.getDefaultConfig.getDouble("global_settings.bounty.escaped", 1500.0));
            setupWorld.setBounty_Murder(getStorageReference.getDefaultConfig.getDouble("global_settings.bounty.murder", 2400.0));
            setupWorld.setBounty_Maximum(getStorageReference.getDefaultConfig.getDouble("global_settings.bounty.maximum", -1.0));
            setupWorld.setBounty_PVP(getStorageReference.getDefaultConfig.getDouble("global_settings.bounty.jailed_pvp", 10.0));

            setupWorld.setTimeInterval_CellDay(getStorageReference.getDefaultConfig.getDouble("global_settings.times.outofcell_day", 0.0));
            setupWorld.setTimeInterval_CellNight(getStorageReference.getDefaultConfig.getDouble("global_settings.times.outofcell_night", 0.0));
            setupWorld.setTimeInterval_Escaped(getStorageReference.getDefaultConfig.getDouble("global_settings.times.escaped_bounty", 1.0));
            setupWorld.setTimeInterval_Jailed(getStorageReference.getDefaultConfig.getDouble("global_settings.times.jailed_bounty", -20.0));
            setupWorld.setTimeInterval_Wanted(getStorageReference.getDefaultConfig.getDouble("global_settings.times.wanted_bounty", 0.0));

            if (getStorageReference.getDefaultConfig.contains("global_settings.inventory.takeonarrest"))
                setupWorld.onArrest_InventoryAction(getStorageReference.getDefaultConfig.getBoolean("global_settings.inventory.takeonarrest", false) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
            if (getStorageReference.getDefaultConfig.contains("global_settings.inventory.returnonescape"))
                setupWorld.onEscape_InventoryAction(getStorageReference.getDefaultConfig.getBoolean("global_settings.inventory.returnonescape", false) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
            if (getStorageReference.getDefaultConfig.contains("global_settings.inventory.returnonfree"))
                setupWorld.onFree_InventoryAction(getStorageReference.getDefaultConfig.getBoolean("global_settings.inventory.returnonfree", false) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);

            setupWorld.setWarning_MaximumDamage(getStorageReference.getDefaultConfig.getInt("global_settings.npc.max_warning_damage", 3));
            setupWorld.setMaximum_GuardDistance(getStorageReference.getDefaultConfig.getInt("global_settings.npc.max_distance_guard", 25));
            setupWorld.setProtect_OnlyAssigned(getStorageReference.getDefaultConfig.getBoolean("global_settings.npc.takeonarrest", false) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
            setupWorld.setLOSAttackSetting(getStorageReference.getDefaultConfig.getBoolean("global_settings.npc.lineofsightattack", false) ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
            setupWorld.setMinimum_WantedBounty(getStorageReference.getDefaultConfig.getInt("global_settings.npc.min_bounty_wanted", 0));

            setupWorld.setBounty_Notice(getStorageReference.getDefaultConfig.getInt("global_settings.intervals.bounty_notice", 60));

            setupWorld.setEscaped_Distance(getStorageReference.getDefaultConfig.getInt("global_settings.player_notices.escaped.distance", 125));
            setupWorld.setEscaped_Delay(getStorageReference.getDefaultConfig.getDouble("global_settings.player_notices.escaped.delay", 0.2));
            setupWorld.setJailed_Distance(getStorageReference.getDefaultConfig.getInt("global_settings.player_notices.arrested.distance", 125));
            setupWorld.setJailed_Delay(getStorageReference.getDefaultConfig.getDouble("global_settings.player_notices.arrested.delay", 0.4));
            setupWorld.setMurder_Distance(getStorageReference.getDefaultConfig.getInt("global_settings.player_notices.murder.distance", 125));
            setupWorld.setMurder_Delay(getStorageReference.getDefaultConfig.getDouble("global_settings.player_notices.murder.delay", 0.3));
            setupWorld.setTheft_Distance(getStorageReference.getDefaultConfig.getInt("global_settings.player_notices.theft.distance", 125));
            setupWorld.setTheft_Delay(getStorageReference.getDefaultConfig.getDouble("global_settings.player_notices.theft.delay", 0.3));

            setupWorld.onNPC_Warning = getStorageReference.getDefaultConfig.getStringList("event_commands.npc_warning");
            setupWorld.onNPC_AlertGuards = getStorageReference.getDefaultConfig.getStringList("event_commands.npc_alertguards");
            setupWorld.onNPC_NoGuards = getStorageReference.getDefaultConfig.getStringList("event_commands.npc_noguards");
            setupWorld.onNPC_Murder = getStorageReference.getDefaultConfig.getStringList("event_commands.npc_murdered");
            setupWorld.onPlayer_Wanted = getStorageReference.getDefaultConfig.getStringList("event_commands.player_wanted");
            setupWorld.onPlayer_Arrest = getStorageReference.getDefaultConfig.getStringList("event_commands.player_arrested");
            setupWorld.onPlayer_Escaped = getStorageReference.getDefaultConfig.getStringList("event_commands.player_escaped");
            setupWorld.onPlayer_Released = getStorageReference.getDefaultConfig.getStringList("event_commands.player_released");

            world_Configurations.put("_GlobalSettings", setupWorld);

        }


        // Validate that we have a record for each world.
        for (World world : getStorageReference.pluginInstance.getServer().getWorlds()) {
            if (!world_Configurations.containsKey(world.getName())) {
                World_Setting newWorld = new World_Setting(world.getName());
                world_Configurations.put(world.getName(), newWorld);
            }
        }

    }

    private void loadWorldConfig(String worldName, YamlConfiguration jail_Settings) {

        World_Setting newWorld = null;

        if (worldName.equalsIgnoreCase("_GlobalSettings"))
            newWorld = new World_Setting();
        else
            newWorld = new World_Setting(worldName);

        if (!world_Configurations.containsKey(worldName))
            world_Configurations.put(worldName, newWorld);
        else
            newWorld = world_Configurations.get(worldName);

        // Load all the world settings first for this world
        if (jail_Settings.contains(worldName + ".groups.wanted_criminals"))
            newWorld.setWantedGroup(jail_Settings.getString(worldName + ".groups.wanted_criminals"));
        if (jail_Settings.contains(worldName + ".groups.jailed_criminals"))
            newWorld.setJailedGroup(jail_Settings.getString(worldName + ".groups.jailed_criminals"));
        if (jail_Settings.contains(worldName + ".groups.escaped_criminals"))
            newWorld.setEscapedGroup(jail_Settings.getString(worldName + ".groups.escaped_criminals"));

        // Bounty settings
        if (jail_Settings.contains(worldName + ".bounty.damage"))
            newWorld.setBounty_Damage(jail_Settings.getDouble(worldName + ".bounty.damage"));
        if (jail_Settings.contains(worldName + ".bounty.escaped"))
            newWorld.setBounty_Escaped(jail_Settings.getDouble(worldName + ".bounty.escaped"));
        if (jail_Settings.contains(worldName + ".bounty.murder"))
            newWorld.setBounty_Murder(jail_Settings.getDouble(worldName + ".bounty.murder"));
        if (jail_Settings.contains(worldName + ".bounty.jailed_pvp"))
            newWorld.setBounty_PVP(jail_Settings.getDouble(worldName + ".bounty.jailed_pvp"));
        if (jail_Settings.contains(worldName + ".bounty.maximum"))
            newWorld.setBounty_Maximum(jail_Settings.getDouble(worldName + ".bounty.maximum"));

        // Wanted Level settings
        if (jail_Settings.contains(worldName + ".wanted.min"))
            newWorld.setMinimum_WantedLevel(net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL.valueOf(jail_Settings.getString(worldName + ".wanted.min")));
        if (jail_Settings.contains(worldName + ".wanted.max"))
            newWorld.setMaximum_WantedLevel(net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL.valueOf(jail_Settings.getString(worldName + ".wanted.max")));
        if (jail_Settings.contains(worldName + ".kick.type"))
            newWorld.setKickType(net.livecar.nuttyworks.npc_police.api.Enumerations.KICK_TYPE.valueOf(jail_Settings.getString(worldName + ".kick.type")));
        if (jail_Settings.contains(worldName + ".kick.location"))
            newWorld.setKickLocation(jail_Settings.getString(worldName + ".kick.location"));

        // Bounty time settings
        if (jail_Settings.contains(worldName + ".times.jailed_bounty"))
            newWorld.setTimeInterval_Jailed(jail_Settings.getDouble(worldName + ".times.jailed_bounty"));
        if (jail_Settings.contains(worldName + ".times.escaped_bounty"))
            newWorld.setTimeInterval_Escaped(jail_Settings.getDouble(worldName + ".times.escaped_bounty"));
        if (jail_Settings.contains(worldName + ".times.wanted_bounty"))
            newWorld.setTimeInterval_Wanted(jail_Settings.getDouble(worldName + ".times.wanted_bounty"));
        if (jail_Settings.contains(worldName + ".times.outofcell_day"))
            newWorld.setTimeInterval_CellDay(jail_Settings.getDouble(worldName + ".times.outofcell_day"));
        if (jail_Settings.contains(worldName + ".times.outofcell_night"))
            newWorld.setTimeInterval_CellNight(jail_Settings.getDouble(worldName + ".times.outofcell_night"));

        // NPC Settings
        if (jail_Settings.contains(worldName + ".npc.max_warning_damage"))
            newWorld.setWarning_MaximumDamage(jail_Settings.getInt(worldName + ".npc.max_warning_damage"));
        if (jail_Settings.contains(worldName + ".npc.max_distance_guard"))
            newWorld.setMaximum_GuardDistance(jail_Settings.getInt(worldName + ".npc.max_distance_guard"));
        if (jail_Settings.contains(worldName + ".npc.protect_only_assigned"))
            newWorld.setProtect_OnlyAssigned(jail_Settings.getBoolean(worldName + ".npc.protect_only_assigned") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
        if (jail_Settings.contains(worldName + ".npc.min_bounty_wanted"))
            newWorld.setMinimum_WantedBounty(jail_Settings.getInt(worldName + ".npc.min_bounty_wanted"));
        if (jail_Settings.contains(worldName + ".npc.monitor_pvp"))
            newWorld.setMonitorPVP(jail_Settings.getBoolean(worldName + ".npc.monitor_pvp") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
        if (jail_Settings.contains(worldName + ".npc.los_attack"))
            newWorld.setLOSAttackSetting(jail_Settings.getBoolean(worldName + ".npc.los_attack") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);

        // Player Notice Settings
        if (jail_Settings.contains(worldName + ".player_notices.escaped.distance"))
            newWorld.setEscaped_Distance(jail_Settings.getInt(worldName + ".player_notices.escaped.distance"));
        if (jail_Settings.contains(worldName + ".player_notices.escaped.delay"))
            newWorld.setEscaped_Delay(jail_Settings.getDouble(worldName + ".player_notices.escaped.delay"));
        if (jail_Settings.contains(worldName + ".player_notices.arrested.distance"))
            newWorld.setJailed_Distance(jail_Settings.getInt(worldName + ".player_notices.arrested.distance"));
        if (jail_Settings.contains(worldName + ".player_notices.arrested.delay"))
            newWorld.setJailed_Delay(jail_Settings.getDouble(worldName + ".player_notices.arrested.delay"));
        if (jail_Settings.contains(worldName + ".player_notices.murder.distance"))
            newWorld.setMurder_Distance(jail_Settings.getInt(worldName + ".player_notices.murder.distance"));
        if (jail_Settings.contains(worldName + ".player_notices.murder.delay"))
            newWorld.setMurder_Delay(jail_Settings.getDouble(worldName + ".player_notices.murder.delay"));

        if (jail_Settings.contains(worldName + ".event_commands.player_arrested"))
            newWorld.onPlayer_Arrest.addAll(jail_Settings.getStringList(worldName + ".event_commands.player_arrested"));
        if (jail_Settings.contains(worldName + ".event_commands.player_escaped"))
            newWorld.onPlayer_Escaped.addAll(jail_Settings.getStringList(worldName + ".event_commands.player_escaped"));
        if (jail_Settings.contains(worldName + ".event_commands.player_released"))
            newWorld.onPlayer_Released.addAll(jail_Settings.getStringList(worldName + ".event_commands.player_released"));
        if (jail_Settings.contains(worldName + ".event_commands.player_wanted"))
            newWorld.onPlayer_Wanted.addAll(jail_Settings.getStringList(worldName + ".event_commands.player_wanted"));

        if (jail_Settings.contains(worldName + ".event_commands.npc_warning"))
            newWorld.onNPC_Warning.addAll(jail_Settings.getStringList(worldName + ".event_commands.npc_warning"));
        if (jail_Settings.contains(worldName + ".event_commands.npc_alertguards"))
            newWorld.onNPC_AlertGuards.addAll(jail_Settings.getStringList(worldName + ".event_commands.npc_alertguards"));
        if (jail_Settings.contains(worldName + ".event_commands.npc_noguards"))
            newWorld.onNPC_NoGuards.addAll(jail_Settings.getStringList(worldName + ".event_commands.npc_noguards"));
        if (jail_Settings.contains(worldName + ".event_commands.npc_murdered"))
            newWorld.onNPC_Murder.addAll(jail_Settings.getStringList(worldName + ".event_commands.npc_murdered"));
        if (jail_Settings.contains(worldName + ".event_commands.bounty_maximum"))
            newWorld.onBounty_Maximum.addAll(jail_Settings.getStringList(worldName + ".event_commands.bounty_maximum"));

        if (jail_Settings.contains(worldName + ".inventory.takeonarrest"))
            newWorld.onArrest_InventoryAction(jail_Settings.getBoolean(worldName + ".inventory.takeonarrest") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
        if (jail_Settings.contains(worldName + ".inventory.returnonescape"))
            newWorld.onEscape_InventoryAction(jail_Settings.getBoolean(worldName + ".inventory.returnonescape") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);
        if (jail_Settings.contains(worldName + ".inventory.returnonfree"))
            newWorld.onFree_InventoryAction(jail_Settings.getBoolean(worldName + ".inventory.returnonfree") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE);

        if (jail_Settings.contains(worldName + ".banneditems")) {
            for (int cnt = 0; cnt < newWorld.bannedItems.length; cnt++) {
                if (jail_Settings.contains(worldName + ".banneditems." + cnt)) {
                    newWorld.bannedItems[cnt] = jail_Settings.getItemStack(worldName + ".banneditems." + cnt);
                }
            }
        }

        if (jail_Settings.contains(worldName + ".jails.")) {
            for (String jailID : jail_Settings.getConfigurationSection(worldName + ".jails.").getKeys(false)) {
                Jail_Setting jailSetting = new Jail_Setting();
                jailSetting.jailName = jailID;
                jailSetting.jailWorld = getStorageReference.pluginInstance.getServer().getWorld(worldName);
                jailSetting.jail_ID = UUID.fromString(jail_Settings.getString(worldName + ".jails." + jailID + ".jail_id"));
                jailSetting.displayName = jail_Settings.getString(worldName + ".jails." + jailID + ".displayname");
                jailSetting.regionName = jail_Settings.getString(worldName + ".jails." + jailID + ".region");
                jailSetting.cellLocations = new ArrayList<Location>();
                jailSetting.onPlayer_Arrest = new ArrayList<String>();
                jailSetting.onPlayer_Escaped = new ArrayList<String>();
                jailSetting.onPlayer_Released = new ArrayList<String>();

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".bounties.escaped_bounty"))
                    jailSetting.bounty_Escaped = jail_Settings.getDouble(worldName + ".jails." + jailID + ".bounties.escaped_bounty");
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".bounties.jailed_pvp"))
                    jailSetting.bounty_PVP = jail_Settings.getDouble(worldName + ".jails." + jailID + ".bounties.jailed_pvp");

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".times.jailed_bounty"))
                    jailSetting.times_Jailed = jail_Settings.getDouble(worldName + ".jails." + jailID + ".times.jailed_bounty");
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".times.outofcell_day"))
                    jailSetting.times_CellDay = jail_Settings.getDouble(worldName + ".jails." + jailID + ".times.outofcell_day");
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".times.outofcell_night"))
                    jailSetting.times_CellNight = jail_Settings.getDouble(worldName + ".jails." + jailID + ".times.outofcell_night");

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".player_notices.escaped.distance"))
                    newWorld.setEscaped_Distance(jail_Settings.getInt(worldName + ".jails." + jailID + ".player_notices.escaped.distance"));
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".player_notices.escaped.delay"))
                    newWorld.setEscaped_Delay(jail_Settings.getDouble(worldName + ".jails." + jailID + ".player_notices.escaped.delay"));

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".cell_locations")) {
                    for (String cellID : jail_Settings.getConfigurationSection(worldName + ".jails." + jailID + ".cell_locations").getKeys(false)) {
                        Location cellLocation = new Location(getStorageReference.pluginInstance.getServer().getWorld(jail_Settings.getString(worldName + ".jails." + jailID + ".cell_locations." + cellID + ".world")), jail_Settings.getDouble(worldName + ".jails." + jailID + ".cell_locations." + cellID + ".x"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".cell_locations." + cellID + ".y"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".cell_locations." + cellID + ".z"), 0.0F, 0.0F).add(0.5, 0, 0.5);
                        jailSetting.cellLocations.add(cellLocation);
                    }
                }

                // Wanted Level settings
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".wanted.min"))
                    jailSetting.minWanted = net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL.valueOf(jail_Settings.getString(worldName + ".jails." + jailID + ".wanted.min"));
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".wanted.max"))
                    jailSetting.maxWanted = net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL.valueOf(jail_Settings.getString(worldName + ".jails." + jailID + ".wanted.max"));

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".event_commands.player_escaped")) {
                    jailSetting.onPlayer_Arrest.addAll(jail_Settings.getStringList(worldName + ".jails." + jailID + ".event_commands.player_escaped"));
                }
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".event_commands.player_escaped")) {
                    jailSetting.onPlayer_Escaped.addAll(jail_Settings.getStringList(worldName + ".jails." + jailID + ".event_commands.player_escaped."));
                }
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".event_commands.player_released")) {
                    jailSetting.onPlayer_Released.addAll(jail_Settings.getStringList(worldName + ".jails." + jailID + ".event_commands.player_released"));
                }

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".inventory.takeonarrest"))
                    jailSetting.onArrest_InventoryAction = jail_Settings.getBoolean(worldName + ".jails." + jailID + ".inventory.takeonarrest") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".inventory.returnonescape"))
                    jailSetting.onEscape_InventoryAction = jail_Settings.getBoolean(worldName + ".jails." + jailID + ".inventory.returnonescape") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;
                if (jail_Settings.contains(worldName + ".jails." + jailID + ".inventory.returnonfree"))
                    jailSetting.onFree_InventoryAction = jail_Settings.getBoolean(worldName + ".jails." + jailID + ".inventory.returnonfree") ? STATE_SETTING.TRUE : STATE_SETTING.FALSE;

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".inventory.chestlocation"))
                    jailSetting.lockedInventoryLocation = new Location(getStorageReference.pluginInstance.getServer().getWorld(jail_Settings.getString(worldName + ".jails." + jailID + ".inventory.chestlocation.world")), jail_Settings.getDouble(worldName + ".jails." + jailID + ".inventory.chestlocation.x"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".inventory.chestlocation.y"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".inventory.chestlocation.z"), 0.0F, 0.0F);

                if (jail_Settings.contains(worldName + ".jails." + jailID + ".spawns.exitlocation"))
                    jailSetting.freeSpawnPoint = new Location(getStorageReference.pluginInstance.getServer().getWorld(jail_Settings.getString(worldName + ".jails." + jailID + ".spawns.exitlocation.world")), jail_Settings.getDouble(worldName + ".jails." + jailID + ".spawns.exitlocation.x"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".spawns.exitlocation.y"), jail_Settings.getDouble(worldName + ".jails." + jailID + ".spawns.exitlocation.z"), 0.0F, 0.0F);

                newWorld.jail_Configs.put(jailSetting.jailName.toLowerCase(), jailSetting);
            }
        }

        world_Configurations.put(worldName, newWorld);
    }

    public void saveJailSettings() {
        if (!getStorageReference.pluginInstance.getDataFolder().exists())
            getStorageReference.pluginInstance.getDataFolder().mkdirs();
        File jailConfigFile = new File(getStorageReference.pluginInstance.getDataFolder(), "jail_settings.yml");
        YamlConfiguration jail_Settings = new YamlConfiguration();

        //Log to the console that we loaded worlds.
        String worldList = "";

        for (World_Setting worldConfig : world_Configurations.values()) {
            if (getStorageReference.pluginInstance.getServer().getWorld(worldConfig.getWorldName()) == null && !worldConfig.getWorldName().equalsIgnoreCase("_GlobalSettings"))
                continue;

            worldList += "[" + worldConfig.getWorldName() + "] ";

            // Load all the world settings first for this world
            if (!worldConfig.getWantedGroup().equalsIgnoreCase(""))
                jail_Settings.set(worldConfig.getWorldName() + ".groups.wanted_criminals", worldConfig.getWantedGroup());
            if (!worldConfig.getJailedGroup().equalsIgnoreCase(""))
                jail_Settings.set(worldConfig.getWorldName() + ".groups.jailed_criminals", worldConfig.getJailedGroup());
            if (!worldConfig.getEscapedGroup().equalsIgnoreCase(""))
                jail_Settings.set(worldConfig.getWorldName() + ".groups.escaped_criminals", worldConfig.getEscapedGroup());

            // Bounty settings
            if (worldConfig.getBounty_Damage() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".bounty.damage", worldConfig.getBounty_Damage());
            if (worldConfig.getBounty_Escaped() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".bounty.escaped", worldConfig.getBounty_Escaped());
            if (worldConfig.getBounty_Murder() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".bounty.murder", worldConfig.getBounty_Murder());
            if (worldConfig.getBounty_PVP() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".bounty.jailed_pvp", worldConfig.getBounty_PVP());
            if (worldConfig.getBounty_Maximum() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".bounty.maximum", worldConfig.getBounty_Maximum());

            // Bounty time settings
            if (worldConfig.getTimeInterval_Jailed() != Double.MIN_VALUE)
                jail_Settings.set(worldConfig.getWorldName() + ".times.jailed_bounty", worldConfig.getTimeInterval_Jailed());
            if (worldConfig.getTimeInterval_Escaped() != Double.MIN_VALUE)
                jail_Settings.set(worldConfig.getWorldName() + ".times.escaped_bounty", worldConfig.getTimeInterval_Escaped());
            if (worldConfig.getTimeInterval_Wanted() != Double.MIN_VALUE)
                jail_Settings.set(worldConfig.getWorldName() + ".times.wanted_bounty", worldConfig.getTimeInterval_Wanted());
            if (worldConfig.getTimeInterval_CellDay() != Double.MIN_VALUE)
                jail_Settings.set(worldConfig.getWorldName() + ".times.outofcell_day", worldConfig.getTimeInterval_CellDay());
            if (worldConfig.getTimeInterval_CellNight() != Double.MIN_VALUE)
                jail_Settings.set(worldConfig.getWorldName() + ".times.outofcell_night", worldConfig.getTimeInterval_CellNight());

            // Wanted Level settings
            if (worldConfig.getMinimum_WantedLevel() != WANTED_LEVEL.GLOBAL)
                jail_Settings.set(worldConfig.getWorldName() + ".wanted.min", worldConfig.getMinimum_WantedLevel().toString());
            if (worldConfig.getMaximum_WantedLevel() != WANTED_LEVEL.GLOBAL)
                jail_Settings.set(worldConfig.getWorldName() + ".wanted.max", worldConfig.getMaximum_WantedLevel().toString());
            if (worldConfig.getKickType() != KICK_TYPE.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".kick.type", worldConfig.getKickType().toString());
            if (worldConfig.getKickLocation() != "")
                jail_Settings.set(worldConfig.getWorldName() + ".kick.location", worldConfig.getKickLocation().toString());

            // NPC Settings
            if (worldConfig.getWarning_MaximumDamage() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.max_warning_damage", worldConfig.getWarning_MaximumDamage());
            if (worldConfig.getMaximum_GardDistance() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.max_distance_guard", worldConfig.getMaximum_GardDistance());
            if (worldConfig.getProtect_OnlyAssigned() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.protect_only_assigned", worldConfig.getProtect_OnlyAssigned() == STATE_SETTING.TRUE ? true : false);
            if (worldConfig.getMinumum_WantedBounty() != -1)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.min_bounty_wanted", worldConfig.getMinumum_WantedBounty());
            if (worldConfig.getMonitorPVP() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.monitor_pvp", worldConfig.getMonitorPVP() == STATE_SETTING.TRUE ? true : false);
            if (worldConfig.getLOSAttackSetting() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".npc.los_attack", worldConfig.getLOSAttackSetting() == STATE_SETTING.TRUE ? true : false);

            // Player Notice Settings
            if (worldConfig.getEscaped_Distance() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.escaped.distance", worldConfig.getEscaped_Distance());
            if (worldConfig.getEscaped_Delay() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.escaped.delay", worldConfig.getEscaped_Delay());
            if (worldConfig.getJailed_Distance() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.arrested.distance", worldConfig.getJailed_Distance());
            if (worldConfig.getJailed_Delay() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.arrested.delay", worldConfig.getJailed_Delay());
            if (worldConfig.getMurder_Distance() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.murder.distance", worldConfig.getMurder_Distance());
            if (worldConfig.getMurder_Delay() > -1)
                jail_Settings.set(worldConfig.getWorldName() + ".player_notices.murder.delay", worldConfig.getMurder_Delay());

            // World inventory Settings
            if (worldConfig.onArrest_InventoryAction() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".inventory.takeonarrest", worldConfig.onArrest_InventoryAction() == STATE_SETTING.TRUE ? true : false);
            if (worldConfig.onEscape_InventoryAction() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".inventory.returnonescape", worldConfig.onEscape_InventoryAction() == STATE_SETTING.TRUE ? true : false);
            if (worldConfig.onFree_InventoryAction() != STATE_SETTING.NOTSET)
                jail_Settings.set(worldConfig.getWorldName() + ".inventory.returnonfree", worldConfig.onFree_InventoryAction() == STATE_SETTING.TRUE ? true : false);

            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.player_arrested", worldConfig.onPlayer_Arrest);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.player_escaped", worldConfig.onPlayer_Escaped);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.player_released", worldConfig.onPlayer_Released);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.player_wanted", worldConfig.onPlayer_Wanted);

            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.npc_warning", worldConfig.onNPC_Warning);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.npc_alertguards", worldConfig.onNPC_AlertGuards);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.npc_noguards", worldConfig.onNPC_NoGuards);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.npc_murdered", worldConfig.onNPC_Murder);
            jail_Settings.set(worldConfig.getWorldName() + ".event_commands.bounty_maximum", worldConfig.onBounty_Maximum);

            int itemCounter = 0;
            for (int cnt = 0; cnt < worldConfig.bannedItems.length; cnt++) {
                if (worldConfig.bannedItems[cnt] != null && worldConfig.bannedItems[cnt].getType() != Material.AIR) {
                    jail_Settings.set(worldConfig.getWorldName() + ".banneditems." + itemCounter, worldConfig.bannedItems[cnt]);
                    itemCounter++;
                }
            }

            for (Jail_Setting jailSetting : worldConfig.jail_Configs.values()) {
                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".jail_id", jailSetting.jail_ID.toString());
                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".displayname", jailSetting.displayName);
                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".region", jailSetting.regionName);

                if (jailSetting.bounty_Escaped > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".bounties.escaped_bounty", jailSetting.bounty_Escaped);
                if (jailSetting.bounty_PVP > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".bounties.jailed_pvp", jailSetting.bounty_PVP);

                if (jailSetting.times_Jailed > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".times.jailed_bounty", jailSetting.times_Jailed);
                if (jailSetting.times_CellDay > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".times.outofcell_day", jailSetting.times_CellDay);
                if (jailSetting.times_CellNight > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".times.outofcell_night", jailSetting.times_CellNight);

                if (jailSetting.escaped_Distance > -1)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".player_notices.escaped.distance", jailSetting.escaped_Distance);
                if (jailSetting.escaped_Delay > -1.0D)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".player_notices.escaped.delay", jailSetting.escaped_Delay);

                for (int locCounter = 0; locCounter < jailSetting.cellLocations.size(); locCounter++) {
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".cell_locations." + locCounter + ".world", jailSetting.cellLocations.get(locCounter).getWorld().getName());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".cell_locations." + locCounter + "..x", jailSetting.cellLocations.get(locCounter).getBlockX());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".cell_locations." + locCounter + ".y", jailSetting.cellLocations.get(locCounter).getBlockY());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".cell_locations." + locCounter + ".z", jailSetting.cellLocations.get(locCounter).getBlockZ());
                }

                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".event_commands.player_arrested", jailSetting.onPlayer_Arrest);
                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".event_commands.player_escaped", jailSetting.onPlayer_Escaped);
                jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".event_commands.player_released", jailSetting.onPlayer_Released);

                // Wanted Level settings
                if (jailSetting.minWanted != WANTED_LEVEL.GLOBAL)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".wanted.min", jailSetting.minWanted.toString());
                if (jailSetting.maxWanted != WANTED_LEVEL.GLOBAL)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".wanted.max", jailSetting.maxWanted.toString());

                // World inventory Settings
                if (jailSetting.onArrest_InventoryAction != STATE_SETTING.NOTSET)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.takeonarrest", jailSetting.onArrest_InventoryAction == STATE_SETTING.TRUE ? true : false);
                if (jailSetting.onEscape_InventoryAction != STATE_SETTING.NOTSET)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.returnonescape", jailSetting.onEscape_InventoryAction == STATE_SETTING.TRUE ? true : false);
                if (jailSetting.onFree_InventoryAction != STATE_SETTING.NOTSET)
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.returnonfree", jailSetting.onFree_InventoryAction == STATE_SETTING.TRUE ? true : false);

                if (jailSetting.lockedInventoryLocation != null) {
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.chestlocation.world", jailSetting.lockedInventoryLocation.getWorld().getName());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.chestlocation.x", jailSetting.lockedInventoryLocation.getBlockX());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.chestlocation.y", jailSetting.lockedInventoryLocation.getBlockY());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".inventory.chestlocation.z", jailSetting.lockedInventoryLocation.getBlockZ());
                }

                if (jailSetting.freeSpawnPoint != null) {
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".spawns.exitlocation.world", jailSetting.freeSpawnPoint.getWorld().getName());
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".spawns.exitlocation.x", jailSetting.freeSpawnPoint.getBlockX() + 0.5D);
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".spawns.exitlocation.y", jailSetting.freeSpawnPoint.getBlockY() + 0.5D);
                    jail_Settings.set(worldConfig.getWorldName() + ".jails." + jailSetting.jailName.toLowerCase() + ".spawns.exitlocation.z", jailSetting.freeSpawnPoint.getBlockZ() + 0.5D);
                }

            }

        }
        try {
            jail_Settings.save(jailConfigFile);
        } catch (IOException e) {
            // Problem return and don't save (Not right)
            return;
        }
        getStorageReference.getMessageManager.consoleMessage("console_messages.plugin_savedworld", worldList);

    }

}
