package net.livecar.nuttyworks.npc_police.worldguard;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldGuard_7_0_3_RegionalHandler extends Handler {

    public static final Factory FACTORY = new Factory();
    private static final long MESSAGE_THRESHOLD = 1000 * 2;
    private long lastMessage;

    public WorldGuard_7_0_3_RegionalHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, com.sk89q.worldedit.util.Location from, com.sk89q.worldedit.util.Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (entered.size() == 0)
            return true;

        Player bukPlayer =  Bukkit.getPlayer(player.getUniqueId());

        if (bukPlayer == null)
            return true;

        //Get the player manager reference
        PlayerManager plrManager = API.getPlayerManager(bukPlayer);

        if (plrManager == null)
            return true;

        Player plr = Bukkit.getPlayer(player.getUniqueId());

        //Get the regional settings
        World newWorld = (World) to.getExtent();
        RegionSettings regionFlags = API.getRegionSettings(new Location(Bukkit.getWorld(newWorld.getName()), to.getX(), to.getY(), to.getZ()));

        if (regionFlags.wanted_Forced != null) {
            plrManager.setWantedLevelForced(regionFlags.wanted_Forced, JAILED_BOUNTY.REGION, true);
        } else if (regionFlags.wanted_Change != null) {
            if (WANTED_SETTING.getWantedLevel(regionFlags.wanted_Change).ordinal() > plrManager.getWantedLevel().ordinal()) {
                if (regionFlags.wanted_Change != WANTED_SETTING.LEVELDOWN && regionFlags.wanted_Change != WANTED_SETTING.LEVELUP)
                    plrManager.setWantedLevel(regionFlags.wanted_Change, JAILED_BOUNTY.REGION);
            }
        }

        if (regionFlags.wanted_DenyMin == WANTED_SETTING.NONE)
            return true;

        if (regionFlags.wanted_DenyMin.ordinal() < plrManager.getWantedLevel().ordinal() || regionFlags.wanted_DenyMax.ordinal() > plrManager.getWantedLevel().ordinal()) {
            if (!getSession().getManager().hasBypass(player, (World) from.getExtent()) && moveType.isCancellable()) {
                String message = toSet.queryValue(null, Flags.ENTRY_DENY_MESSAGE);
                long now = System.currentTimeMillis();

                if ((now - lastMessage) > MESSAGE_THRESHOLD && message != null && !message.isEmpty()) {
                    plr.sendMessage(WorldGuard_7_0_3.replaceColorMacros(message));
                    lastMessage = now;
                }

                return false;
            }
        }

        return true;
    }

    public static class Factory extends Handler.Factory<WorldGuard_7_0_3_RegionalHandler> {
        @Override
        public WorldGuard_7_0_3_RegionalHandler create(Session session) {
            return new WorldGuard_7_0_3_RegionalHandler(session);
        }
    }
}
