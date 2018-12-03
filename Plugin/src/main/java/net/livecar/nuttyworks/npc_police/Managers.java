package net.livecar.nuttyworks.npc_police;

import net.livecar.nuttyworks.npc_police.api.InvalidJailException;
import net.livecar.nuttyworks.npc_police.api.InvalidWorldException;
import net.livecar.nuttyworks.npc_police.api.managers.JailManager;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.List;

/**
 * Managers is a class that provides access to player, and jail objects.
 *
 * @author Sir_Nutty (Bruce Johnson)
 * @since 2.1.1
 * @deprecated this class has been renamed to API for uniformity
 */
public class Managers {

    /**
     * @param player
     * @deprecated this class has been renamed to API for uniformity </br>
     * Please use API.getPlayerManager(player)
     *
     * <blockquote>
     *
     * <pre>
     * net.livecar.nuttyworks.npc_police.API.getPlayerManager(OfflinePlayer player)
     *             </pre>
     *
     * </blockquote>
     */
    @Deprecated
    public static PlayerManager getPlayerManager(OfflinePlayer player) {
        return API.getPlayerManager(player);
    }

    /**
     * @param jailName
     * @deprecated this class has been renamed to API for uniformity </br>
     * Please use API.getJailManager(jailName)
     *
     * <blockquote>
     *
     * <pre>
     * net.livecar.nuttyworks.npc_police.API.getJailManager(String jailName)
     *             </pre>
     *
     * </blockquote>
     */
    @Deprecated
    public static JailManager getJailManager(String jailName) throws InvalidJailException {
        return API.getJailManager(jailName);
    }

    /**
     * @param jailLocation
     * @deprecated this class has been renamed to API for uniformity </br>
     * Please use API.getJailManager(jailLocation)
     *
     * <blockquote>
     *
     * <pre>
     * net.livecar.nuttyworks.npc_police.API.getJailManager(Location jailLocation)
     *             </pre>
     *
     * </blockquote>
     */
    @Deprecated
    public static JailManager getJailManager(Location jailLocation) throws InvalidJailException {
        return API.getJailManager(jailLocation);
    }

    /**
     * @param world
     * @deprecated this class has been renamed to API for uniformity </br>
     * Please use API.getWorldJailNames(world)
     *
     * <blockquote>
     *
     * <pre>
     * net.livecar.nuttyworks.npc_police.API.getWorldJailNames(World world)
     *             </pre>
     *
     * </blockquote>
     */
    @Deprecated
    public List<String> getWorldJailNames(World world) throws InvalidWorldException {
        return API.getWorldJailNames(world);
    }

}
