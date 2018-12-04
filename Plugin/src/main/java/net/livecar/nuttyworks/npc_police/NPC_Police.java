package net.livecar.nuttyworks.npc_police;

import net.citizensnpcs.Citizens;
import net.livecar.nuttyworks.npc_police.battlemanagers.BattleManager_Interface;
import net.livecar.nuttyworks.npc_police.bridges.*;
import net.livecar.nuttyworks.npc_police.database.Database_Manager;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_Interface;
import net.livecar.nuttyworks.npc_police.jails.Jail_Manager;
import net.livecar.nuttyworks.npc_police.listeners.BungeeCordListener;
import net.livecar.nuttyworks.npc_police.listeners.DamageListener;
import net.livecar.nuttyworks.npc_police.listeners.commands.*;
import net.livecar.nuttyworks.npc_police.messages.Language_Manager;
import net.livecar.nuttyworks.npc_police.messages.Messages_Manager;
import net.livecar.nuttyworks.npc_police.players.PlayerDataManager;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.BetonQuest_Interface;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.jobs_reborn.JobsReborn_Plugin;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.leaderheads.LeaderHeads_Plugin;
import net.livecar.nuttyworks.npc_police.thirdpartyplugins.placeholderapi.PlaceHolder_Plugin;
import net.livecar.nuttyworks.npc_police.utilities.Utilities;
import net.livecar.nuttyworks.npc_police.worldguard.VersionBridge;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class NPC_Police {
    // For quick reference to this instance of the plugin.
    public NPCPolice_Plugin pluginInstance;
    public FileConfiguration getDefaultConfig;

    // variables
    public String currentLanguage = "en_def";
    public Level debugLogLevel = Level.OFF;
    public int Version = 10000;
    public boolean disableDamageMonitoring = false;

    // Storage locations
    public File storagePath;
    public File languagePath;
    public File loggingPath;

    // Links to other classes
    public Language_Manager getLanguageManager;
    public Messages_Manager getMessageManager;
    public Utilities getUtilities = null;
    public PlayerDataManager getPlayerManager = null;
    public Jail_Manager getJailManager = null;
    public JailerGUI_Interface getGUIManager = null;
    public Database_Manager getDatabaseManager = null;
    public Command_Manager getCommandManager = null;
    public DamageListener getCustomDamageListenerClass = null;
    public MCUtilsBridge getVersionBridge = null;

    // Links to external plugin managers / classes
    public BungeeCordListener getBungeeListener = null;
    public Citizens getCitizensPlugin;
    public BetonQuest_Interface bqPlugin = null;
    public JobsReborn_Plugin getJobsRebornPlugin = null;
    public BattleManager_Interface getSentinelPlugin = null;
    public VersionBridge getWorldGuardPlugin = null;
    public Economy getEconomyManager = null;
    public Permission getPermissionManager = null;
    public PlaceHolder_Plugin getPlaceHolderPlugin = null;
    public LeaderHeads_Plugin getLeaderHeadsPlugin = null;

    public NPC_Police(NPCPolice_Plugin policePlugin) {
        pluginInstance = policePlugin;
    }

    boolean pluginStartup() {
        // Setup defaults
        getLanguageManager = new Language_Manager(this);
        getMessageManager = new Messages_Manager(this);
        getJailManager = new Jail_Manager(this);
        getGUIManager = new JailerGUI_Interface(this);
        getCommandManager = new Command_Manager(this);
        getUtilities = new Utilities(this);
        getPlayerManager = new PlayerDataManager(this);

        // Setup the default paths in the storage folder.
        storagePath = pluginInstance.getDataFolder();
        languagePath = new File(pluginInstance.getDataFolder(), "/Languages/");
        loggingPath = new File(pluginInstance.getDataFolder(), "/Logs/");

        // Generate the default folders and files.
        getDefaultConfigs();

        // Get languages
        getLanguageManager.loadLanguages();

        // Init Default settings
        if (this.getDefaultConfig.contains("language"))
            this.currentLanguage = this.getDefaultConfig.getString("language");
        if (this.currentLanguage.equalsIgnoreCase("en-def"))
            this.currentLanguage = "en_def";

        if (this.getDefaultConfig.contains("debug"))
            this.debugLogLevel = Level.parse(this.getDefaultConfig.getString("debug"));


        //Mark the version
        if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_8_R3")) {
            Version = 10808;
            getVersionBridge = new MCUtils_1_8_R3();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_9_R2")) {
            Version = 10902;
            getVersionBridge = new MCUtils_1_9_R2();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_10_R1")) {
            Version = 11000;
            getVersionBridge = new MCUtils_1_10_R1();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_11_R1") && pluginInstance.getServer().getVersion().endsWith("MC: 1.11)")) {
            Version = 11100;
            getVersionBridge = new MCUtils_1_11_R1();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_11_R1") && pluginInstance.getServer().getVersion().endsWith("MC: 1.11.2)")) {
            Version = 11120;
            getVersionBridge = new MCUtils_1_11_R1();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_12_R1")) {
            Version = 11200;
            getVersionBridge = new MCUtils_1_12_R1();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else if (pluginInstance.getServer().getClass().getPackage().getName().endsWith("v1_13_R2")) {
            Version = 11310;
            getVersionBridge = new MCUtils_1_13_R2();
            getMessageManager.consoleMessage("console_messages.plugin_version",
                    pluginInstance.getServer().getVersion().substring(pluginInstance.getServer().getVersion().indexOf('(')));
        } else {
            getMessageManager.consoleMessage("console_messages.plugin_unknownversion", pluginInstance.getServer().getVersion() + " [" + pluginInstance.getServer().getClass().getPackage().getName() + "]", Level.WARNING);
            return false;
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean startProcesses() {
        setupEconomy();
        setupPermissions();

        // Kick off the database
        getDatabaseManager = new Database_Manager(this);
        if (!getDatabaseManager.startDatabase())
            return false;

        getPlayerManager.MonitorPlayers();
        getPlayerManager.SaveMonitor();

        //Populate the commands
        getCommandManager.registerCommandClass(Commands_UserCommands.class);
        getCommandManager.registerCommandClass(Commands_Admin.class);
        getCommandManager.registerCommandClass(Commands_NPCSettings_.class);
        getCommandManager.registerCommandClass(Commands_GlobalSettings.class);
        getCommandManager.registerCommandClass(Commands_World.class);
        getCommandManager.registerCommandClass(Commands_GroupConfig.class);
        getCommandManager.registerCommandClass(Commands_NPCConfig.class);
        getCommandManager.registerCommandClass(Commands_BountyConfig.class);
        getCommandManager.registerCommandClass(Commands_TimesConfig.class);
        getCommandManager.registerCommandClass(Commands_InventoryConfig.class);
        getCommandManager.registerCommandClass(Commands_JailConfig.class);
        getCommandManager.registerCommandClass(Commands_PlayerNotices.class);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                this.pluginInstance, new BukkitRunnable() {
                    @Override
                    public void run() {
                        getJailManager.loadJailSettings();
                    }
                }
        );

        return true;
    }

    public void startBungeeChecks() {

    }

    public void stopPlugin() {
        //Save all the players
        this.getJailManager.saveJailSettings();
        this.getPlayerManager.SaveMonitor();
        this.getDatabaseManager.stopDatabase();

    }

    public void getDefaultConfigs() {
        // Create the default folders
        if (!languagePath.exists())
            languagePath.mkdirs();
        if (!loggingPath.exists())
            loggingPath.mkdirs();

        // Validate that the default package is in the MountPackages folder. If
        // not, create it.
        if (!(new File(pluginInstance.getDataFolder(), "config.yml").exists()))
            exportConfig(pluginInstance.getDataFolder(), "config.yml");
        exportConfig(languagePath, "en_def-npcpolice.yml");

        this.getDefaultConfig = getUtilities.loadConfiguration(new File(pluginInstance.getDataFolder(), "config.yml"));
    }

    private void exportConfig(File path, String filename) {
        this.getMessageManager.debugMessage(Level.FINEST, "nuNPCPolice_Plugin.exportConfig()|");
        File fileConfig = new File(path, filename);
        if (!fileConfig.isDirectory()) {
            // Reader defConfigStream = null;
            try {
                FileUtils.copyURLToFile((URL) getClass().getResource("/" + filename), fileConfig);
                // defConfigStream = new
                // InputStreamReader(this.getResource(filename), "UTF8");
            } catch (IOException e1) {
                this.getMessageManager.debugMessage(
                        Level.SEVERE, "nuNPCPolice_Plugin.exportConfig()|FailedToExtractFile(" + filename + ")"
                );
                getMessageManager.logToConsole(" Failed to extract default file (" + filename + ")", Level.WARNING);
                return;
            }
        }
    }

    private boolean setupEconomy() {
        if (pluginInstance.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = pluginInstance.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        getEconomyManager = rsp.getProvider();
        return getEconomyManager != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = pluginInstance.getServer().getServicesManager()
                .getRegistration(Permission.class);
        this.getPermissionManager = rsp.getProvider();
        if (this.getPermissionManager.getName().equalsIgnoreCase("superperms")) {
            //Groups are not supported -- plugin_invalidpermplugin
            getMessageManager.consoleMessage("console_messages.plugin_invalidpermplugin", Level.WARNING);
            this.getPermissionManager = null;
        }
        return this.getPermissionManager != null;
    }

    public Boolean hasPermissions(CommandSender player, String permission) {
        if (player instanceof Player) {
            if (player.isOp())
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.stats.") && player.hasPermission("npcpolice.stats.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.fines.") && player.hasPermission("npcpolice.fines.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.config.") && player.hasPermission("npcpolice.config.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.config.npc.") && player.hasPermission("npcpolice.config.npc.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.config.jails.") && player.hasPermission("npcpolice.config.jails.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.settings.") && player.hasPermission("npcpolice.settings.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.settings.groups.") && player.hasPermission("npcpolice.settings.groups.*"))
                return true;

            if (permission.toLowerCase().startsWith("npcpolice.settings.npc.") && player.hasPermission("npcpolice.settings.npc.*"))
                return true;

            return player.hasPermission(permission);
        }
        return true;
    }
}