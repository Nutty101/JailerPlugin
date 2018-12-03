package net.livecar.nuttyworks.npc_police;

import net.livecar.nuttyworks.npc_police.api.InvalidJailException;
import net.livecar.nuttyworks.npc_police.api.InvalidWorldException;
import net.livecar.nuttyworks.npc_police.api.managers.JailManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import net.livecar.nuttyworks.npc_police.bridges.APIBridge;
import net.livecar.nuttyworks.npc_police.worldguard.RegionSettings;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
public class API {
    private static APIBridge getStorageReference = null;

    static void setReference(APIBridge bridgeReference) {
        getStorageReference = bridgeReference;
    }

    /**
     * This method provides access to the players information, as well as custom
     * methods to change information on that player.
     *
     * @param player OfflinePlayer class from bukkit representing the player you want
     *               to gain information on.
     * @return <code>PlayerManager</code> returns an object that allows for
     * interaction with the players record.
     * @since 2.1.1
     */
    public static PlayerManager getPlayerManager(OfflinePlayer player) {
        return getStorageReference.getPlayerManager(player);
    }

    /**
     * This method provides access to a list of jail names for a specific world.
     *
     * @param world Pass a world object from bukkit representing the world you would
     *              like to get string list of all jail names
     * @return <code>List<String></code> returns a listing of all jail shortnames
     * in the referenced world
     * @since 2.1.1
     */
    public static List<String> getWorldJailNames(World world) throws InvalidWorldException {
        return getStorageReference.getWorldJailNames(world);
    }

    /**
     * This method provides access to the configuration settings for the named
     * jail.
     *
     * @param jailName Pass a string object with the jails short name like to get a
     *                 configuration object for
     * @return <code>JailManager</code> returns an object with methods and
     * configuration information
     * @since 2.1.1
     */
    public static JailManager getJailManager(String jailName) throws InvalidJailException {
        return getStorageReference.getJailManager(jailName);
    }

    /**
     * This method provides access to the configuration settings for any jail in
     * the location specified.
     *
     * @param jailLocation Pass a bukkit Location object to get any jail at the specific
     *                     point
     * @return <code>JailManager</code> returns an object with methods and
     * configuration information
     * @since 2.1.1
     */
    public static JailManager getJailManager(Location jailLocation) throws InvalidJailException {
        return getStorageReference.getJailManager(jailLocation);
    }

    /**
     * This method provides access to the configuration settings for any jail in
     * the location specified.
     *
     * @param disable Passing a true will stop the plugin from processing damage based
     *                on bukkit events
     * @since 2.1.1
     */
    public static void disableMonitoringDamage(boolean disable) {
        getStorageReference.disableMonitoringDamage(disable);
    }

    /**
     * This method allows passing customized damage from another plugin to the
     * NPCPolice plugin.
     * <p>
     * Make sure you stop all damage processing via
     * disableMonitoringDamage(boolean), otherwise you can cause double events.
     *
     * @param event Pass a new EntityDamageByEntityEvent, or a modified event with
     *              your damage modifier.
     * @since 2.1.1
     */
    public static void processCustomDamageEvent(EntityDamageByEntityEvent event) {
        getStorageReference.processCustomDamageEvent(event);
    }

    /**
     * This method allows the conversion of a location to the region settings
     * assigned to the worldguard regions that are relevant at the location
     * <p>
     *
     * @param location Pass a Bukkit location for conversion
     * @since 2.1.1
     */
    public static RegionSettings getRegionSettings(Location location) {
        return getStorageReference.getRegionSettings(location);
    }
}
