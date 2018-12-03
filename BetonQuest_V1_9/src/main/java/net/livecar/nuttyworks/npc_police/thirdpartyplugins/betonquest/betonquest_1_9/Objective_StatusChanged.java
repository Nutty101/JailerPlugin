package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.events.StatusChangedEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Objective_StatusChanged extends Objective implements Listener {
    private CURRENT_STATUS objectStatus = null;

    public Objective_StatusChanged(Instruction instruction) throws InstructionParseException {
        super(instruction);
        objectStatus = CURRENT_STATUS.valueOf(instruction.getInstruction());
        if (objectStatus == null)
            throw new InstructionParseException("Valid values are " + Arrays.toString(CURRENT_STATUS.values()));
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getServer().getPluginManager().getPlugin("NPC_Police"));
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onJailStatusChanged(StatusChangedEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer().getPlayer());
        if (containsPlayer(playerID) && event.getStatus() == objectStatus)
            completeObjective(playerID);
    }
}