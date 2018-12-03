package net.livecar.nuttyworks.npc_police.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;

import java.util.Arrays;

public class CurrentStatusFlag extends Flag<CURRENT_STATUS> {
    public CurrentStatusFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public CurrentStatusFlag(String name) {
        super(name);
    }

    @Override
    public CURRENT_STATUS parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();

        try {
            return CURRENT_STATUS.valueOf(input);
        } catch (Exception e) {
            throw new InvalidFlagFormat("Unknown value '" + input + "' in " + Arrays.toString(CURRENT_STATUS.values()));
        }
    }

    @Override
    public CURRENT_STATUS unmarshal(Object o) {
        try {
            return CURRENT_STATUS.valueOf(String.valueOf(o));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Object marshal(CURRENT_STATUS o) {
        return o.toString();
    }

}
