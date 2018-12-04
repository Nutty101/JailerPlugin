package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.bukkit.entity.Player;
import org.apache.commons.lang3.math.NumberUtils;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Condition_CurrentBounty extends Condition {
    private int testedBounty = 0;

    public Condition_CurrentBounty(Instruction instruction) throws InstructionParseException {
        super(instruction);

        if (NumberUtils.isNumber(instruction.getInstruction()))
            testedBounty = Integer.parseInt(instruction.getInstruction());
        else
            throw new InstructionParseException("Values should be numeric");
    }

    public boolean check(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        PlayerManager plrManager = API.getPlayerManager(passedPlayer);

        if (plrManager.getCurrentBounty() >= testedBounty)
            return true;
        else
            return false;
    }
}
