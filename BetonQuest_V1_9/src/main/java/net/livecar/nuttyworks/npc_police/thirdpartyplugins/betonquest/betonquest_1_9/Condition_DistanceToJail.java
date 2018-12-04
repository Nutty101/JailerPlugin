package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.bukkit.entity.Player;
import org.apache.commons.lang3.math.NumberUtils;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Condition_DistanceToJail extends Condition {
    private Double minDistance = 0D;

    public Condition_DistanceToJail(Instruction instruction) throws InstructionParseException {
        super(instruction);

        if (NumberUtils.isNumber(instruction.getInstruction()))
            minDistance = Double.parseDouble(instruction.getInstruction());
        else
            throw new InstructionParseException("Values should be numeric");
    }

    public boolean check(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        PlayerManager plrManager = API.getPlayerManager(passedPlayer);

        if (plrManager.distanceToJail() >= minDistance)
            return true;
        else
            return false;
    }
}
