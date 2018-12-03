package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.livecar.nuttyworks.npc_police.api.events.BountyChangedEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Objective_BountyChanged extends Objective implements Listener {
    private Double objectiveBounty = Double.MAX_VALUE;

    public Objective_BountyChanged(Instruction instruction) throws InstructionParseException {
        super(instruction);

        if (NumberUtils.isNumber(instruction.getInstruction()))
            objectiveBounty = Double.parseDouble(instruction.getInstruction());
        else
            throw new InstructionParseException("Values should be numeric");
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
    public void onBountyChanged(BountyChangedEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer().getPlayer());
        if (containsPlayer(playerID) && event.getBounty() >= this.objectiveBounty)
            completeObjective(playerID);
    }
}