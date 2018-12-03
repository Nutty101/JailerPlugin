package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.bukkit.entity.Player;

import java.util.Arrays;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Condition_PriorWantedStatus extends Condition {

    private CURRENT_STATUS testedStatus = null;

    public Condition_PriorWantedStatus(Instruction instruction) throws InstructionParseException {
        super(instruction);

        testedStatus = CURRENT_STATUS.valueOf(instruction.getInstruction());
        if (testedStatus == null)
            throw new InstructionParseException("Valid values are " + Arrays.toString(CURRENT_STATUS.values()));
    }

    public boolean check(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        PlayerManager plrManager = API.getPlayerManager(passedPlayer);

        if (plrManager.getCurrentStatus() == testedStatus)
            return true;
        else
            return false;
    }
}
