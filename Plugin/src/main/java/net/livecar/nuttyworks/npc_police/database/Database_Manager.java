package net.livecar.nuttyworks.npc_police.database;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.database.Database_QueuedRequest.RequestType;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class Database_Manager {
    private ArrayBlockingQueue<Database_QueuedRequest> processingRequests;
    private ArrayBlockingQueue<Database_QueuedRequest> returnedRequests;
    private Thread databaseThread;
    private Database_Interface getDatabaseManager;
    private int queueMonitorID = -1;

    private NPC_Police getStorageReference;

    public Database_Manager(NPC_Police policeRef) {
        this.getStorageReference = policeRef;
        processingRequests = new ArrayBlockingQueue<>(500);
        returnedRequests = new ArrayBlockingQueue<>(500);

        // What datastorage does the config use
        switch (policeRef.getDefaultConfig.getString("database.type", "sqlite")) {
            case "sqlite":
                getStorageReference.getMessageManager.debugMessage(Level.FINE, "SQLite");
                getDatabaseManager = new Database_SqlLite(policeRef, processingRequests, returnedRequests);
                databaseThread = new Thread((Runnable) this.getDatabaseManager);
                databaseThread.setName("NPC_Police-SQLite DB");
                break;
            case "mysql":
                getStorageReference.getMessageManager.debugMessage(Level.FINE, "MySql");
                getDatabaseManager = new Database_MySql(policeRef, processingRequests, returnedRequests);
                databaseThread = new Thread((Runnable) this.getDatabaseManager);
                databaseThread.setName("NPC_Police-MySql DB");
                break;
            default:
                break;
        }
    }

    public boolean startDatabase() {
        if (this.databaseThread == null)
            return false;

        getStorageReference.getMessageManager.debugMessage(Level.FINE, "");
        this.databaseThread.start();

        queueMonitorID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                getStorageReference.pluginInstance, () -> {
                    try {
                        processReturnQueue();
                    } catch (Exception e) {
                    }
                }, 30L, 5L
        );

        return true;
    }

    public boolean stopDatabase() {
        if (queueMonitorID != -1)
            Bukkit.getServer().getScheduler().cancelTask(queueMonitorID);

        getStorageReference.getMessageManager.debugMessage(Level.FINE, "");

        if (this.databaseThread == null)
            return false;

        getStorageReference.getMessageManager.consoleMessage("console_messages.plugin_savingdata");

        // This can pause the server (but it's closing down anyway, who cares)
        int loopCounter = 0;
        while (!processingRequests.isEmpty()) {
            this.databaseThread.interrupt();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
            loopCounter++;
            if (loopCounter > 50)
                break;
        }

        loopCounter = 0;
        while (!this.getDatabaseManager.isSleeping()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
            loopCounter++;
            if (loopCounter > 50)
                break;

        }

        this.getDatabaseManager.closeConnections();
        return true;
    }

    public void queueLoadPlayerRequest(UUID playerUUID) {
        getStorageReference.getMessageManager.debugMessage(Level.FINE,processingRequests.size() + "|" + playerUUID.toString());
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LOAD_USER, new Arrest_Record(getStorageReference, playerUUID, 0.0D, new HashMap<String, Integer>(), CURRENT_STATUS.FREE, CURRENT_STATUS.FREE)));
    }

    public void queueSavePlayerRequest(final Arrest_Record plrRecord) {
        getStorageReference.getMessageManager.debugMessage(Level.FINE, processingRequests.size() + "|" +plrRecord.getPlayerUUID().toString());
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.SAVE_USER, plrRecord));
    }

    public void queueRemovePlayerRequest(final Arrest_Record plrRecord) {
        getStorageReference.getMessageManager.debugMessage(Level.FINE, processingRequests.size() + "|" +plrRecord.getPlayerUUID().toString());
        plrRecord.unRegisterEvents();
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.REMOVE_USER, plrRecord));
    }

    public void requestUpdatedStats() {
        getStorageReference.getMessageManager.debugMessage(Level.FINE, processingRequests.size() + "|" +"");
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_TOTALBOUNTIES));
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_MOSTMURDERS));
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_MOSTESCAPES));
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_LASTESCAPES));
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_LASTARRESTS));
        addProcessingRequestToQueue(new Database_QueuedRequest(RequestType.LEADERHEADS_BOUNTIES));
    }


    private void addProcessingRequestToQueue(final Database_QueuedRequest queueRequest) {
        try {
            processingRequests.put(queueRequest);
            if (this.getDatabaseManager.isSleeping()) {
                this.databaseThread.interrupt();
            }
            return;
        } catch (InterruptedException e) {
            // Why would this get interrupted??
        }

        // The process is backing up, launch a runnable to see if we can load it
        // later
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                getStorageReference.pluginInstance, () -> addProcessingRequestToQueue(queueRequest), 5L
        );
    }


    private void processReturnQueue() {
        if (this.returnedRequests.isEmpty())
            return;
        while (true) {
            if (this.returnedRequests.isEmpty())
                return;

            Database_QueuedRequest newRequest = null;

            try {
                newRequest = this.returnedRequests.take();
            } catch (InterruptedException e) {
            }

            switch (newRequest.getRequestType()) {
                case LEADERHEADS_BOUNTIES:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("BOUNTIES", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_TOTALBOUNTIES:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("TOTALBOUNTIES", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_MOSTARRESTS:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("MOSTARRESTS", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_MOSTESCAPES:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("MOSTESCAPES", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_MOSTMURDERS:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("MOSTMURDERS", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_LASTARRESTS:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("LASTARRESTS", newRequest.getLeaderHeadsResults());
                    break;
                case LEADERHEADS_LASTESCAPES:
                    getStorageReference.getLeaderHeadsPlugin.setLeaderBoardStats("LASTESCAPES", newRequest.getLeaderHeadsResults());
                    break;
                case LOAD_USER:
                    if (newRequest.getPlayerRecord() != null) {
                        Arrest_Record plrRecord = newRequest.getPlayerRecord();
                        if (!plrRecord.currentJailName.trim().equals(""))
                            plrRecord.currentJail = getStorageReference.getJailManager.getJailByName(plrRecord.currentJailName);
                        getStorageReference.getPlayerManager.addPlayerRecord(plrRecord);
                        plrRecord.registerEvents();

                        plrRecord.playerKickCheck(plrRecord.getPlayer().getWorld(), true);
                    }
                    break;
                case SAVE_USER:
                    break;
                case REMOVE_USER:
                    getStorageReference.getPlayerManager.removePlayerRecord(newRequest.getPlayerRecord());
                    break;
                default:
                    break;

            }

        }
    }

}
