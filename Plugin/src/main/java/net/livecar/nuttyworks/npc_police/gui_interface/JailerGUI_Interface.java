package net.livecar.nuttyworks.npc_police.gui_interface;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.events.Core_PlayerPayBountyEvent;
import net.livecar.nuttyworks.npc_police.api.events.PlayerPayBountyEvent;
import net.livecar.nuttyworks.npc_police.citizens.NPCPolice_Trait;
import net.livecar.nuttyworks.npc_police.gui_interface.JailMenuData.Action;
import net.livecar.nuttyworks.npc_police.gui_interface.JailerGUI_Inventory.OptionClickEvent;
import net.livecar.nuttyworks.npc_police.jails.Jail_Setting;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class JailerGUI_Interface {
    private NPC_Police getStorageReference;

    public JailerGUI_Interface(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    public void MenuClick(final JailerGUI_Inventory.OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();
        switch (jmenuData.getMenuAction()) {
            case Close:
                break;
            case PayOwn:
                PayOwnFine(event);
                break;
            case PayPlayer:
                PayOtherUser(event);
                break;
            case Arrested:
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        getStorageReference.pluginInstance, () -> Arrested(event), 2
                );
                break;
            case Warrants:
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        getStorageReference.pluginInstance, () -> Wanted(event), 2
                );
                break;
            case Escaped:
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        getStorageReference.pluginInstance, () -> Escaped(event), 2
                );
                break;
            case MainMenu:
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        getStorageReference.pluginInstance, () -> {
                            JailMenuData jailData = (JailMenuData) event.getCustom();
                            Jailer_Menu(event.getPlayer(), jailData.getNPC(), jailData.getTrait(), jailData.GetJailData(), jailData.GetPlayerData());
                        }, 2
                );
                break;
        }
    }

    public void Jailer_Menu(final PlayerInteractEntityEvent event, NPC npc) {
        Player player = event.getPlayer();
        NPCPolice_Trait policeTrait = npc.getTrait(NPCPolice_Trait.class);
        Jail_Setting currentJail = getStorageReference.getJailManager.getJailAtLocation(npc.getEntity().getLocation());
        Arrest_Record playerRecord = getStorageReference.getPlayerManager.getPlayer(player.getUniqueId());

        Jailer_Menu(player, npc, policeTrait, currentJail, playerRecord);
    }

    public void Jailer_Menu(final Player player, NPC npc, NPCPolice_Trait policeTrait, Jail_Setting currentJail, Arrest_Record playerRecord) {
        final JailerGUI_Inventory TransportMenu = new JailerGUI_Inventory(
                getStorageReference.getMessageManager.buildMessage(player, "gui_menu.main_title", policeTrait, null, playerRecord, currentJail, null, npc, null, 0)[0], 9, new JailerGUI_Inventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(JailerGUI_Inventory.OptionClickEvent event) {
                MenuClick(event);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        }, getStorageReference.pluginInstance
        );

        if (getStorageReference.hasPermissions(player, "npcpolice.fines.payown")) {
            if (playerRecord != null) {
                if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED && playerRecord.getPriorStatus() != CURRENT_STATUS.ESCAPED) {
                    TransportMenu.setOption(0, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("player_payment_menuitem"), 1), new JailMenuData(Action.PayOwn, playerRecord, currentJail, npc, policeTrait), getStorageReference.getMessageManager.buildMessage(player, "gui_menu.pay_own_menu", policeTrait, playerRecord, null, currentJail, null, npc, null, 0));
                }
            }
        }
        if (getStorageReference.hasPermissions(player, "npcpolice.fines.payothers") || getStorageReference.hasPermissions(player, "npcpolice.lists.incarcerated")) {
            TransportMenu.setOption(2, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("pay_others_menuitem"), 1), new JailMenuData(Action.Arrested, playerRecord, currentJail, npc, policeTrait), getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_serving_title", policeTrait, playerRecord, null, currentJail, null, npc, null, 0)[0], getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_serving_menu", policeTrait, playerRecord, null, currentJail, null, npc, null, 0));
        }
        if (getStorageReference.hasPermissions(player, "npcpolice.lists.wanted")) {
            TransportMenu.setOption(4, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("list_wanted_menuiten"), 1), new JailMenuData(Action.Warrants, playerRecord, currentJail, npc, policeTrait), getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_wanted_title", policeTrait, playerRecord, null, currentJail, null, npc, null, 0)[0], getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_wanted_menu", policeTrait, playerRecord, null, currentJail, null, npc, null, 0));
        }
        if (getStorageReference.hasPermissions(player, "npcpolice.lists.escaped")) {
            TransportMenu.setOption(6, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("list_escaped_menuitem"), 1), new JailMenuData(Action.Escaped, playerRecord, currentJail, npc, policeTrait), getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_escaped_title", policeTrait, playerRecord, null, currentJail, null, npc, null, 0)[0], getStorageReference.getMessageManager.buildMessage(player, "gui_menu.list_escaped_menu", policeTrait, playerRecord, null, currentJail, null, npc, null, 0));
        }
        TransportMenu.setOption(8, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("close_menu_menuitem"), 1), new JailMenuData(Action.Close, playerRecord, currentJail, npc, policeTrait), getStorageReference.getMessageManager.buildMessage(player, "gui_menu.close_menu", policeTrait, playerRecord, null, currentJail, null, npc, null, 0)[0]);
        TransportMenu.open(player.getPlayer());
    }

    private void PayOwnFine(final OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();
        if (getStorageReference.hasPermissions(event.getPlayer(), "npcpolice.fines.payown")) {
            Arrest_Record plrRecord = jmenuData.GetPlayerData();

            if (plrRecord.getCurrentStatus() == CURRENT_STATUS.JAILED && plrRecord.getPriorStatus() != CURRENT_STATUS.ESCAPED) {

                    //@Since 2.2.1 - First attempt to raise the event and see if anyone covers the payment prior to the economy plugin.
                    PlayerPayBountyEvent playerPayBountyEvent = new Core_PlayerPayBountyEvent(getStorageReference, event.getPlayer(), event.getPlayer(), plrRecord.getBounty(), plrRecord);
                    try { Bukkit.getServer().getPluginManager().callEvent(playerPayBountyEvent); } catch (Exception err) {}

                    if (playerPayBountyEvent.isCancelled())
                        return;

                    if (playerPayBountyEvent.getBountyPaid()) {
                        plrRecord.releasePlayer();
                        return;
                    }

                if (getStorageReference.getEconomyManager != null) {
                    if (getStorageReference.getEconomyManager.getBalance(event.getPlayer()) >= ((double) (plrRecord.getBounty()))) {
                        getStorageReference.getEconomyManager.withdrawPlayer(event.getPlayer(), ((double) plrRecord.getBounty()));
                        getStorageReference.getMessageManager.sendMessage(event.getPlayer(), "judge_interaction.fines_paid", jmenuData.getTrait(), plrRecord);

                        plrRecord.releasePlayer();
                    } else {
                        getStorageReference.getMessageManager.sendMessage(event.getPlayer(), "judge_interaction.to_broke", jmenuData.getTrait(), plrRecord);
                    }
                }
            } else {
                getStorageReference.getMessageManager.sendMessage(event.getPlayer(), "judge_interaction.serving_time", plrRecord);
            }
        }
    }

    private void PayOtherUser(final OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();
        if (getStorageReference.hasPermissions(event.getPlayer(), "npcpolice.fines.payothers")) {
            Arrest_Record plrRecord = jmenuData.GetPlayerData();

            if (plrRecord.getCurrentStatus() == CURRENT_STATUS.JAILED && plrRecord.getPriorStatus() != CURRENT_STATUS.ESCAPED) {

                //@Since 2.2.1 - First attempt to raise the event and see if anyone covers the payment prior to the economy plugin.
                PlayerPayBountyEvent playerPayBountyEvent = new Core_PlayerPayBountyEvent(getStorageReference, plrRecord.getOfflinePlayer(), event.getPlayer(), plrRecord.getBounty(), plrRecord);
                try { Bukkit.getServer().getPluginManager().callEvent(playerPayBountyEvent);} catch (Exception err) {}

                if (playerPayBountyEvent.isCancelled())
                    return;

                if (playerPayBountyEvent.getBountyPaid()) {
                    plrRecord.releasePlayer();
                    return;
                }


                if (getStorageReference.getEconomyManager != null) {
                    if (getStorageReference.getEconomyManager.getBalance(event.getPlayer()) >= ((double) (plrRecord.getBounty()))) {
                        Arrest_Record payorRecord = getStorageReference.getPlayerManager.getPlayer(event.getPlayer().getUniqueId());

                        getStorageReference.getMessageManager.sendMessage(event.getPlayer(), "judge_interaction.pay_user", jmenuData.getTrait(), plrRecord);
                        getStorageReference.getEconomyManager.withdrawPlayer(event.getPlayer(), ((double) plrRecord.getBounty()));
                        getStorageReference.getMessageManager.sendMessage(plrRecord.getPlayer(), "judge_interaction.other_paid", jmenuData.getTrait(), plrRecord, payorRecord, jmenuData.GetJailData());

                        plrRecord.releasePlayer();
                    } else {
                        getStorageReference.getMessageManager.sendMessage(event.getPlayer(), "judge_interaction.to_broke", jmenuData.getTrait(), plrRecord);
                    }
                }
            }
        }
    }

    public void Arrested(final OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();

        final JailerGUI_Inventory TransportMenu = new JailerGUI_Inventory(
                getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_serving_title", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0], 54, event1 -> {
            MenuClick(event1);
            event1.setWillDestroy(true);
            event1.setWillClose(true);
        }, getStorageReference.pluginInstance
        );

        TransportMenu.setOption(53, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("close_menu_menuitem"), 1), new JailMenuData(Action.MainMenu, jmenuData.GetPlayerData(), jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.return_menu", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0]);
        int nCnt = 0;
        for (Arrest_Record playerRecord : getStorageReference.getPlayerManager.getPlayerRecords()) {
            if (playerRecord.isOnline()) {
                if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED && playerRecord.getPriorStatus() != CURRENT_STATUS.ESCAPED && getStorageReference.hasPermissions(event.getPlayer(), "npcpolice.fines.payothers")) {
                    TransportMenu.setOption(nCnt, getStorageReference.getVersionBridge.createPlayerHead(playerRecord.getOfflinePlayer()), new JailMenuData(Action.PayPlayer, playerRecord, jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), playerRecord.getPlayer().getName(), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_serving_bail", jmenuData.getTrait(), playerRecord, null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0));
                    nCnt++;
                } else if (playerRecord.getCurrentStatus() == CURRENT_STATUS.JAILED) {
                    TransportMenu.setOption(nCnt, getStorageReference.getVersionBridge.createPlayerHead(playerRecord.getOfflinePlayer()), new JailMenuData(Action.Arrested, playerRecord, jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), playerRecord.getPlayer().getName(), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_serving_nobail", jmenuData.getTrait(), playerRecord, null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0));
                    nCnt++;
                }
            }
        }
        TransportMenu.open(event.getPlayer());
    }

    public void Wanted(final OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();

        final JailerGUI_Inventory TransportMenu = new JailerGUI_Inventory(
                getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_wanted_title", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0], 54, event1 -> {
            MenuClick(event1);
            event1.setWillDestroy(true);
            event1.setWillClose(true);
        }, getStorageReference.pluginInstance
        );

        TransportMenu.setOption(53, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("close_menu_menuitem"), 1), new JailMenuData(Action.MainMenu, jmenuData.GetPlayerData(), jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.return_menu", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0]);
        int nCnt = 0;
        for (Arrest_Record playerRecord : getStorageReference.getPlayerManager.getPlayerRecords()) {
            if (playerRecord.isOnline() && playerRecord.getCurrentStatus() == CURRENT_STATUS.WANTED) {
                TransportMenu.setOption(nCnt, getStorageReference.getVersionBridge.createPlayerHead(playerRecord.getOfflinePlayer()), new JailMenuData(Action.Warrants, playerRecord, jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), playerRecord.getPlayer().getName(), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_wanted_desc", jmenuData.getTrait(), playerRecord, null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0]);
                nCnt++;
            }
        }
        TransportMenu.open(event.getPlayer());
    }

    public void Escaped(final OptionClickEvent event) {
        JailMenuData jmenuData = (JailMenuData) event.getCustom();

        final JailerGUI_Inventory TransportMenu = new JailerGUI_Inventory(
                getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_escaped_title", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0], 54, event1 -> {
            MenuClick(event1);
            event1.setWillDestroy(true);
            event1.setWillClose(true);
        }, getStorageReference.pluginInstance
        );

        TransportMenu.setOption(53, new ItemStack(getStorageReference.getVersionBridge.getGuiMenuItems().get("close_menu_menuitem"), 1), new JailMenuData(Action.MainMenu, jmenuData.GetPlayerData(), jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.return_menu", jmenuData.getTrait(), jmenuData.GetPlayerData(), null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0]);
        int nCnt = 0;
        for (Arrest_Record playerRecord : getStorageReference.getPlayerManager.getPlayerRecords()) {
            if (playerRecord.isOnline() && playerRecord.getCurrentStatus() == CURRENT_STATUS.ESCAPED) {
                TransportMenu.setOption(nCnt, getStorageReference.getVersionBridge.createPlayerHead(playerRecord.getOfflinePlayer()), new JailMenuData(Action.Escaped, playerRecord, jmenuData.GetJailData(), jmenuData.getNPC(), jmenuData.getTrait()), playerRecord.getPlayer().getName(), getStorageReference.getMessageManager.buildMessage(event.getPlayer(), "gui_menu.list_escaped_desc", jmenuData.getTrait(), playerRecord, null, jmenuData.GetJailData(), null, jmenuData.getNPC(), null, 0)[0]);
                nCnt++;
            }
        }
        TransportMenu.open(event.getPlayer());
    }
}
