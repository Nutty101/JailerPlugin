package net.livecar.nuttyworks.npc_police.database;

import net.livecar.nuttyworks.npc_police.players.Arrest_Record;

import java.util.List;
import java.util.Map.Entry;

public class Database_QueuedRequest {

    private Arrest_Record playerRecord;
    private RequestType requestType;
    private List<Entry<?, Double>> leaderHeadsResults;
    public Database_QueuedRequest(RequestType requestType) {
        this.requestType = requestType;
    }

    public Database_QueuedRequest(RequestType requestType, Arrest_Record playerRecord) {
        this.requestType = requestType;
        this.playerRecord = playerRecord;
    }

    public Database_QueuedRequest(RequestType requestType, List<Entry<?, Double>> leaderHeadsResults) {
        this.requestType = requestType;
        this.leaderHeadsResults = leaderHeadsResults;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Arrest_Record getPlayerRecord() {
        return playerRecord;
    }

    public List<Entry<?, Double>> getLeaderHeadsResults() {
        return leaderHeadsResults;
    }

    public enum RequestType {
        LOAD_USER, SAVE_USER, REMOVE_USER, SHUTDOWN, LEADERHEADS_BOUNTIES, LEADERHEADS_LASTARRESTS, LEADERHEADS_LASTESCAPES, LEADERHEADS_MOSTARRESTS, LEADERHEADS_MOSTMURDERS, LEADERHEADS_MOSTESCAPES, LEADERHEADS_TOTALBOUNTIES
    }
}
