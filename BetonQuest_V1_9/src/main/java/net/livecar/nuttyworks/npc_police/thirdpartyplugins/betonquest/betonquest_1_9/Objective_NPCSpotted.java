package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import java.util.HashMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.api.events.PlayerSpottedEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Objective_NPCSpotted extends Objective implements Listener {
    private HashMap<Integer, NPC> objectiveGuards = null;

    public Objective_NPCSpotted(Instruction instruction) throws InstructionParseException {
        super(instruction);

        String[] npcIDS = instruction.getArray();

        if (npcIDS.length == 0)
            throw new InstructionParseException("You need to provide a list, or a single npc ID to watch");

        objectiveGuards = new HashMap<Integer, NPC>();

        for (String npcID : npcIDS) {
            if (NumberUtils.isNumber(npcID)) {
                NPC tmpNPC = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));
                if (tmpNPC == null)
                    throw new InstructionParseException("You have provided an NPC id of " + npcID + " but it does not exist!");
                objectiveGuards.put(tmpNPC.getId(), tmpNPC);
            }
        }
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
    public void onGuardSpottedPlayer(PlayerSpottedEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer().getPlayer());
        if (containsPlayer(playerID) && objectiveGuards.containsKey(event.getWitnessNPC().getId()))
            completeObjective(playerID);
    }
}