package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import java.util.Arrays;

import org.bukkit.entity.Player;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Event_SetCurrentStatus extends QuestEvent {
    private CURRENT_STATUS setStatus = null;

    public Event_SetCurrentStatus(Instruction instruction) throws InstructionParseException {
        super(instruction);

        setStatus = CURRENT_STATUS.valueOf(instruction.getInstruction());
        if (setStatus == null)
            throw new InstructionParseException("Valid values are " + Arrays.toString(CURRENT_STATUS.values()));
    }

    @Override
    public void run(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        if (!passedPlayer.isOnline())
            return;

        PlayerManager plrManager = API.getPlayerManager(passedPlayer);
        plrManager.setCurrentStatus(setStatus);
    }
}

