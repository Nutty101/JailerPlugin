package net.livecar.nuttyworks.npc_police.thirdpartyplugins.betonquest.betonquest_1_9;

import org.bukkit.entity.Player;

import net.livecar.nuttyworks.npc_police.API;
import net.livecar.nuttyworks.npc_police.api.managers.PlayerManager;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Event_ReturnLockedInventory extends QuestEvent {
    public Event_ReturnLockedInventory(Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public void run(String playerID) {
        Player passedPlayer = PlayerConverter.getPlayer(playerID);
        if (!passedPlayer.isOnline())
            return;

        PlayerManager plrManager = API.getPlayerManager(passedPlayer);
        plrManager.returnLockedInventory();
    }
}

