package net.livecar.nuttyworks.npc_police.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BungeeCordListener implements PluginMessageListener {
    // BungeeCord Checks
    public STATE_SETTING bungeeCordEnabled = STATE_SETTING.NOTSET;

    private NPC_Police getStorageReference = null;
    private String[] serverList = new String[]{"none"};

    public BungeeCordListener(NPC_Police policeRef) {
        getStorageReference = policeRef;

        getStorageReference.pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(getStorageReference.pluginInstance, "BungeeCord");
        getStorageReference.pluginInstance.getServer().getMessenger().registerIncomingPluginChannel(getStorageReference.pluginInstance, "BungeeCord", this);
    }

    public void startBungeeChecks(Player player) {
        if (bungeeCordEnabled != STATE_SETTING.NOTSET)
            return;

        this.bungeeCordEnabled = STATE_SETTING.FALSE;

        final Player fnlPlayer = player;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("GetServers");

                fnlPlayer.sendPluginMessage(getStorageReference.pluginInstance, "BungeeCord", out.toByteArray());
            }
        }, 500);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player plr, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (!subchannel.equalsIgnoreCase("GetServers"))
            return;

        String serverList = in.readUTF();
        if (!serverList.isEmpty()) {
            this.serverList = serverList.trim().split("\\s*,\\s*");
        }
    }

    public List<String> getServerList() {
        return Arrays.asList(serverList);
    }

    public void switchServer(Player plr, String serverName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeUTF("Connect");
            dos.writeUTF(serverName);
            plr.sendPluginMessage(getStorageReference.pluginInstance, "BungeeCord", baos.toByteArray());
            baos.close();
            dos.close();
        } catch (IOException e) {
        }
    }

}
