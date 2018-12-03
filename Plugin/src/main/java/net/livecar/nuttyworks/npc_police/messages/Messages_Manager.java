package net.livecar.nuttyworks.npc_police.messages;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.COMMAND_LISTS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.STATE_SETTING;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_REASONS;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class Messages_Manager {
    private List<LogDetail> logHistory;
    private jsonChat jsonManager;
    private NPC_Police getStorageReference = null;

    public Messages_Manager(NPC_Police policeRef) {
        getStorageReference = policeRef;
        jsonManager = new jsonChat(policeRef);
    }

    public void sendJsonRaw(Player player, String message) {
        this.jsonManager.sendJsonMessage(player, message);
    }

    public void consoleMessage(String msgKey) {
        consoleMessage(msgKey, "", Level.INFO);
    }

    public void consoleMessage(String msgKey, Level logLevel) {
        consoleMessage(msgKey, "", logLevel);
    }

    public void consoleMessage(String msgKey, String extendedMessage) {
        consoleMessage(msgKey, extendedMessage, Level.INFO);
    }

    public void consoleMessage(String msgKey, String extendedMessage, Level logLevel) {
        for (String message : buildMessage(msgKey.toLowerCase(), extendedMessage))
            logToConsole(message, logLevel);
    }

    public void logToConsole(String logLine) {
        getStorageReference.pluginInstance.getLogger().log(Level.INFO, logLine);
    }

    public void logToConsole(String logLine, Level logLevel) {
        getStorageReference.pluginInstance.getLogger().log(logLevel, logLine);
    }

    public void debugMessage(Level debugLevel, String extendedMessage) {
        if (logHistory == null)
            logHistory = new ArrayList<LogDetail>();

        if (getStorageReference.debugLogLevel.intValue() <= debugLevel.intValue()) {
            logHistory.add(new LogDetail("[" + debugLevel.toString() + "] " + extendedMessage));

            if (getStorageReference.pluginInstance.isEnabled()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        getStorageReference.pluginInstance, new Runnable() {
                            public void run() {
                                saveDebugMessages();
                            }
                        }, 500
                );
            } else {
                saveDebugMessages();
            }
        }
    }

    private void saveDebugMessages() {
        if (logHistory != null && logHistory.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'.log'");
            try (FileWriter fileOut = new FileWriter(new File(getStorageReference.loggingPath, dateFormat.format(logHistory.get(0).logDateTime)), true)) {
                for (LogDetail logLine : logHistory) {
                    SimpleDateFormat lnDateFormat = new SimpleDateFormat("hh:mm:ss");

                    fileOut.write(lnDateFormat.format(logLine.logDateTime) + "|" + logLine.logContent + "\r\n");
                }
                logHistory.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] buildMessage(String msgKey, String extendedMessage) {
        String[] messages = this.getResultMessage(msgKey.toLowerCase());
        if (messages == null || messages.length == 0) {
            this.logToConsole("Unable to locate [" + msgKey + "]");
            return new String[0];
        }

        for (int nCnt = 0; nCnt < messages.length; nCnt++) {
            messages[nCnt] = messages[nCnt].replaceAll("<message>", extendedMessage);
        }
        return messages;
    }

    public String[] buildMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Arrest_Record playerRecord, Arrest_Record targetRecord, Jail_Setting jailSetting, World_Setting worldSetting, NPC npc, Wanted_Information wantedInfo, int ident) {
        List<String> processedMessages = new ArrayList<String>();

        if (worldSetting != null && msgKey.equalsIgnoreCase("world_settings.settings_menu")) {
            String[] headerMsgs;
            if (worldSetting.getWorldName().equalsIgnoreCase("_GlobalSettings"))
                headerMsgs = this.getResultMessage("world_settings.global_settings");
            else
                headerMsgs = this.getResultMessage("world_settings.world_information");

            for (int nCnt = 0; nCnt < headerMsgs.length; nCnt++) {
                processedMessages.add(parseMessage(sender, headerMsgs[nCnt], npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedInfo, ident));
            }
        }

        String[] messages = this.getResultMessage(msgKey.toLowerCase());
        if (messages == null || messages.length == 0) {
            this.logToConsole("Unable to locate [" + msgKey + "]");
            return new String[0];
        }

        for (int nCnt = 0; nCnt < messages.length; nCnt++) {
            processedMessages.add(parseMessage(sender, messages[nCnt], npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedInfo, ident));
        }
        return processedMessages.toArray(new String[processedMessages.size()]);
    }

    public void sendMessage(CommandSender sender, String msgKey) {
        sendMessage(sender, buildMessage(sender, msgKey, null, null, null, null, null, null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, null, null, null, null, npcTrait.getNPC(), null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, int ident) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, null, null, null, null, npcTrait.getNPC(), null, ident));
    }

    public void sendMessage(CommandSender sender, String msgKey, Jail_Setting jailSetting) {
        sendMessage(sender, buildMessage(sender, msgKey, null, null, null, jailSetting, null, null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, World_Setting worldSetting) {
        sendMessage(sender, buildMessage(sender, msgKey, null, null, null, null, worldSetting, null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Jail_Setting jailSetting) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, null, null, jailSetting, null, npcTrait.getNPC(), null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, Arrest_Record playerRecord) {
        sendMessage(sender, buildMessage(sender, msgKey, null, playerRecord, null, playerRecord != null && playerRecord.currentJail != null ? playerRecord.currentJail : null, null, null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPC npc, NPCPolice_Trait npcTrait, Arrest_Record playerRecord) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, playerRecord, null, playerRecord != null && playerRecord.currentJail != null ? playerRecord.currentJail : null, null, npc, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Arrest_Record playerRecord) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, playerRecord, null, playerRecord != null && playerRecord.currentJail != null ? playerRecord.currentJail : null, null, npcTrait != null ? npcTrait.getNPC() : null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Arrest_Record playerRecord, Arrest_Record targetRecord) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, playerRecord, targetRecord, playerRecord != null && playerRecord.currentJail != null ? playerRecord.currentJail : null, null, npcTrait != null ? npcTrait.getNPC() : null, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPC npc, Arrest_Record playerRecord) {
        sendMessage(sender, buildMessage(sender, msgKey, null, playerRecord, null, playerRecord != null && playerRecord.currentJail != null ? playerRecord.currentJail : null, null, npc, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Arrest_Record playerRecord, Arrest_Record targetRecord, Jail_Setting jailSetting) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, playerRecord, targetRecord, jailSetting, null, npcTrait.getNPC(), null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, String message) {
        sendMessage(sender, buildMessage(msgKey, message));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPC npc) {
        sendMessage(sender, buildMessage(sender, msgKey, null, null, null, null, null, npc, null, 0));
    }

    public void sendMessage(CommandSender sender, String msgKey, NPCPolice_Trait npcTrait, Wanted_Information wantedInfo) {
        sendMessage(sender, buildMessage(sender, msgKey, npcTrait, null, null, null, null, npcTrait.getNPC(), wantedInfo, 0));
    }

    private void sendMessage(CommandSender sender, String[] messages) {
        if (sender instanceof Player) {
            String sjsonMessage = "";
            for (String sMsg : messages) {
                if (sMsg.startsWith("[") && sjsonMessage.length() > 0) {
                    jsonManager.sendJsonMessage((Player) sender, sjsonMessage);
                    sjsonMessage = "";
                }
                sjsonMessage += sMsg;

                if (sjsonMessage.endsWith("]")) {
                    if (sjsonMessage.endsWith(",]"))
                        sjsonMessage = sjsonMessage.substring(0, sjsonMessage.length() - 2) + "]";
                    if (!sjsonMessage.equalsIgnoreCase("[]")) {
                        jsonManager.sendJsonMessage((Player) sender, sjsonMessage);
                        sjsonMessage = "";
                    } else {
                        sjsonMessage = "";
                    }
                }
            }
        } else {
            // sender.sendMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
            // message)));
        }
    }

    public String parseMessage(CommandSender sender, String message, NPCPolice_Trait npcTrait, Arrest_Record playerRecord, Arrest_Record targetRecord, Jail_Setting jailSetting, World_Setting worldSetting, NPC npc, Wanted_Information wantedInfo, int ident) {
        if (message == null)
            return "";

        if (message.toLowerCase().contains("<plugin.language>"))
            message = message.replaceAll("<plugin\\.language>", getStorageReference.getDefaultConfig.getString("language", "en-def"));

        if (message.toLowerCase().contains("<commands.list>"))
            message = message.replaceAll("<commands\\.list>", Arrays.toString(COMMAND_LISTS.values()));

        if (npcTrait != null) {
            // Citizens settings
            if (message.toLowerCase().contains("<trait.hasmenu>"))
                message = message.replaceAll("<trait\\.hasmenu>", !npcTrait.hasMenu ? "X\",\"color\":\"red" : "✔\",\"color\":\"white");
            if (message.toLowerCase().contains("<trait.isguard>"))
                message = message.replaceAll("<trait\\.isguard>", !npcTrait.isGuard ? "X\",\"color\":\"red" : "✔\",\"color\":\"white");
            if (message.toLowerCase().contains("<trait.monitorpvp>"))
                message = message.replaceAll("<trait\\.monitorpvp>", npcTrait.monitorPVP < 0 ? this.getResultMessage("result_Messages.notset")[0] + "\",\"color\":\"green" : (npcTrait.monitorPVP == 0 ? "X\",\"color\":\"red" : "✔\",\"color\":\"white"));
            if (message.toLowerCase().contains("<trait.maxguarddistance>"))
                message = message.replaceAll("<trait\\.maxguarddistance>", npcTrait.maxDistance_Guard < 0 ? this.getResultMessage("result_Messages.notset")[0] : Integer.toString(npcTrait.maxDistance_Guard));
            if (message.toLowerCase().contains("<trait.losattack>"))
                message = message.replaceAll("<trait\\.losattack>", npcTrait.lineOfSightAttack < 0 ? this.getResultMessage("result_Messages.notset")[0] + "\",\"color\":\"green" : (npcTrait.lineOfSightAttack == 0 ? "X\",\"color\":\"red" : "✔\",\"color\":\"white"));
            if (message.toLowerCase().contains("<trait.wantedsetting>"))
                message = message.replaceAll("<trait\\.wantedsetting>", this.getResultMessage("result_Messages.wantedlevel_" + npcTrait.wantedSetting.toString())[0]);

            if (message.toLowerCase().contains("<trait.bounty.damageicon>"))
                message = message.replaceAll("<trait\\.bounty\\.damageicon>", npcTrait.bounty_assault < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<trait.bounty.murdericon>"))
                message = message.replaceAll("<trait\\.bounty\\.murdericon>", npcTrait.bounty_murder < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);

            if (message.toLowerCase().contains("<trait.time.damageicon>"))
                message = message.replaceAll("<trait\\.time\\.damageicon>", npcTrait.time_assault < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<trait.time.murdericon>"))
                message = message.replaceAll("<trait\\.time\\.murdericon>", npcTrait.time_murder < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);

            if (message.toLowerCase().contains("<trait.bounty.damage>"))
                message = message.replaceAll("<trait\\.bounty\\.damage>", String.valueOf(npcTrait.bounty_assault));
            if (message.toLowerCase().contains("<trait.bounty.murder>"))
                message = message.replaceAll("<trait\\.bounty\\.murder>", String.valueOf(npcTrait.bounty_murder));

            if (message.toLowerCase().contains("<trait.time.damage>"))
                message = message.replaceAll("<trait\\.time\\.damage>", String.valueOf(npcTrait.time_assault));
            if (message.toLowerCase().contains("<trait.time.murder>"))
                message = message.replaceAll("<trait\\.time\\.murder>", String.valueOf(npcTrait.time_murder));

        }

        if (npc != null) {
            if (message.toLowerCase().contains("<npc.id>"))
                message = message.replaceAll("<npc\\.id>", Integer.toString(npc.getId()));
            if (message.toLowerCase().contains("<npc.name>"))
                message = message.replaceAll("<npc\\.name>", npc.getName().replace("[", "").replace("]", "]"));
            if (message.toLowerCase().contains("<npc.spawned>"))
                message = message.replaceAll("<npc\\.spawned>", npc.isSpawned() ? this.getResultMessage("result_Messages.true_text")[0] : this.getResultMessage("result_Messages.false_text")[0]);
            if (message.toLowerCase().contains("<npc.location>"))
                message = message.replaceAll("<npc\\.location>", "(" + npc.getEntity().getLocation().getBlockX() + "," + npc.getEntity().getLocation().getBlockY() + "," + npc.getEntity().getLocation().getBlockZ() + ")");
            if (message.toLowerCase().contains("<npc.type>"))
                message = message.replaceAll("<npc\\.type>", npc.getEntity().getType().name());
        }

        if (npc == null) {
            if (message.toLowerCase().contains("<npc.name>")) {
                if (playerRecord != null && playerRecord.getClosestJail() != null) {
                    message = message.replaceAll("<npc\\.name>", playerRecord.getClosestJail().displayName);
                } else {
                    message = message.replaceAll("<npc\\.name>", "Jailer");
                }
            }

        }
        if (playerRecord != null) {
            if (message.toLowerCase().contains("<player.name>"))
                message = message.replaceAll("<player\\.name>", playerRecord.getPlayer().getName());
            if (message.toLowerCase().contains("<player.bounty>"))
                message = message.replaceAll("<player\\.bounty>", String.valueOf(playerRecord.getBountyInt()));
            if (message.toLowerCase().contains("<player.totalbounty>"))
                message = message.replaceAll("<player\\.totalbounty>", String.valueOf(playerRecord.getTotalBountyInt()));
            if (message.toLowerCase().contains("<player.currentjail>"))
                message = message.replaceAll("<player\\.currentjail>", playerRecord.currentJail != null ? playerRecord.currentJail.displayName : this.getResultMessage("result_messages.no_current_jail")[0]);
            if (message.toLowerCase().contains("<player.currentstatus>")) {
                if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED) {
                    if (playerRecord.getTime() > 0) {
                        message = message.replaceAll("<player\\.currentstatus>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]) + " + " + String.valueOf(playerRecord.getTime()) + "s ";
                    } else {
                        message = message.replaceAll("<player\\.currentstatus>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]);
                    }
                } else {
                    message = message.replaceAll("<player\\.currentstatus>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]);
                }
            }
            if (message.toLowerCase().contains("<player.currentstatusuntil>")) {
                if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED) {
                    if (playerRecord.getJailedExpires().after(new Date())) {
                        message = message.replaceAll("<player\\.currentstatusuntil>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0] + " (" + this.getResultMessage("result_messages.until")[0] + " " + (new SimpleDateFormat("MMM dd HH:mm").format(playerRecord.getJailedExpires())).toString() + ")");
                    } else if (playerRecord.getTime() > 0) {
                        message = message.replaceAll("<player\\.currentstatusuntil>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]) + " + " + String.valueOf(playerRecord.getTime()) + "s";
                    } else {
                        message = message.replaceAll("<player\\.currentstatusuntil>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]);
                    }
                } else {
                    message = message.replaceAll("<player\\.currentstatusuntil>", this.getResultMessage("result_messages." + playerRecord.getCurrentStatus().toString().toLowerCase())[0]);
                }
            }
            if (message.toLowerCase().contains("<player.priorstatus>"))
                message = message.replaceAll("<player\\.priorstatus>", this.getResultMessage("result_messages." + playerRecord.getPriorStatus().toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<player.lastattack>"))
                message = message.replaceAll("<player\\.lastattack>", (playerRecord.lastAttack != null && !playerRecord.lastAttack.trim().equals("")) ? playerRecord.lastAttack : this.getResultMessage("result_messages.no_prior")[0]);
            if (message.toLowerCase().contains("<player.wantedlvl>"))
                message = message.replaceAll("<player\\.wantedlvl>", this.getResultMessage("result_Messages.wantedlevel_" + playerRecord.getWantedLevel().toString().toLowerCase())[0]);

            if (message.toLowerCase().contains("<player.murdercount>"))
                message = message.replaceAll("<player\\.murdercount>", Integer.toString(playerRecord.getStat("MURDER")));
            if (message.toLowerCase().contains("<player.arrestcount>"))
                message = message.replaceAll("<player\\.arrestcount>", Integer.toString(playerRecord.getStat("ARRESTED")));
            if (message.toLowerCase().contains("<player.currentlevel>"))
                message = message.replaceAll("<player\\.currentlevel>", playerRecord.getWantedLevel().toString().toLowerCase());
            if (message.toLowerCase().contains("<player.escapecount>"))
                message = message.replaceAll("<player\\.escapecount>", Integer.toString(playerRecord.getStat("ESCAPED")));
            if (message.toLowerCase().contains("<player.jailtime>")) {
                Long secondsLeft = playerRecord.getPlayerJailTime();
                if (secondsLeft == Long.MAX_VALUE)
                    message = message.replaceAll("<player\\.jailtime>", getStorageReference.getMessageManager.getResultMessage("result_Messages.endless")[0]);
                else
                    message = message.replaceAll("<player\\.jailtime>", getStorageReference.getUtilities.secondsToTime(secondsLeft));
            }
            if (message.toLowerCase().contains("<player.lastwarning>")) {
                if (playerRecord.getLastWarning().getTime() < 1451606400000L)
                    message = message.replaceAll("<player\\.lastwarning>", "");
                else
                    message = message.replaceAll("<player\\.lastwarning>", (new SimpleDateFormat("MMM dd HH:mm").format(playerRecord.getLastWarning())).toString());
            }
            if (message.toLowerCase().contains("<player.lastarrest>")) {
                if (playerRecord.getLastArrest().getTime() < 1451606400000L)
                    message = message.replaceAll("<player\\.lastarrest>", "");
                else
                    message = message.replaceAll("<player\\.lastarrest>", (new SimpleDateFormat("MMM dd HH:mm").format(playerRecord.getLastArrest())).toString());
            }
            if (message.toLowerCase().contains("<player.lastescape>")) {
                if (playerRecord.getLastArrest().getTime() < 1451606400000L)
                    message = message.replaceAll("<player\\.lastescape>", "");
                else
                    message = message.replaceAll("<player\\.lastescape>", (new SimpleDateFormat("MMM dd HH:mm").format(playerRecord.getLastEscape())).toString());
            }
            if (message.toLowerCase().contains("<player.secondsleft>")) {
                if (playerRecord.getTime() < 1)
                    message = message.replaceAll("<player\\.secondsleft>", "0");
                else
                    message = message.replaceAll("<player\\.secondsleft>", String.valueOf(playerRecord.getTime()));
            }
            if (message.toLowerCase().contains("<player.jaileduntil>")) {
                if (playerRecord.getJailedExpires().before(new Date()))
                    message = message.replaceAll("<player\\.jaileduntil>", "");
                else
                    message = message.replaceAll("<player\\.jaileduntil>", (new SimpleDateFormat("MMM dd HH:mm").format(playerRecord.getJailedExpires())).toString());
            }
            if (message.toLowerCase().contains("<player.pendingmurders>")) {
                List<Wanted_Information> wantedList = playerRecord.getWantedReasons(WANTED_REASONS.MURDER);
                StringBuilder murderList = new StringBuilder();
                for (Wanted_Information wantedEntry : wantedList) {
                    murderList.append(this.buildMessage(sender, "player_stats.pending_detail", npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedEntry, ident)[0]);
                }
                message = message.replaceAll("<player\\.pendingmurders>", murderList.toString());
            }
            if (message.toLowerCase().contains("<player.pendingassults>")) {
                List<Wanted_Information> wantedList = playerRecord.getWantedReasons(WANTED_REASONS.ASSAULT);
                StringBuilder assaultList = new StringBuilder();
                for (Wanted_Information wantedEntry : wantedList) {
                    assaultList.append(this.buildMessage(sender, "player_stats.pending_detail", npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedEntry, ident)[0]);
                }
                message = message.replaceAll("<player\\.pendingassults>", assaultList.toString());
            }
        }

        if (wantedInfo != null) {
            if (message.toLowerCase().contains("<wantedinfo.targetnamex>"))
                message = message.replaceAll("<wantedinfo\\.targetnamex>", wantedInfo.getAttackedName() + (wantedInfo.getOffenseCount() > 1 ? "*" + String.valueOf(wantedInfo.getOffenseCount()) : ""));
            if (message.toLowerCase().contains("<wantedinfo.offensecount>"))
                message = message.replaceAll("<wantedinfo\\.offensecount>", String.valueOf(wantedInfo.getOffenseCount()));
            if (message.toLowerCase().contains("<wantedinfo.targetname>"))
                message = message.replaceAll("<wantedinfo\\.targetname>", wantedInfo.getAttackedName());
            if (message.toLowerCase().contains("<wantedinfo.witnessname>"))
                message = message.replaceAll("<wantedinfo\\.witnessname>", wantedInfo.getWitnessName());
            if (message.toLowerCase().contains("<wantedinfo.bounty>"))
                message = message.replaceAll("<wantedinfo\\.bounty>", String.valueOf(wantedInfo.getBountyValue()));
            if (message.toLowerCase().contains("<wantedinfo.offense>"))
                message = message.replaceAll("<wantedinfo\\.offense>", String.valueOf(wantedInfo.getWantedReason().toString()));

            if (message.toLowerCase().contains("<wantedinfo.offensedate>"))
                message = message.replaceAll("<wantedinfo\\.offensedate>", (new SimpleDateFormat("MMM dd HH:mm").format(wantedInfo.getFirstOffenseDate())).toString());

        }

        if (targetRecord != null) {
            if (message.toLowerCase().contains("<targetrecord.name>"))
                message = message.replaceAll("<targetrecord\\.name>", targetRecord.getPlayer().getName());
            if (message.toLowerCase().contains("<targetrecord.bounty>"))
                message = message.replaceAll("<targetrecord\\.bounty>", String.valueOf(targetRecord.getBountyInt()));
            if (message.toLowerCase().contains("<targetrecord.totalbounty>"))
                message = message.replaceAll("<targetrecord\\.totalbounty>", String.valueOf(targetRecord.getTotalBountyInt()));
            if (message.toLowerCase().contains("<targetrecord.currentjail>"))
                message = message.replaceAll("<targetrecord\\.currentjail>", targetRecord.currentJail != null ? targetRecord.currentJail.displayName : this.getResultMessage("result_messages.no_current_jail")[0]);
            if (message.toLowerCase().contains("<targetrecord.currentstatus>"))
                message = message.replaceAll("<targetrecord\\.currentstatus>", this.getResultMessage("result_messages." + targetRecord.getCurrentStatus().toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<targetrecord.priorstatus>"))
                message = message.replaceAll("<targetrecord\\.priorstatus>", this.getResultMessage("result_messages." + targetRecord.getPriorStatus().toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<targetrecord.lastattack>"))
                message = message.replaceAll("<targetrecord\\.lastattack>", (targetRecord.lastAttack != null && !targetRecord.lastAttack.trim().equals("")) ? targetRecord.lastAttack : this.getResultMessage("result_messages.no_prior")[0]);

            if (message.toLowerCase().contains("<targetrecord.murdercount>"))
                message = message.replaceAll("<targetrecord\\.murdercount>", Integer.toString(targetRecord.getStat("MURDER")));
            if (message.toLowerCase().contains("<targetrecord.arrestcount>"))
                message = message.replaceAll("<targetrecord\\.arrestcount>", Integer.toString(targetRecord.getStat("ARRESTED")));
            if (message.toLowerCase().contains("<targetrecord.escapecount>"))
                message = message.replaceAll("<targetrecord\\.escapecount>", Integer.toString(targetRecord.getStat("ESCAPED")));
            if (message.toLowerCase().contains("<targetrecord.lastwarning>")) {
                if (targetRecord.getLastWarning().getTime() < 1451606400000L)
                    message = message.replaceAll("<targetrecord\\.lastwarning>", "");
                else
                    message = message.replaceAll("<targetrecord\\.lastwarning>", (new SimpleDateFormat("MMM dd HH:mm").format(targetRecord.getLastWarning())).toString());
            }
            if (message.toLowerCase().contains("<targetrecord.lastarrest>")) {
                if (targetRecord.getLastWarning().getTime() < 1451606400000L)
                    message = message.replaceAll("<targetrecord\\.lastarrest>", "");
                else
                    message = message.replaceAll("<targetrecord\\.lastarrest>", (new SimpleDateFormat("MMM dd HH:mm").format(targetRecord.getLastArrest())).toString());
            }
            if (message.toLowerCase().contains("<targetrecord.lastescape>")) {
                if (targetRecord.getLastWarning().getTime() < 1451606400000L)
                    message = message.replaceAll("<targetrecord\\.lastescape>", "");
                else
                    message = message.replaceAll("<targetrecord\\.lastescape>", (new SimpleDateFormat("MMM dd HH:mm").format(targetRecord.getLastEscape())).toString());
            }
            if (message.toLowerCase().contains("<targetrecord.pendingmurders>")) {
                List<Wanted_Information> wantedList = targetRecord.getWantedReasons(WANTED_REASONS.MURDER);
                StringBuilder murderList = new StringBuilder();
                for (Wanted_Information wantedEntry : wantedList) {
                    murderList.append(this.buildMessage(sender, "player_stats.pending_detail", npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedEntry, ident)[0]);
                }
                message = message.replaceAll("<targetrecord\\.pendingmurders>", murderList.toString());
            }
            if (message.toLowerCase().contains("<targetrecord.pendingassults>")) {
                List<Wanted_Information> wantedList = targetRecord.getWantedReasons(WANTED_REASONS.ASSAULT);
                StringBuilder assaultList = new StringBuilder();
                for (Wanted_Information wantedEntry : wantedList) {
                    assaultList.append(this.buildMessage(sender, "player_stats.pending_detail", npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedEntry, ident)[0]);
                }
                message = message.replaceAll("<targetrecord\\.pendingassults>", assaultList.toString());
            }
        }

        if (jailSetting != null) {
            // V1.44 -- Commands
            if (message.toLowerCase().contains("<currentjail.distance>")) {
                if (sender instanceof Player) {
                    Location loc = ((Player)sender).getLocation();
                    Location[] regionBounds = getStorageReference.getWorldGuardPlugin.getRegionBounds(loc.getWorld(),jailSetting.regionName);
                    if (regionBounds.length == 0)
                    {
                        message = message.replaceAll("<currentjail\\.distance>", "N/A");
                    } else {
                        Double distToRegion = loc.distance(new Location(loc.getWorld(), regionBounds[0].getX() + ((regionBounds[1].getX() - regionBounds[0].getX()) / 2), regionBounds[0].getY() + ((regionBounds[1].getY()-regionBounds[0].getY()) / 2), regionBounds[0].getZ() + ((regionBounds[1].getZ()-regionBounds[0].getZ()) / 2)));
                        message = message.replaceAll("<currentjail\\.distance>", String.valueOf(distToRegion.intValue()));
                    }
                } else {
                    message = message.replaceAll("<currentjail\\.distance>", "N/A");
                }
            }
            if (message.toLowerCase().contains("<currentjail.displayname>"))
                message = message.replaceAll("<currentjail\\.displayname>", String.valueOf(jailSetting.displayName));
            if (message.toLowerCase().contains("<currentjail.name>"))
                message = message.replaceAll("<currentjail\\.name>", String.valueOf(jailSetting.jailName));
            if (message.toLowerCase().contains("<currentjail.namepad>"))
                message = message.replaceAll("<currentjail\\.namepad>", StringUtils.rightPad(jailSetting.jailName, 10));
            if (message.toLowerCase().contains("<currentjail.region>"))
                message = message.replaceAll("<currentjail\\.region>", String.valueOf(jailSetting.regionName));
            if (message.toLowerCase().contains("<currentjail.regionpad>"))
                message = message.replaceAll("<currentjail\\.regionpad>", StringUtils.rightPad((jailSetting.regionName.length() > 15 ? jailSetting.regionName.substring(0, 12) : jailSetting.regionName), 12));
            if (message.toLowerCase().contains("<currentjail.setting.chestlocation>"))
                message = message.replaceAll("<currentjail\\.setting\\.chestlocation>", jailSetting.lockedInventoryLocation == null ? this.getResultMessage("result_Messages.nolocationset")[0] : this.getStorageReference.getUtilities.locationToString(jailSetting.lockedInventoryLocation));
            if (message.toLowerCase().contains("<currentjail.spawns.exitpoint>"))
                message = message.replaceAll("<currentjail\\.spawns\\.exitpoint>", jailSetting.freeSpawnPoint == null ? this.getResultMessage("result_Messages.nolocationset")[0] : this.getStorageReference.getUtilities.locationToString(jailSetting.freeSpawnPoint));

            if (message.toLowerCase().contains("<currentjail.showcells>")) {
                if (jailSetting.cellLocations.size() == 0) {
                    message = message.replaceAll("<currentjail\\.showcells>", ",{\"text\":\"\"}");
                } else {
                    StringBuilder locationString = new StringBuilder();

                    for (int nCnt = 0; nCnt < jailSetting.cellLocations.size(); nCnt++) {
                        locationString.append(this.buildMessage(sender, "jail_settings.jail_info_cellformat", npcTrait, playerRecord, targetRecord, jailSetting, worldSetting, npc, wantedInfo, 0)[0].replaceAll("<currentjail\\.cellalpha>", String.valueOf(Character.toChars(65 + nCnt))).replaceAll("<currentjail\\.cellid>", String.valueOf(nCnt)));
                    }

                    message = message.replaceAll("<currentjail\\.showcells>", locationString.toString().trim());
                }
            }

            if (message.toLowerCase().contains("<currentjail.setting.bountyescaped>"))
                message = message.replaceAll("<currentjail\\.setting\\.bountyescaped>", jailSetting.bounty_Escaped < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.bounty_Escaped));
            if (message.toLowerCase().contains("<currentjail.setting.bountypvp>"))
                message = message.replaceAll("<currentjail\\.setting\\.bountypvp>", jailSetting.bounty_PVP < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.bounty_PVP));
            if (message.toLowerCase().contains("<currentjail.setting.timesjailed>"))
                message = message.replaceAll("<currentjail\\.setting\\.timesjailed>", jailSetting.times_Jailed == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.times_Jailed));
            if (message.toLowerCase().contains("<currentjail.setting.timescellday>"))
                message = message.replaceAll("<currentjail\\.setting\\.timescellday>", jailSetting.times_CellDay == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.times_CellDay));
            if (message.toLowerCase().contains("<currentjail.setting.timescellnight>"))
                message = message.replaceAll("<currentjail\\.setting\\.timescellnight>", jailSetting.times_CellNight == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.times_CellNight));
            if (message.toLowerCase().contains("<currentjail.setting.escapeddistance>"))
                message = message.replaceAll("<currentjail\\.setting\\.escapeddistance>", jailSetting.escaped_Distance < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.escaped_Distance));
            if (message.toLowerCase().contains("<currentjail.setting.escapeddelay>"))
                message = message.replaceAll("<currentjail\\.setting\\.escapeddelay>", jailSetting.escaped_Delay < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(jailSetting.escaped_Delay));

            if (message.toLowerCase().contains("<currentjail.wanted.minlevel>"))
                message = message.replaceAll("<currentjail\\.wanted\\.minlevel>", this.getResultMessage("result_Messages.wantedlevel_" + jailSetting.minWanted.toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<currentjail.wanted.maxlevel>"))
                message = message.replaceAll("<currentjail\\.wanted\\.maxlevel>", this.getResultMessage("result_Messages.wantedlevel_" + jailSetting.maxWanted.toString().toLowerCase())[0]);

            if (message.toLowerCase().contains("<currentjail.setting.takeinventoryonarrest>"))
                message = message.replaceAll("<currentjail\\.setting\\.takeinventoryonarrest>", jailSetting.onArrest_InventoryAction == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : jailSetting.onArrest_InventoryAction == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<currentjail.setting.returninventoryonescape>"))
                message = message.replaceAll("<currentjail\\.setting\\.returninventoryonescape>", jailSetting.onEscape_InventoryAction == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : jailSetting.onEscape_InventoryAction == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<currentjail.setting.returninventoryonfree>"))
                message = message.replaceAll("<currentjail\\.setting\\.returninventoryonfree>", jailSetting.onFree_InventoryAction == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : jailSetting.onFree_InventoryAction == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
        }

        if (worldSetting != null) {
            if (message.toLowerCase().contains("<worldsetting.worldname>"))
                message = message.replaceAll("<worldsetting\\.worldname>", String.valueOf(worldSetting.getWorldName()));
            if (message.toLowerCase().contains("<worldsetting.groups.wanted>"))
                message = message.replaceAll("<worldsetting\\.groups\\.wanted>", worldSetting.getWantedGroup().equalsIgnoreCase("") ? this.getResultMessage("result_Messages.nogroup")[0] : worldSetting.getWantedGroup());
            if (message.toLowerCase().contains("<worldsetting.groups.jailed>"))
                message = message.replaceAll("<worldsetting\\.groups\\.jailed>", worldSetting.getJailedGroup().equalsIgnoreCase("") ? this.getResultMessage("result_Messages.nogroup")[0] : worldSetting.getJailedGroup());
            if (message.toLowerCase().contains("<worldsetting.groups.escaped>"))
                message = message.replaceAll("<worldsetting\\.groups\\.escaped>", worldSetting.getEscapedGroup().equalsIgnoreCase("") ? this.getResultMessage("result_Messages.nogroup")[0] : worldSetting.getEscapedGroup());

            // Shorter Notices for cleanup.
            if (message.toLowerCase().contains("<worldsetting.bounty.damageicon>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.damageicon>", worldSetting.getBounty_Damage() < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<worldsetting.bounty.escapedicon>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.escapedicon>", worldSetting.getBounty_Escaped() < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<worldsetting.bounty.murdericon>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.murdericon>", worldSetting.getBounty_Murder() < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<worldsetting.bounty.pvpicon>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.pvpicon>", worldSetting.getBounty_PVP() < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);
            if (message.toLowerCase().contains("<worldsetting.bounty.maximumicon>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.maximumicon>", worldSetting.getBounty_Maximum() < 0 ? this.getResultMessage("result_Messages.notset")[0] : this.getResultMessage("result_Messages.numeric")[0]);

            // Values
            if (message.toLowerCase().contains("<worldsetting.bounty.damage>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.damage>", worldSetting.getBounty_Damage() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getBounty_Damage()));
            if (message.toLowerCase().contains("<worldsetting.bounty.escaped>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.escaped>", worldSetting.getBounty_Escaped() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getBounty_Escaped()));
            if (message.toLowerCase().contains("<worldsetting.bounty.murder>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.murder>", worldSetting.getBounty_Murder() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getBounty_Murder()));
            if (message.toLowerCase().contains("<worldsetting.bounty.pvp>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.pvp>", worldSetting.getBounty_PVP() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getBounty_PVP()));
            if (message.toLowerCase().contains("<worldsetting.bounty.maximum>"))
                message = message.replaceAll("<worldsetting\\.bounty\\.maximum>", worldSetting.getBounty_Maximum() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getBounty_Maximum()));

            if (message.toLowerCase().contains("<worldsetting.times.jailed>"))
                message = message.replaceAll("<worldsetting\\.times\\.jailed>", worldSetting.getTimeInterval_Jailed() == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTimeInterval_Jailed()));
            if (message.toLowerCase().contains("<worldsetting.times.escaped>"))
                message = message.replaceAll("<worldsetting\\.times\\.escaped>", worldSetting.getTimeInterval_Escaped() == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTimeInterval_Escaped()));
            if (message.toLowerCase().contains("<worldsetting.times.wanted>"))
                message = message.replaceAll("<worldsetting\\.times\\.wanted>", worldSetting.getTimeInterval_Wanted() == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTimeInterval_Wanted()));
            if (message.toLowerCase().contains("<worldsetting.times.cellday>"))
                message = message.replaceAll("<worldsetting\\.times\\.cellday>", worldSetting.getTimeInterval_CellDay() == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTimeInterval_CellDay()));
            if (message.toLowerCase().contains("<worldsetting.times.cellnight>"))
                message = message.replaceAll("<worldsetting\\.times\\.cellnight>", worldSetting.getTimeInterval_CellNight() == Double.MIN_VALUE ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTimeInterval_CellNight()));

            if (message.toLowerCase().contains("<worldsetting.npc.maxdistance>"))
                message = message.replaceAll("<worldsetting\\.npc\\.maxdistance>", worldSetting.getMaximum_GardDistance() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getMaximum_GardDistance()));
            if (message.toLowerCase().contains("<worldsetting.npc.maxdamage>"))
                message = message.replaceAll("<worldsetting\\.npc\\.maxdamage>", worldSetting.getWarning_MaximumDamage() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getWarning_MaximumDamage()));
            if (message.toLowerCase().contains("<worldsetting.npc.minbounty>"))
                message = message.replaceAll("<worldsetting\\.npc\\.minbounty>", worldSetting.getMinumum_WantedBounty() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getMinumum_WantedBounty()));
            if (message.toLowerCase().contains("<worldsetting.npc.pvpmonitor>"))
                message = message.replaceAll("<worldsetting\\.npc\\.pvpmonitor>", worldSetting.getMonitorPVP() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.getMonitorPVP() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<worldsetting.npc.onlyassigned>"))
                message = message.replaceAll("<worldsetting\\.npc\\.onlyassigned>", worldSetting.getProtect_OnlyAssigned() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.getProtect_OnlyAssigned() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<worldsetting.npc.losattack>"))
                message = message.replaceAll("<worldsetting\\.npc\\.losattack>", worldSetting.getLOSAttackSetting() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.getLOSAttackSetting() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");

            if (message.toLowerCase().contains("<worldsetting.kick.type>"))
                message = message.replaceAll("<worldsetting\\.kick\\.type>", this.getResultMessage("result_Messages.kicktype_" + worldSetting.getKickType().toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<worldsetting.kick.location>"))
                message = message.replaceAll("<worldsetting\\.kick\\.location>", worldSetting.getKickLocation().isEmpty() ? this.getResultMessage("result_messages.nolocationset")[0] : worldSetting.getKickLocation());

            if (message.toLowerCase().contains("<worldsetting.wanted.minlevel>"))
                message = message.replaceAll("<worldsetting\\.wanted\\.minlevel>", this.getResultMessage("result_Messages.wantedlevel_" + worldSetting.getMinimum_WantedLevel().toString().toLowerCase())[0]);
            if (message.toLowerCase().contains("<worldsetting.wanted.maxlevel>"))
                message = message.replaceAll("<worldsetting\\.wanted\\.maxlevel>", this.getResultMessage("result_Messages.wantedlevel_" + worldSetting.getMaximum_WantedLevel().toString().toLowerCase())[0]);

            if (message.toLowerCase().contains("<worldsetting.notice.escapeddist>"))
                message = message.replaceAll("<worldsetting\\.notice\\.escapeddist>", worldSetting.getEscaped_Distance() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getEscaped_Distance()));
            if (message.toLowerCase().contains("<worldsetting.notice.escapeddelay>"))
                message = message.replaceAll("<worldsetting\\.notice\\.escapeddelay>", worldSetting.getEscaped_Delay() < 0.0D ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getEscaped_Delay()));
            if (message.toLowerCase().contains("<worldsetting.notice.jaileddist>"))
                message = message.replaceAll("<worldsetting\\.notice\\.jaileddist>", worldSetting.getJailed_Distance() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getJailed_Distance()));
            if (message.toLowerCase().contains("<worldsetting.notice.jaileddelay>"))
                message = message.replaceAll("<worldsetting\\.notice\\.jaileddelay>", worldSetting.getJailed_Delay() < 0.0D ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getJailed_Delay()));
            if (message.toLowerCase().contains("<worldsetting.notice.murderdist>"))
                message = message.replaceAll("<worldsetting\\.notice\\.murderdist>", worldSetting.getMurder_Distance() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getMurder_Distance()));
            if (message.toLowerCase().contains("<worldsetting.notice.murderdelay>"))
                message = message.replaceAll("<worldsetting\\.notice\\.murderdelay>", worldSetting.getMurder_Delay() < 0.0D ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getMurder_Delay()));
            if (message.toLowerCase().contains("<worldsetting.notice.theftdist>"))
                message = message.replaceAll("<worldsetting\\.notice\\.theftdist>", worldSetting.getTheft_Distance() < 0 ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTheft_Distance()));
            if (message.toLowerCase().contains("<worldsetting.notice.theftdelay>"))
                message = message.replaceAll("<worldsetting\\.notice\\.theftdelay>", worldSetting.getTheft_Delay() < 0.0D ? this.getResultMessage("result_Messages.notset")[0] : String.valueOf(worldSetting.getTheft_Delay()));

            if (message.toLowerCase().contains("<worldsetting.inventory.onarrest>"))
                message = message.replaceAll("<worldsetting\\.inventory\\.onarrest>", worldSetting.onArrest_InventoryAction() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.onArrest_InventoryAction() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<worldsetting.inventory.onescape>"))
                message = message.replaceAll("<worldsetting\\.inventory\\.onescape>", worldSetting.onEscape_InventoryAction() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.onEscape_InventoryAction() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");
            if (message.toLowerCase().contains("<worldsetting.inventory.onfree>"))
                message = message.replaceAll("<worldsetting\\.inventory\\.onfree>", worldSetting.onFree_InventoryAction() == STATE_SETTING.NOTSET ? this.getResultMessage("result_Messages.notset")[0] : worldSetting.onFree_InventoryAction() == STATE_SETTING.FALSE ? "X\",\"color\":\"red" : "✔\",\"color\":\"yellow");

        }
        return message;
    }

    public String[] getResultMessage(String msgKey) {
        String language = getStorageReference.currentLanguage;
        msgKey = msgKey.toLowerCase();
        List<String> response = new ArrayList<String>();

        if (!getStorageReference.getLanguageManager.languageStorage.containsKey(language + "-npcpolice")) {
            logToConsole("Missing language file [" + language + "-npcpolice" + "] Check your config files.");
            language = "en_def";
        }

        if (!getStorageReference.getLanguageManager.languageStorage.containsKey(language + "-npcpolice")) {
            logToConsole("Missing language file [" + language + "-npcpolice" + "] Check your config files.");
            response.add("Language file failure. Contact the servers admin");
            return response.toArray(new String[response.size()]);
        }

        if (!getStorageReference.getLanguageManager.languageStorage.get(language + "-npcpolice").contains(msgKey)) {
            language = "en_def";
        }

        if (getStorageReference.getLanguageManager.languageStorage.get(language + "-npcpolice").isList(msgKey)) {
            response.addAll(getStorageReference.getLanguageManager.languageStorage.get(language + "-npcpolice").getStringList(msgKey));
        } else {
            response.add(getStorageReference.getLanguageManager.languageStorage.get(language + "-npcpolice").getString(msgKey));
        }
        return response.toArray(new String[response.size()]);
    }

}

class LogDetail {
    public Date logDateTime;
    public String logContent;

    public LogDetail(String logContent) {
        logDateTime = new Date();
        this.logContent = logContent;
    }
}
