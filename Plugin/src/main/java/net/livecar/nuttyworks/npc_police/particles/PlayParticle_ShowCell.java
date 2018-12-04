package net.livecar.nuttyworks.npc_police.particles;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayParticle_ShowCell extends BukkitRunnable {

    private int loopCounter = 0;
    private Player playToPlayer = null;
    private NPC_Police getStorageReference = null;
    private Jail_Setting jailSetting = null;

    public PlayParticle_ShowCell(NPC_Police getStorageReference, Player player, Jail_Setting jailSetting) {
        loopCounter = 0;
        this.getStorageReference = getStorageReference;
        this.playToPlayer = player;
        this.jailSetting = jailSetting;
    }

    @Override
    public void run() {
        if (loopCounter < 25) {
            loopCounter++;
            for (Location cellLocation : jailSetting.cellLocations)
                getStorageReference.getVersionBridge.PlayOutParticle(cellLocation, playToPlayer);
        } else {
            this.cancel();
        }
    }

}
