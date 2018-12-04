package net.livecar.nuttyworks.npc_police.database;

import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map.Entry;

public interface Database_Interface {
    void openDatabase();

    Arrest_Record getUserData(OfflinePlayer player);

    void saveUserData(Arrest_Record playerData);

    boolean isSleeping();

    void closeConnections();

    // Leaderhead results
    List<Entry<?, Double>> getLastArrests();

    List<Entry<?, Double>> getLastEscapes();

    List<Entry<?, Double>> getMostMurders();

    List<Entry<?, Double>> getMostEscapes();

    List<Entry<?, Double>> getMostArrests();

    List<Entry<?, Double>> getCurrentBounties();

    List<Entry<?, Double>> getTotalBounties();
}
