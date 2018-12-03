package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Player;

import java.util.Date;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Condition_SecondsSinceEscape extends Condition {
    private long timeSinceEscape = Integer.MAX_VALUE;

    public Condition_SecondsSinceEscape(Instruction instruction) throws InstructionParseException {
        super(instruction);

        if (NumberUtils.isNumber(instruction.getInstruction()))
            timeSinceEscape = Long.parseLong(instruction.getInstruction());
        else
            throw new InstructionParseException("Values should be numeric");
    }

    public boolean check(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        PlayerManager plrManager = API.getPlayerManager(passedPlayer);

        long difference = (new Date()).getTime() - plrManager.getLastEscape().getTime() / 1000;

        if (difference > timeSinceEscape)
            return true;
        else
            return false;
    }
}
