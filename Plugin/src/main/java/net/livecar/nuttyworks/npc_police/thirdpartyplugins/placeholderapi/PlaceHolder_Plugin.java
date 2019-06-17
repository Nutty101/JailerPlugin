package net.livecar.nuttyworks.npc_police.thirdpartyplugins.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class PlaceHolder_Plugin extends PlaceholderExpansion {
    private NPC_Police getStorageReference = null;

    public PlaceHolder_Plugin(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String getIdentifier() {
        return "npcpolice";
    }

    @Override
    public String getAuthor() {
        return "Sir_Nutty";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist(){return true;}


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        try {
            Arrest_Record plrRecord = getStorageReference.getPlayerManager.getPlayer(player.getUniqueId());
            if (plrRecord == null)
                return "";
            if (plrRecord.getCurrentStatus() == null)
                return "";

            switch (identifier) {
                case "user_bounty":
                    return String.valueOf(plrRecord.getBountyInt());
                case "user_totalbounty":
                    return String.valueOf(plrRecord.getTotalBountyInt());
                case "user_status":
                    if (plrRecord.getCurrentStatus() == CURRENT_STATUS.JAILED) {
                        switch (plrRecord.isInCell()) {
                            case FALSE:
                                return getStorageReference.getMessageManager.getResultMessage("result_messages.jailed_outcell")[0];
                            case NOTSET:
                                return getStorageReference.getMessageManager.getResultMessage("result_messages.jailed")[0];
                            case TRUE:
                                return getStorageReference.getMessageManager.getResultMessage("result_messages.jailed_incell")[0];
                            default:
                                break;
                        }
                    } else
                        return plrRecord.getCurrentStatus().toString();
                case "user_prior":
                    return plrRecord.getPriorStatus().toString();
                case "user_jailtime":
                    Long secondsLeft = plrRecord.getPlayerJailTime();
                    if (secondsLeft == Long.MAX_VALUE)
                        return getStorageReference.getMessageManager.getResultMessage("result_Messages.endless")[0];
                    return getStorageReference.getUtilities.secondsToTime(secondsLeft);
                case "user_jail":
                    if (plrRecord.currentJail == null)
                        return "";
                    else
                        return plrRecord.currentJail.displayName;
                case "user_ttl_arrests":
                    return String.valueOf(plrRecord.getStat("ARRESTED"));
                case "user_ttl_murders":
                    return String.valueOf(plrRecord.getStat("MURDER"));
                case "user_ttl_escapes":
                    return String.valueOf(plrRecord.getStat("ESCAPE"));
                case "user_lst_arrest":
                    if (plrRecord.getLastArrest().getTime() < 1451606400000L)
                        return "";
                    return (new SimpleDateFormat("MMM dd HH:mm").format(plrRecord.getLastArrest()).toString());
                case "user_lst_escape":
                    if (plrRecord.getLastEscape().getTime() < 1451606400000L)
                        return "";
                    return (new SimpleDateFormat("MMM dd HH:mm").format(plrRecord.getLastEscape()).toString());
                case "user_lastspotted_name":
                    if (plrRecord.getLastSpottedBy() == null)
                        return "";
                    return plrRecord.getLastSpottedBy().getFullName();
                case "user_lastspotted_time":
                    if (plrRecord.getLastSpottedTime().getTime() < 1451606400000L)
                        return "";
                    return (new SimpleDateFormat("MMM dd HH:mm").format(plrRecord.getLastSpottedTime()).toString());
                default:
                    break;
            }
            return "";
        } catch (Exception err) {
            return "";
        }
    }
}
