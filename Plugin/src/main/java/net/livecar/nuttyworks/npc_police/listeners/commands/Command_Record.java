package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Command_Record {
    public String commandName = "";
    public String groupName = "";
    public String commandPermission = "";
    public String badArgumentsMessage = "";
    public String helpMessage = "";
    public String[] arguments = null;
    public Boolean allowConsole = false;
    public int minArguments = 0;
    public int maxArguments = 50;

    private Class<?> commandClass = null;
    private Method commandMethod = null;

    public Command_Record(String commandName, String groupName, String commandPermission, String badArgumentsMessage, String helpMessage, Boolean allowConsole, int minArguments, int maxArguments, String[] arguments, Class<?> commandClass, String commandMethod) {
        this.commandName = commandName;
        this.groupName = groupName;
        this.commandPermission = commandPermission;
        this.badArgumentsMessage = badArgumentsMessage;
        this.helpMessage = helpMessage;
        this.allowConsole = allowConsole;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.arguments = arguments;
        this.commandClass = commandClass;
        this.commandMethod = getMethod(commandClass, commandMethod);
    }

    public boolean invokeCommand(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        try {
            //return (boolean) commandMethod.invoke(getMethodClass().getDeclaredConstructor(NPC_Police.class).newInstance(policeRef), sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);

            Constructor<?> ctr = commandClass.getConstructor();
            ctr.setAccessible(true);

            return (boolean) commandMethod.invoke(ctr.newInstance(), policeRef, sender, npc, inargs, playerRecord, serverWorld, selectedWorld, selectedJail);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | InstantiationException | NoSuchMethodException e) {
            // Oops!
            e.printStackTrace();
        }
        return false;
    }

    private Method getMethod(Class<?> commandClass, String methodName) {
        try {
            return commandClass.getMethod(methodName, NPC_Police.class, CommandSender.class, NPC.class, String[].class, Arrest_Record.class, String.class, World_Setting.class, Jail_Setting.class);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
}
