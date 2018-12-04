package net.livecar.nuttyworks.npc_police.api.managers;

import org.bukkit.Location;

import java.util.List;

/**
 * JailManager is a class that provides access to player, and jail objects.
 *
 * @author Sir_Nutty (Bruce Johnson)
 * @since 2.1.1
 */
public abstract class JailManager {

    /**
     * This method provides access to the jails region setting.
     *
     * @return <code>String</code> returns the primary region name for selected jail
     * @since 2.1.1
     */
    abstract public String getJailRegion();

    /**
     * This method provides access to the jails display name.
     *
     * @return <code>String</code> returns the selected jail's display name.
     * @since 2.1.1
     */
    abstract public String getJailDisplayName();

    /**
     * This method provides access the jail's configured cell locations
     *
     * @return <code>List<Location></code> returns a list object populated with bukkit locations for each jail cell
     * @since 2.1.1
     */
    abstract public List<Location> getCellLocations();
}
