package net.livecar.nuttyworks.npc_police;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.CitizensDisableEvent;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.listeners.BungeeCordListener;
import net.livecar.nuttyworks.npc_police.listeners.DamageListener;
import net.livecar.nuttyworks.npc_police.listeners.PlayerListener;
import net.livecar.nuttyworks.npc_police.metrics.BStat_Metrics;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9.BetonQuest_Plugin_V1_9;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads.LeaderHeads_Plugin;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.placeholderapi.PlaceHolder_Plugin;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.sentinel.Sentinel_Plugin_1_6;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.sentinel.Sentinel_Plugin_1_7;
import net.livecar.nuttyworks.npc_police.utilities.Utilities;
import net.livecar.nuttyworks.npc_police.worldguard.WorldGuard_6_2_2;
import net.livecar.nuttyworks.npc_police.worldguard.WorldGuard_7_0_0;
import net.livecar.nuttyworks.npc_police.worldguard.WorldGuard_7_0_1;
import net.livecar.nuttyworks.npc_police.worldguard.WorldGuard_7_0_3;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class NPCPolice_Plugin extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener, PluginMessageListener {
    public static NPCPolice_Plugin getPluginInstance = null;
    private NPC_Police policeStorage_Class = null;
    private boolean isEnabled = false;

    public NPCPolice_Plugin() {
        policeStorage_Class = new NPC_Police(this);
        getPluginInstance = this;
    }

    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            getServer().getLogger().log(Level.WARNING, "Worldguard not found, custom flags are not enabled");
        } else {
            String wgVer = getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            if (wgVer.contains(";"))
                wgVer = wgVer.substring(0, wgVer.indexOf(";"));
            if (wgVer.contains("-SNAPSHOT"))
                wgVer = wgVer.substring(0, wgVer.indexOf("-"));
            if (wgVer.startsWith("v"))
                wgVer = wgVer.substring(1);

            String[] parts = wgVer.split("[.]");

            int majorVersion = 0;
            policeStorage_Class.getUtilities = new Utilities(policeStorage_Class);

            boolean goodVersion = false;
            try {
                Integer[] verPart = new Integer[3];
                if (policeStorage_Class.getUtilities.isNumeric(parts[0])) {
                    verPart[0] = Integer.parseInt(parts[0]);
                }

                if (policeStorage_Class.getUtilities.isNumeric(parts[1])) {
                    verPart[1] = Integer.parseInt(parts[1]);
                }

                if (parts.length > 2 && policeStorage_Class.getUtilities.isNumeric(parts[2])) {
                    verPart[2] = Integer.parseInt(parts[2]);
                }

                if (verPart[0] == 6 && verPart[1] == 1 && verPart[2] >= 3) {
                    majorVersion = 6;
                    goodVersion = true;
                } else if (verPart[0] == 6 && verPart[1] > 1) {
                    majorVersion = 6;
                    goodVersion = true;
                } else if (verPart[0] > 6) {
                    majorVersion = 7;
                    goodVersion = true;
                }

            } catch (Exception err) {
                goodVersion = false;
            }

            if (!goodVersion) {
                getServer().getLogger().log(Level.WARNING, "This Worldguard version is not supported, custom flags are not enabled");
            } else {
                if (majorVersion == 6 && WorldGuard_6_2_2.isValidVersion())
                    policeStorage_Class.getWorldGuardPlugin = new WorldGuard_6_2_2();
                else if (majorVersion == 7 && WorldGuard_7_0_1.isValidVersion())
                    policeStorage_Class.getWorldGuardPlugin = new WorldGuard_7_0_1();
                else if (majorVersion == 7 && WorldGuard_7_0_3.isValidVersion())
                    policeStorage_Class.getWorldGuardPlugin = new WorldGuard_7_0_3();
                else if (majorVersion == 7 && WorldGuard_7_0_0.isValidVersion())
                    policeStorage_Class.getWorldGuardPlugin = new WorldGuard_7_0_0();

                policeStorage_Class.getWorldGuardPlugin.registerFlags();
            }
        }
    }

    public void onEnable() {
        // Setup the defaults
        if (!policeStorage_Class.pluginStartup()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Init links to other plugins
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false || !(getServer().getPluginManager().getPlugin("Citizens") instanceof Citizens)) {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.citizens_notfound", Level.SEVERE);
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            policeStorage_Class.getCitizensPlugin = (Citizens) getServer().getPluginManager().getPlugin("Citizens");
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.citizens_found", policeStorage_Class.getCitizensPlugin.getDescription().getVersion());
        }

        if (getServer().getPluginManager().getPlugin("BetonQuest") == null) {
            policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.betonquest_notfound", Level.WARNING);
        } else {
            // Get version 1.8.x or 1.9x
            Bukkit.getServer().getLogger().log(Level.ALL, "Version Check" + getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion());
            if (getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion().startsWith("1.9") || getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion().startsWith("1.10") || getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion().startsWith("2.")) {
                policeStorage_Class.bqPlugin = new BetonQuest_Plugin_V1_9();
                policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.betonquest_found", getServer().getPluginManager().getPlugin("BetonQuest").getDescription().getVersion());
            }
        }

        if (getServer().getPluginManager().getPlugin("Sentinel") == null) {
            policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.npcbattle_notfound");
        } else if (getServer().getPluginManager().getPlugin("Sentinel") != null) {

            if (Sentinel_Plugin_1_7.isValidVersion())
            {
                policeStorage_Class.getSentinelPlugin = new Sentinel_Plugin_1_7();
                policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.sentinel_found", policeStorage_Class.getSentinelPlugin.getVersionString());
            } else if (Sentinel_Plugin_1_6.isValidVersion()) {
                policeStorage_Class.getSentinelPlugin = new Sentinel_Plugin_1_6();
                policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.sentinel_found", policeStorage_Class.getSentinelPlugin.getVersionString());
            } else {
                policeStorage_Class.getSentinelPlugin = null;
                policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.sentinel_version");
            }
        }

        if (getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.worldguard_notfound", Level.SEVERE);
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            String wgVer = getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
            if (wgVer.contains(";"))
                wgVer = wgVer.substring(0, wgVer.indexOf(";"));
            if (wgVer.contains("-SNAPSHOT"))
                wgVer = wgVer.substring(0, wgVer.indexOf("-"));
            if (wgVer.startsWith("v"))
                wgVer = wgVer.substring(1);

            String[] parts = wgVer.split("[.]");

            int majorVersion = 0;
            policeStorage_Class.getUtilities = new Utilities(policeStorage_Class);

            boolean goodVersion = false;
            try {
                Integer[] verPart = new Integer[3];
                if (policeStorage_Class.getUtilities.isNumeric(parts[0])) {
                    verPart[0] = Integer.parseInt(parts[0]);
                }

                if (policeStorage_Class.getUtilities.isNumeric(parts[1])) {
                    verPart[1] = Integer.parseInt(parts[1]);
                }

                if (parts.length > 2 && policeStorage_Class.getUtilities.isNumeric(parts[2])) {
                    verPart[2] = Integer.parseInt(parts[2]);
                }

                if (verPart[0] == 6 && verPart[1] == 1 && verPart[2] >= 3) {
                    majorVersion = 6;
                    goodVersion = true;
                } else if (verPart[0] == 6 && verPart[1] > 1) {
                    majorVersion = 6;
                    goodVersion = true;
                } else if (verPart[0] > 6) {
                    majorVersion = 7;
                    goodVersion = true;
                }

            } catch (Exception err) {
                goodVersion = false;
            }

            if (!goodVersion) {
                policeStorage_Class.getMessageManager.consoleMessage("console_messages.worldguard_unsupported", getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion());
                getServer().getPluginManager().disablePlugin(this);
                return;
            } else {
                policeStorage_Class.getMessageManager.consoleMessage("console_messages.worldguard_found", getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion());
                if (majorVersion == 6)
                    Bukkit.getPluginManager().registerEvents((WorldGuard_6_2_2) policeStorage_Class.getWorldGuardPlugin, this);
                else if (majorVersion == 7)
                    Bukkit.getPluginManager().registerEvents(policeStorage_Class.getWorldGuardPlugin, this);

                policeStorage_Class.getWorldGuardPlugin.registerHandlers();
            }
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.placeholder_notfound");
        } else {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.placeholder_found", getServer().getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion());
            policeStorage_Class.getPlaceHolderPlugin = new PlaceHolder_Plugin(policeStorage_Class);
            //register with placeholder.
            policeStorage_Class.getPlaceHolderPlugin.register();
        }

        if (getServer().getPluginManager().getPlugin("LeaderHeads") == null) {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.leaderheads_notfound");
        } else {
            policeStorage_Class.getMessageManager.consoleMessage("console_messages.leaderheads_found", getServer().getPluginManager().getPlugin("LeaderHeads").getDescription().getVersion());
            policeStorage_Class.getLeaderHeadsPlugin = new LeaderHeads_Plugin(policeStorage_Class);
        }

        if (!policeStorage_Class.startProcesses()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register your trait with Citizens.
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(NPCPolice_Trait.class).withName("npcpolice"));

        // Register bungee class
        policeStorage_Class.getBungeeListener = new BungeeCordListener(policeStorage_Class);

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(policeStorage_Class), this);

        policeStorage_Class.getCustomDamageListenerClass = new DamageListener(policeStorage_Class);
        Bukkit.getPluginManager().registerEvents(policeStorage_Class.getCustomDamageListenerClass, this);

        isEnabled = true;

        API.setReference(new Core_API(policeStorage_Class));

        try {
            BStat_Metrics metrics = new BStat_Metrics(this);
            metrics.Start();
        } catch (Exception e) {
            // Wheee no stats, oh well.
        }
    }

    public void onDisable() {
        if (isEnabled) {
            policeStorage_Class.stopPlugin();
            Bukkit.getServer().getScheduler().cancelTasks(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] inargs) {
        if (cmd.getName().equalsIgnoreCase("npcpolice") | cmd.getName().equalsIgnoreCase("np")) {
            return policeStorage_Class.getCommandManager.onCommand(sender, inargs);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLabel, String[] inargs) {
        if (cmd.getName().equalsIgnoreCase("npcpolice") | cmd.getName().equalsIgnoreCase("np")) {
            return policeStorage_Class.getCommandManager.onTabComplete(sender, inargs);
        }
        return new ArrayList<String>();
    }

    @EventHandler
    public void onCitizensDisabled(final CitizensDisableEvent event) {
        Bukkit.getServer().getScheduler().cancelTasks(this);
        policeStorage_Class.getMessageManager.consoleMessage("Console_Messages.plugin_ondisable");
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onPluginMessageReceived(String arg0, Player arg1, byte[] arg2) {
        // TODO Auto-generated method stub

    }
}
