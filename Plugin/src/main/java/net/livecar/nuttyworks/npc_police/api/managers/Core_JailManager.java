package net.livecar.nuttyworks.npc_police.api.managers;

import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import org.bukkit.Location;

import java.util.List;

/**
 * JailManager is a class that provides access to player, and jail objects.
 *
 * @author Sir_Nutty (Bruce Johnson)
 * @since 2.1.1
 */
public class Core_JailManager extends JailManager {
    private Jail_Setting jailRecord = null;

    public Core_JailManager(Jail_Setting jailRecord) {
        this.jailRecord = jailRecord;
    }

    @Override
    public String getJailRegion() {
        final String regionName = jailRecord.regionName;
        return regionName;
    }

    @Override
    public String getJailDisplayName() {
        final String displayName = jailRecord.displayName;
        return displayName;
    }

    @Override
    public List<Location> getCellLocations() {
        final List<Location> locations = jailRecord.cellLocations;
        return locations;
    }
}
