package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Command_Manager {
    HashMap<String, Command_Record> registeredCommands;
    private List<String> commandGroups;
    private NPC_Police getStorageReference;

    public Command_Manager(NPC_Police policeRef) {
        this.getStorageReference = policeRef;
        registeredCommands = new HashMap<>();
        commandGroups = new ArrayList<>();
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, String[] inargs) {
        if (getStorageReference.getWorldGuardPlugin == null) {
            getStorageReference.getMessageManager.sendMessage(sender, "general_messages.invalid_worldguard");
            return true;
        }

        int npcid = -1;
        Jail_Setting selectedJail = null;
        String selectedWorld = null;
        World_Setting worldConfig = null;
        Arrest_Record selectedPlayer = null;

        Player player = null;
        // Commands only avail to players
        if (sender instanceof Player) {
            player = (Player) sender;
            if (inargs.length == 0 && getStorageReference.hasPermissions(player, "npcpolice.stats.mystats")) {
                inargs = new String[]{"mystats"};
            }
        }

        List<String> sList = new ArrayList<>();
        for (int nCnt = 0; nCnt < inargs.length; nCnt++) {
            if (inargs[nCnt].equalsIgnoreCase("--npc")) {
                // Npc ID should be the next one
                if (inargs.length >= nCnt + 2) {
                    npcid = Integer.parseInt(inargs[nCnt + 1]);
                    nCnt++;
                }
            } else if (inargs[nCnt].equalsIgnoreCase("--jail")) {
                selectedJail = getStorageReference.getJailManager.getJailByName(inargs[nCnt + 1]);
                if (selectedJail == null) {
                    getStorageReference.getMessageManager.sendMessage(sender, "general_messages.config_jail_nojail", inargs[nCnt + 1]);
                    return true;
                }
                nCnt++;
            } else if (inargs[nCnt].equalsIgnoreCase("--world")) {
                if (!inargs[nCnt + 1].equalsIgnoreCase("_GlobalSettings")) {
                    World testWorld = getStorageReference.pluginInstance.getServer().getWorld(inargs[nCnt + 1]);
                    if (testWorld == null) {
                        getStorageReference.getMessageManager.sendMessage(sender, "general_messages.config_world_invalid");
                        return true;
                    }
                    selectedWorld = testWorld.getName();
                    worldConfig = getStorageReference.getJailManager.getWorldSettings(testWorld.getName());
                    nCnt++;
                } else {
                    selectedWorld = "_GlobalSettings";
                    worldConfig = getStorageReference.getJailManager.getWorldSettings("_GlobalSettings");
                    nCnt++;
                }
            } else if (inargs[nCnt].equalsIgnoreCase("--player")) {
                OfflinePlayer foundPlayer = null;
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    if (onlinePlayer.getName().equalsIgnoreCase(inargs[nCnt + 1])) {
                        foundPlayer = onlinePlayer;
                        break;
                    }
                }

                if (foundPlayer == null) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(inargs[nCnt + 1]);
                    if (op != null && op.hasPlayedBefore()) {
                        // Request to load the player, wait a few and re-issue this
                        // command.
                        getStorageReference.getDatabaseManager.queueLoadPlayerRequest(op.getUniqueId());
                        final CommandSender tmpSender = sender;
                        final String[] tmpArgs = inargs;

                        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(
                                getStorageReference.pluginInstance, () -> onCommand(tmpSender, tmpArgs), 2L
                        );
                        return true;
                    } else {
                        getStorageReference.getMessageManager.sendMessage(sender, "general_messages.config_command_playerselect", inargs[nCnt + 1]);
                        return true;
                    }

                } else {
                    selectedPlayer = getStorageReference.getPlayerManager.getPlayer(foundPlayer.getUniqueId());
                }

                if (selectedPlayer == null) {
                    getStorageReference.getMessageManager.sendMessage(sender, "general_messages.config_world_invalid");
                    return true;
                }
                nCnt++;
            } else {
                sList.add(inargs[nCnt]);
            }
        }

        if (selectedWorld == null && isPlayer(sender)) {
            selectedWorld = player.getWorld().getName();
            if (worldConfig == null)
                worldConfig = getStorageReference.getJailManager.getWorldSettings(selectedWorld);
        }

        inargs = sList.toArray(new String[sList.size()]);
        NPC npc;
        if (npcid == -1) {
            // Now lets find the NPC this should run on.
            npc = getStorageReference.getCitizensPlugin.getNPCSelector().getSelected(sender);
        } else {
            npc = CitizensAPI.getNPCRegistry().getById(npcid);
        }


        if (inargs.length == 0) {
            inargs = new String[]{"help"};
        }

        if (inargs[0].equalsIgnoreCase("help")) {

            for (String groupName : commandGroups) {
                StringBuilder response = new StringBuilder();

                for (Command_Record cmdRecord : registeredCommands.values()) {
                    if (cmdRecord.groupName.equals(groupName)) {
                        if (getStorageReference.hasPermissions(sender, cmdRecord.commandPermission) && isPlayer(sender)) {
                            String messageValue = getStorageReference.getMessageManager.buildMessage(sender, "command_jsonhelp." + cmdRecord.helpMessage, null, selectedPlayer, null, selectedJail, worldConfig, npc, null, 0)[0];
                            if (messageValue.trim().equals("")) {
                                getStorageReference.getMessageManager.logToConsole("Language Message Missing (" + cmdRecord.helpMessage + ")");
                            } else {
                                response.append(messageValue.replaceAll("<permission>", cmdRecord.commandPermission).replaceAll("<commandname>", cmdRecord.commandName) + ",{\"text\":\" \"},");
                            }
                        } else if (getStorageReference.hasPermissions(sender, cmdRecord.commandPermission) && (!isPlayer(sender) && cmdRecord.allowConsole)) {
                            response.append(cmdRecord.commandName + " ");
                        }
                    }
                }
                if (isPlayer(sender) && !response.toString().trim().equals("")) {
                    getStorageReference.getMessageManager.sendMessage(sender, "command_jsonhelp.command_help_group", groupName);
                    getStorageReference.getMessageManager.sendJsonRaw((Player) sender, "[" + response.toString() + "{\"text\":\"\"}]");
                } else if (!isPlayer(sender) && !response.toString().trim().equals("")) {
                    sender.sendMessage("---[" + groupName + "]--------------------");
                    sender.sendMessage(response.toString());
                }
            }
            return true;
        } else if (registeredCommands.containsKey(inargs[0])) {
            Command_Record cmdRecord = registeredCommands.get(inargs[0].toLowerCase());
            if (!cmdRecord.allowConsole & !isPlayer(sender)) {
                getStorageReference.getMessageManager.sendMessage(sender, "console_messages.command_noconsole");
                return true;
            }

            if (getStorageReference.hasPermissions(sender, cmdRecord.commandPermission) && (inargs.length - 1 >= cmdRecord.minArguments && inargs.length - 1 <= cmdRecord.maxArguments))
                return registeredCommands.get(inargs[0].toLowerCase()).invokeCommand(getStorageReference, sender, npc, inargs, selectedPlayer, selectedWorld, worldConfig, selectedJail);
            else {
                if (isPlayer(sender))
                    getStorageReference.getMessageManager.sendMessage(sender, "general_messages." + cmdRecord.badArgumentsMessage);
                else
                    getStorageReference.getMessageManager.sendMessage(sender, "console_messages." + cmdRecord.badArgumentsMessage);
            }
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> results = new ArrayList<>();
        Boolean isPlayer = (sender instanceof Player);
        if (arguments.length == 1) {
            for (Command_Record cmdSetting : this.registeredCommands.values()) {
                if ((!isPlayer && cmdSetting.allowConsole) || getStorageReference.hasPermissions(sender, cmdSetting.commandPermission)) {
                    if ((arguments[0].trim().length() > 0 && cmdSetting.commandName.startsWith(arguments[0].trim().toLowerCase())) || arguments[0].trim().equals(""))
                        results.add(cmdSetting.commandName);
                }
            }
        } else {
            for (Command_Record cmdSetting : this.registeredCommands.values()) {
                if ((!isPlayer && cmdSetting.allowConsole) || getStorageReference.hasPermissions(sender, cmdSetting.commandPermission)) {
                    if (arguments[0].trim().equalsIgnoreCase(cmdSetting.commandName)) {
                        if (arguments.length - 1 <= cmdSetting.arguments.length) {
                            String argumentLine = cmdSetting.arguments[arguments.length - 2];
                            String currentArg = arguments[arguments.length - 1].trim();
                            String priorArg = "";
                            if (arguments.length - 2 > -1)
                                priorArg = arguments[arguments.length - 2].trim();

                            if (argumentLine.contains("|")) {
                                if (currentArg.equals("")) {
                                    for (String itemDesc : argumentLine.split("\\|")) {
                                        results.addAll(parseTabItem(itemDesc, priorArg,currentArg));
                                    }

                                    return results;
                                } else {
                                    for (String argValue : argumentLine.split("\\|")) {
                                        if (argValue.toLowerCase().startsWith(currentArg.toLowerCase())) {
                                            results.addAll(parseTabItem(argValue, priorArg,currentArg));
                                        }
                                    }
                                    return results;
                                }
                            } else if (argumentLine.equalsIgnoreCase("<PLAYERNAME>")) {
                                return null;
                            } else {
                                results.addAll(parseTabItem(argumentLine, priorArg,currentArg));
                                return results;
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public void registerCommandClass(Class<?> commandClass) {
        for (Method commandMethod : commandClass.getMethods()) {
            if (commandMethod.isAnnotationPresent(CommandInfo.class)) {
                CommandInfo methodAnnotation = commandMethod.getAnnotation(CommandInfo.class);
                if (!commandGroups.contains(methodAnnotation.group()))
                    commandGroups.add(methodAnnotation.group());
                Command_Record cmdRecord = new Command_Record(methodAnnotation.name(), methodAnnotation.group(), methodAnnotation.permission(), methodAnnotation.badArgumentsMessage(), methodAnnotation.helpMessage(), methodAnnotation.allowConsole(), methodAnnotation.minArguments(), methodAnnotation.maxArguments(), methodAnnotation.arguments(), commandClass, commandMethod.getName());
                registeredCommands.put(methodAnnotation.name(), cmdRecord);
            }
        }
    }

    private List<String> parseTabItem(String item, String priorArg, String currentValue) {
        List<String> results = new ArrayList<String>();

        if (item.equalsIgnoreCase("<player>") && (!priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--npc"))) {
            for (Player plr : getStorageReference.pluginInstance.getServer().getOnlinePlayers()) {
                if (currentValue.length() > 0)
                {
                    if (String.valueOf(plr.getName()).toLowerCase().startsWith(currentValue.toLowerCase()))
                        results.add(plr.getName());
                } else {
                    results.add(plr.getName());
                }
            }
        } else if (item.equalsIgnoreCase("<world>") && (!priorArg.equalsIgnoreCase("--player") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--npc"))) {
            for (World world : getStorageReference.pluginInstance.getServer().getWorlds()) {
                if (currentValue.length() > 0)
                {
                    if (world.getName().toLowerCase().startsWith(currentValue.toLowerCase()))
                        results.add(world.getName());
                } else {
                    results.add(world.getName());
                }
            }
        } else if (item.equalsIgnoreCase("<npc>") && (!priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--player"))) {
            for (NPC npc : getStorageReference.getCitizensPlugin.getNPCRegistry()) {
                if (currentValue.length() > 0)
                {
                    if (String.valueOf(npc.getId()).toLowerCase().startsWith(currentValue.toLowerCase()))
                        results.add(String.valueOf(npc.getId()));
                } else {
                    results.add(String.valueOf(npc.getId()));
                }
            }
        } else if (item.equalsIgnoreCase("<jail>") && (!priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--player") && !priorArg.equalsIgnoreCase("--npc"))) {
            for (World world : getStorageReference.pluginInstance.getServer().getWorlds()) {
                for (Jail_Setting jailRecord : getStorageReference.getJailManager.getWorldJails(world.getName())) {
                    if (currentValue.length() > 0)
                    {
                        if (String.valueOf(jailRecord.jailName).toLowerCase().startsWith(currentValue.toLowerCase()))
                            results.add(String.valueOf(jailRecord.jailName));
                    } else {
                        results.add(String.valueOf(jailRecord.jailName));
                    }
                }
            }
        } else if (item.equalsIgnoreCase("<region>") && (!priorArg.equalsIgnoreCase("--player") && !priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--npc"))) {
            for (World world : getStorageReference.pluginInstance.getServer().getWorlds()) {
                if (currentValue.length() > 0)
                {
                    for (String regionname : getStorageReference.getWorldGuardPlugin.getWorldRegions(world))
                    {
                        if (String.valueOf(regionname).toLowerCase().startsWith(currentValue.toLowerCase()))
                            results.add(regionname);
                    }
                } else {
                    results.addAll(getStorageReference.getWorldGuardPlugin.getWorldRegions(world));
                }
            }
        } else if (item.equalsIgnoreCase("<groups>") && (!priorArg.equalsIgnoreCase("--player") && !priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--npc"))) {
            if (getStorageReference.getPermissionManager != null)
            {
                for (String groupName : Arrays.asList(getStorageReference.getPermissionManager.getGroups())) {
                    if (currentValue.length() > 0)
                    {
                        if (groupName.toLowerCase().startsWith(currentValue.toLowerCase()))
                            results.add(groupName);
                    } else {
                        results.add(groupName);
                    }
                }
            }
        } else if (item.equalsIgnoreCase("<server>") && (!priorArg.equalsIgnoreCase("--player") && !priorArg.equalsIgnoreCase("--world") && !priorArg.equalsIgnoreCase("--jail") && !priorArg.equalsIgnoreCase("--npc"))) {
            results.addAll(getStorageReference.getBungeeListener.getServerList());
            for (String serverName : getStorageReference.getBungeeListener.getServerList()) {
                if (currentValue.length() > 0)
                {
                    if (serverName.toLowerCase().startsWith(currentValue.toLowerCase()))
                        results.add(serverName);
                } else {
                    results.add(serverName);
                }
            }
        } else if (item.equalsIgnoreCase("<groups>") || item.equalsIgnoreCase("<jail>") || item.equalsIgnoreCase("<npc>") || item.equalsIgnoreCase("<world>") || item.equalsIgnoreCase("<player>")) {

        } else {
            results.add(item);
        }
        return results;
    }

    private boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player)
            return true;
        return false;
    }
}
