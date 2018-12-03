package net.livecar.nuttyworks.npc_police;

import net.livecar.nuttyworks.npc_police.api.InvalidJailException;
import net.livecar.nuttyworks.npc_police.api.InvalidWorldException;
import net.livecar.nuttyworks.npc_police.api.managers.Core_JailManager;
import net.livecar.nuttyworks.npc_police.api.managers.Core_PlayerManager;
import net.livecar.nuttyworks.npc_police.api.managers.JailManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.bridges.APIBridge;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * API is class that allows custom communication with the NPCPolice plugin from
 * your own plugins. This allows further expansion of the plugin without the
 * need to modify the base plugin itself. It also ensures that your
 * customization points do not break, based on changes that I do to the plugin
 * internally down the road.
 *
 * @author Sir_Nutty (Bruce Johnson)
 * @since 2.1.8
 */
public class Core_API extends APIBridge {
    private static NPC_Police getStorageReference = null;

    public Core_API(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    @Override
    public PlayerManager getPlayerManager(OfflinePlayer player) {
        return new Core_PlayerManager(getStorageReference, player.getUniqueId());
    }

    @Override
    public List<String> getWorldJailNames(World world) throws InvalidWorldException {
        if (getStorageReference.getJailManager.containsWorld(world.getName()))
            throw new InvalidWorldException();

        List<String> jailNames = new ArrayList<String>();
        for (Jail_Setting jailRecord : getStorageReference.getJailManager.getWorldJails(world.getName())) {
            jailNames.add(jailRecord.jailName);
        }
        return jailNames;
    }

    @Override
    public JailManager getJailManager(String jailName) throws InvalidJailException {
        Jail_Setting jailRecord = getStorageReference.getJailManager.getJailByName(jailName);
        if (jailRecord == null)
            throw new InvalidJailException();
        return new Core_JailManager(jailRecord);
    }

    @Override
    public JailManager getJailManager(Location jailLocation) throws InvalidJailException {
        Jail_Setting jailRecord = getStorageReference.getJailManager.getJailAtLocation(jailLocation);
        if (jailRecord == null)
            throw new InvalidJailException();
        return new Core_JailManager(jailRecord);
    }

    @Override
    public void disableMonitoringDamage(boolean disable) {
        getStorageReference.disableDamageMonitoring = disable;
    }

    @Override
    public void processCustomDamageEvent(EntityDamageByEntityEvent event) {
        getStorageReference.getCustomDamageListenerClass.customEntityDamageByEntity(event);
    }

    @Override
    public RegionSettings getRegionSettings(Location location) {
        return getStorageReference.getWorldGuardPlugin.getRelatedRegionFlags(location);
    }

}
