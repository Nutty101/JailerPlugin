package net.livecar.nuttyworks.npc_police.listeners.commands;

import org.bukkit.Material;

import java.util.Date;

public class Pending_Command {
    public Material blockType = null;
    public String commandString = "";
    public Date timeOutTime = null;
    public String timeoutMessage = "";
    public enum ACTION {INTERACT, CHAT}
}
