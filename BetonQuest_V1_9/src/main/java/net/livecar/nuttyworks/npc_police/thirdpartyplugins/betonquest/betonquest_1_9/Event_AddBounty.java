package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Player;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Event_AddBounty extends QuestEvent {
    private Double bountyAdded = 0D;

    public Event_AddBounty(Instruction instruction) throws InstructionParseException {
        super(instruction);
        if (NumberUtils.isNumber(instruction.getInstruction().replaceAll("-", "").replaceAll(".", "")))
            bountyAdded = Double.parseDouble(instruction.getInstruction());
        else
            throw new InstructionParseException("Values should be numeric");
    }

    @Override
    public void run(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        if (!passedPlayer.isOnline())
            return;

        PlayerManager plrManager = API.getPlayerManager(passedPlayer);
        plrManager.changeBounty(bountyAdded);
    }
}

