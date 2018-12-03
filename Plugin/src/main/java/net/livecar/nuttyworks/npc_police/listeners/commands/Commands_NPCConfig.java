package net.livecar.nuttyworks.npc_police.listeners.commands;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.annotations.CommandInfo;
import net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_SETTING;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.jails.World_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Commands_NPCConfig {
    @CommandInfo(
            name = "npc",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npc_args",
            helpMessage = "command_npc_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.npc.info",
            allowConsole = true,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCInfo(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        // Configuration Commands
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);
        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcstick",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcstick_args",
            helpMessage = "command_npcstick_help",
            arguments = {""},
            permission = "npcpolice.npc.stick",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCStick(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        // Configuration Commands
        Player player = (Player) sender;
        ItemStack stack = new ItemStack(Material.STICK, 1);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eNPCPolice &2[&fNPCStick&2]"));
        im.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&5Right Click NPCs to set NPC Police settings")));
        stack.setItemMeta(im);
        player.getInventory().addItem(new ItemStack(stack));
        policeRef.getMessageManager.sendMessage(sender, "general_messages.commands_npcstick");
        return true;
    }

    @CommandInfo(
            name = "npcguard",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcguard_args",
            helpMessage = "command_npcguard_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.npc.guard",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCSetGuard(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        npc.getTrait(NPCPolice_Trait.class).isGuard = !npc.getTrait(NPCPolice_Trait.class).isGuard;
        if (npc.getTrait(NPCPolice_Trait.class).isGuard)
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_npc_guardset");
        else
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_npc_notguard");

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npclosattack",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npclosattack_args",
            helpMessage = "command_npclosattack_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.npc.losattack",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCLosAttack(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack < 0)
            npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack = 1;
        else if (npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack == 0)
            if (serverWorld.equalsIgnoreCase("_GlobalSettings"))
                npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack = 1;
            else
                npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack = -1;
        else if (npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack == 1)
            npc.getTrait(NPCPolice_Trait.class).lineOfSightAttack = 0;

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }


    @CommandInfo(
            name = "npcmenu",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcmenu_args",
            helpMessage = "command_npcmenu_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.npc.menu",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCSetMenu(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        npc.getTrait(NPCPolice_Trait.class).hasMenu = !npc.getTrait(NPCPolice_Trait.class).hasMenu;
        if (npc.getTrait(NPCPolice_Trait.class).hasMenu)
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_npc_hasmenu");
        else
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_npc_nomenu");

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcpvp",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcpvp_args",
            helpMessage = "command_npcpvp_help",
            arguments = {"--npc", "<npc>"},
            permission = "npcpolice.npc.pvp",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 0
    )
    public boolean npcConfig_NPCSetPVP(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (npc.getTrait(NPCPolice_Trait.class).monitorPVP < 0)
            npc.getTrait(NPCPolice_Trait.class).monitorPVP = 1;
        else if (npc.getTrait(NPCPolice_Trait.class).monitorPVP == 0)
            if (serverWorld.equalsIgnoreCase("_GlobalSettings"))
                npc.getTrait(NPCPolice_Trait.class).monitorPVP = 1;
            else
                npc.getTrait(NPCPolice_Trait.class).monitorPVP = -1;
        else if (npc.getTrait(NPCPolice_Trait.class).monitorPVP == 1)
            npc.getTrait(NPCPolice_Trait.class).monitorPVP = 0;

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcdistance",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcdistance_args",
            helpMessage = "command_npcdistance_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.distance",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCSetDistance(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).maxDistance_Guard = -1;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).maxDistance_Guard = Integer.parseInt(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcminbounty",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcminbounty_args",
            helpMessage = "command_npcminbounty_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.minbounty",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCSetMinBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).minBountyAttack = -1;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).minBountyAttack = Integer.parseInt(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcwanted",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcwanted_args",
            helpMessage = "command_npcwanted_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.config.npc.wanted",
            allowConsole = false,
            minArguments = 0,
            maxArguments = 1
    )
    public boolean npcConfig_NPCSetWanted(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            if (npc.getTrait(NPCPolice_Trait.class).wantedSetting.next() == null)
                npc.getTrait(NPCPolice_Trait.class).wantedSetting = WANTED_SETTING.NONE;
            else
                npc.getTrait(NPCPolice_Trait.class).wantedSetting = npc.getTrait(NPCPolice_Trait.class).wantedSetting.next();
        } else if (inargs.length == 2) {
            if (WANTED_SETTING.contains(inargs[1].toUpperCase())) {
                npc.getTrait(NPCPolice_Trait.class).wantedSetting = WANTED_SETTING.valueOf(inargs[1].toUpperCase());
            }
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcdamagebounty",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcdamagebounty_args",
            helpMessage = "command_npcdamagebounty_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.npcdamagebounty",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCDamageBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).bounty_assault = -1.0D;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).bounty_assault = Double.parseDouble(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcmurderbounty",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcmurderbounty_args",
            helpMessage = "command_npcmurderbounty_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.npcmurderbounty",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCMurderBounty(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).bounty_murder = -1.0D;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).bounty_murder = Double.parseDouble(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcdamagetime",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcdamagetime_args",
            helpMessage = "command_npcdamagetime_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.npcdamagetime",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCDamageTime(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).time_assault = -1;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).time_assault = Integer.parseInt(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

    @CommandInfo(
            name = "npcmurdertime",
            group = "NPC Configuration",
            badArgumentsMessage = "command_npcmurdertime_args",
            helpMessage = "command_npcmurdertime_help",
            arguments = {"--npc|#", "<npc>", "#"},
            permission = "npcpolice.npc.npcmurdertime",
            allowConsole = false,
            minArguments = 1,
            maxArguments = 1
    )
    public boolean npcConfig_NPCMurderTime(NPC_Police policeRef, CommandSender sender, NPC npc, String[] inargs, Arrest_Record playerRecord, String serverWorld, World_Setting selectedWorld, Jail_Setting selectedJail) {
        if (npc == null) {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.invalid_npc");
            return true;
        }
        if (!npc.hasTrait(NPCPolice_Trait.class))
            npc.addTrait(NPCPolice_Trait.class);

        if (inargs.length == 1) {
            npc.getTrait(NPCPolice_Trait.class).time_murder = -1;
        } else if (policeRef.getUtilities.isNumeric(inargs[1])) {
            npc.getTrait(NPCPolice_Trait.class).time_murder = Integer.parseInt(inargs[1]);
        } else {
            policeRef.getMessageManager.sendMessage(sender, "general_messages.config_command_numeric");
        }

        policeRef.getMessageManager.sendMessage(sender, "npc_settings", npc.getTrait(NPCPolice_Trait.class));
        return true;
    }

}
