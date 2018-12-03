package net.livecar.nuttyworks.npc_police.database;

import me.robin.leaderheads.api.LeaderHeadsAPI;
import net.livecar.nuttyworks.npc_police.NPC_Police;
import net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS;
import net.livecar.nuttyworks.npc_police.api.Enumerations.JAILED_BOUNTY;
import net.livecar.nuttyworks.npc_police.api.Wanted_Information;
import net.livecar.nuttyworks.npc_police.database.Database_QueuedRequest.RequestType;
import net.livecar.nuttyworks.npc_police.players.Arrest_Record;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class Database_MySql extends Thread implements Database_Interface {
    private Connection dbConnection = null;
    private ArrayBlockingQueue<Database_QueuedRequest> processingRequests;
    private ArrayBlockingQueue<Database_QueuedRequest> returnedRequests;
    private boolean sleeping = false;

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbLogin;
    private String dbPass;
    private String dbTablePrefix;
    private NPC_Police getStorageReference;

    public Database_MySql(NPC_Police policeRef, ArrayBlockingQueue<Database_QueuedRequest> processQueue, ArrayBlockingQueue<Database_QueuedRequest> resultQueue) {
        processingRequests = processQueue;
        returnedRequests = resultQueue;
        getStorageReference = policeRef;

        dbHost = getStorageReference.getDefaultConfig.getString("database.host");
        dbPort = getStorageReference.getDefaultConfig.getString("database.port");
        dbName = getStorageReference.getDefaultConfig.getString("database.name");
        dbLogin = getStorageReference.getDefaultConfig.getString("database.login");
        dbPass = getStorageReference.getDefaultConfig.getString("database.password");
        dbTablePrefix = getStorageReference.getDefaultConfig.getString("database.table_prefix");
    }

    @Override
    public boolean isSleeping() {
        return sleeping;
    }

    public void closeConnections() {
        close(dbConnection);
        dbConnection = null;
    }

    @Override
    public void openDatabase() {
        if (dbConnection == null) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
                try (Statement sqlStatement = dbConnection.createStatement()) {
                    sqlStatement.setQueryTimeout(2);
                    try (ResultSet results = sqlStatement.executeQuery("SHOW TABLES LIKE '" + dbTablePrefix + "_Players'")) {
                        if (!results.isBeforeFirst()) {
                            try (Statement sqlStmtInsert = dbConnection.createStatement()) {
                                sqlStmtInsert.setQueryTimeout(30);
                                sqlStmtInsert.executeUpdate("Create table " + dbTablePrefix + "_Players (player_id varchar(40),player_name varchar(25), last_warning datetime, last_arrest datetime, last_escape datetime, bounty int, totalbounty int, pendingtime int, last_jail_location varchar(100), count_jailed int, count_arrested int, count_escaped int, count_murder int, count_theft int, current_status varchar(25), prior_status varchar(25), lockedinventory text, wantedlevel text)");
                                close(sqlStmtInsert);
                            }
                        }
                    }
                    close(sqlStatement);
                }

                try (Statement sqlStatement = dbConnection.createStatement()) {
                    sqlStatement.setQueryTimeout(2);
                    try (ResultSet results = sqlStatement.executeQuery("SHOW TABLES LIKE '" + dbTablePrefix + "_WantedReasons'")) {
                        if (!results.isBeforeFirst()) {
                            try (Statement sqlStmtInsert = dbConnection.createStatement()) {
                                sqlStmtInsert.setQueryTimeout(30);
                                sqlStatement.executeUpdate("Create table " + dbTablePrefix + "_WantedReasons (player_id varchar(40), wanted_reason varchar(20), plugin_reason varchar(50), attacked_name varchar(30), witness_name varchar(40), server_name varchar(40), bounty decimal(15,4), offense_count int, offense_date BIGINT)");
                                close(sqlStmtInsert);
                            }
                        }
                    }
                    close(sqlStatement);
                }
            } catch (SQLException e1) {
                getStorageReference.getMessageManager.logToConsole("Database Issue: " + e1.getMessage());
            }

            try {
                if (!this.columnExists(dbTablePrefix + "_Players", "lockedinventory")) {
                    try (Statement sqlStatement = dbConnection.createStatement()) {
                        sqlStatement.setQueryTimeout(5);
                        sqlStatement.executeUpdate("ALTER TABLE " + dbTablePrefix + "_Players ADD COLUMN lockedinventory text");
                        close(sqlStatement);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (!this.columnExists(dbTablePrefix + "_Players", "wantedlevel")) {
                    try (Statement sqlStatement = dbConnection.createStatement()) {
                        sqlStatement.setQueryTimeout(5);
                        sqlStatement.executeUpdate("ALTER TABLE " + dbTablePrefix + "_Players ADD COLUMN wantedlevel text");
                        close(sqlStatement);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                if (!this.columnExists(dbTablePrefix + "_Players", "pendingtime")) {
                    try (Statement sqlStatement = dbConnection.createStatement()) {
                        sqlStatement.setQueryTimeout(5);
                        sqlStatement.executeUpdate("ALTER TABLE " + dbTablePrefix + "_Players ADD COLUMN pendingtime int");
                        close(sqlStatement);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (!this.columnExists(dbTablePrefix + "_Players", "jailed_until")) {
                    try (Statement sqlStatement = dbConnection.createStatement()) {
                        sqlStatement.setQueryTimeout(5);
                        sqlStatement.executeUpdate("ALTER TABLE " + dbTablePrefix + "_Players ADD COLUMN jailed_until datetime");
                        close(sqlStatement);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (!this.columnExists(dbTablePrefix + "_Players", "count_jailed")) {
                    try (Statement sqlStatement = dbConnection.createStatement()) {
                        sqlStatement.setQueryTimeout(5);
                        sqlStatement.executeUpdate("ALTER TABLE " + dbTablePrefix + "_Players ADD COLUMN count_jailed int");
                        close(sqlStatement);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            if (!this.isDbConnected()) {
                try {
                    dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
                } catch (SQLException e) {
                    getStorageReference.getMessageManager.logToConsole("Database Issue: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void run() {
        openDatabase();
        while (true) {
            try {
                // Process the queue if there are any requests pending and then
                // wait.
                sleeping = false;
                processQueue();
            } catch (InterruptedException e) {
                // Wakeup call or activity requested
            }
            if (processingRequests == null) {
                return;
            }
        }
    }

    private void processQueue() throws InterruptedException {
        synchronized (processingRequests) {
            if (processingRequests.isEmpty()) {
                sleeping = true;
                processingRequests.wait();
            }

            if (processingRequests != null) {
                while (!processingRequests.isEmpty()) {
                    Database_QueuedRequest newRequest = processingRequests.take();
                    switch (newRequest.getRequestType()) {
                        case LEADERHEADS_BOUNTIES:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_BOUNTIES, this.getCurrentBounties()));
                            break;
                        case LEADERHEADS_TOTALBOUNTIES:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_TOTALBOUNTIES, this.getTotalBounties()));
                            break;
                        case LEADERHEADS_MOSTARRESTS:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_MOSTARRESTS, this.getMostArrests()));
                            break;
                        case LEADERHEADS_MOSTESCAPES:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_MOSTESCAPES, this.getMostEscapes()));
                            break;
                        case LEADERHEADS_MOSTMURDERS:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_MOSTMURDERS, this.getMostMurders()));
                            break;
                        case LEADERHEADS_LASTARRESTS:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_LASTARRESTS, this.getLastArrests()));
                            break;
                        case LEADERHEADS_LASTESCAPES:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LEADERHEADS_LASTESCAPES, this.getLastEscapes()));
                            break;
                        case LOAD_USER:
                            returnedRequests.put(new Database_QueuedRequest(RequestType.LOAD_USER, getUserData(newRequest.getPlayerRecord().getPlayer())));
                            break;
                        case SAVE_USER:
                            saveUserData(newRequest.getPlayerRecord());
                            break;
                        case REMOVE_USER:
                            saveUserData(newRequest.getPlayerRecord());
                            returnedRequests.put(new Database_QueuedRequest(RequestType.REMOVE_USER, newRequest.getPlayerRecord()));
                            break;
                        default:
                            break;

                    }
                }
            }
        }
    }

    @Override
    public Arrest_Record getUserData(OfflinePlayer player) {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        HashMap<String, Integer> statistics = new HashMap<String, Integer>();
        Arrest_Record plrData = null;

        try (Statement sqlStatement = dbConnection.createStatement()) {
            sqlStatement.setQueryTimeout(3);
            try (ResultSet results = sqlStatement.executeQuery("SELECT last_Warning, last_arrest, last_escape, bounty, totalbounty, last_jail_location, count_jailed, count_arrested, count_escaped, count_murder,count_theft, current_status,prior_status, lockedInventory, wantedlevel, pendingtime, jailed_until FROM " + dbTablePrefix + "_Players WHERE player_id='" + player.getUniqueId().toString() + "'")) {

                while (results.next()) {
                    statistics.put("JAILED", results.getInt("count_jailed"));
                    statistics.put("ARRESTED", results.getInt("count_arrested"));
                    statistics.put("ESCAPED", results.getInt("count_escaped"));
                    statistics.put("MURDER", results.getInt("count_murder"));
                    statistics.put("THEFT", results.getInt("count_theft"));

                    plrData = new Arrest_Record(getStorageReference, player.getUniqueId(), results.getDouble("totalbounty"), statistics, net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS.valueOf(results.getString("current_status")), net.livecar.nuttyworks.npc_police.api.Enumerations.CURRENT_STATUS.valueOf(results.getString("prior_status")));
                    plrData.currentJailName = results.getString("last_jail_location");
                    plrData.setLastArrest(new Date(results.getDate("last_arrest").getTime()));
                    plrData.setLastEscape(new Date(results.getDate("last_escape").getTime()));
                    plrData.setLastWarning(new Date(results.getDate("last_warning").getTime()));
                    plrData.setJailedExpires(new Date(results.getDate("jailed_until").getTime()));
                    plrData.setTime(results.getInt("pendingtime"));
                    plrData.setWantedLevel(net.livecar.nuttyworks.npc_police.api.Enumerations.WANTED_LEVEL.valueOf(results.getString("wantedlevel")), JAILED_BOUNTY.MANUAL, false);
                    plrData.setBounty(results.getInt("bounty"));
                    plrData.setLastCheck(new Date());

                    if (results.getString("lockedinventory") != null) {
                        String res = results.getString("lockedinventory");
                        ItemStack[] lockedInventory = getStorageReference.getUtilities.deserialzeItemStack(res);
                        plrData.addToLockedInventory(lockedInventory);
                    }
                }
                close(results);
                close(sqlStatement);

            } catch (Exception e) {
            }
        } catch (Exception e) {
        }

        if (plrData != null) {
            try (Statement sqlStatement = dbConnection.createStatement()) {
                sqlStatement.setQueryTimeout(3);
                try (ResultSet results = sqlStatement.executeQuery("select wanted_reason, plugin_reason, attacked_name, witness_name, server_name, bounty, offense_count, offense_date FROM " + dbTablePrefix + "_WantedReasons WHERE player_id='" + player.getUniqueId().toString() + "'")) {

                    while (results.next()) {
                        Wanted_Information wanted = new Wanted_Information(results.getString("witness_name"), results.getString("attacked_name"), results.getString("server_name"), results.getString("wanted_reason"), results.getDouble("bounty"), new Date(results.getLong("offense_date")), results.getInt("offense_count"));
                        plrData.addNewWanted(wanted);
                    }
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
        } else {
            statistics.put("ARRESTED", 0);
            statistics.put("JAILED", 0);
            statistics.put("ESCAPED", 0);
            statistics.put("MURDER", 0);
            statistics.put("THEFT", 0);

            // No current user?
            plrData = new Arrest_Record(getStorageReference, player.getUniqueId(), 0.0D, statistics, CURRENT_STATUS.FREE, CURRENT_STATUS.FREE);
            plrData.currentJailName = "";
            plrData.setLastArrest(new Date(0));
            plrData.setLastEscape(new Date(0));
            plrData.setLastWarning(new Date(0));

        }

        return plrData;
    }

    @Override
    public void saveUserData(Arrest_Record playerData) {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return;
            }
        }

        try (Statement sqlStatement = dbConnection.createStatement()) {
            sqlStatement.setQueryTimeout(2);
            try (ResultSet results = sqlStatement.executeQuery("SELECT player_id FROM " + dbTablePrefix + "_Players WHERE player_id='" + playerData.getPlayerUUID().toString() + "'")) {
                if (!results.isBeforeFirst()) {
                    // Need to create a default record.
                    try (PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO " + dbTablePrefix + "_Players (Player_id,last_warning,last_arrest,last_escape,bounty,totalbounty,last_jail_location,count_jailed,count_arrested,count_escaped,count_murder,count_theft,current_status,prior_status,player_name,lockedinventory,Wantedlevel,pendingtime,jailed_until) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                        preparedStatement.setString(1, playerData.getPlayerUUID().toString());
                        preparedStatement.setDate(2, new java.sql.Date(0));
                        preparedStatement.setDate(3, new java.sql.Date(0));
                        preparedStatement.setDate(4, new java.sql.Date(0));

                        preparedStatement.setDouble(5, 0.0D);
                        preparedStatement.setDouble(6, 0.0D);

                        preparedStatement.setString(7, "");


                        preparedStatement.setInt(8, 0);
                        preparedStatement.setInt(9, 0);
                        preparedStatement.setInt(10, 0);
                        preparedStatement.setInt(11, 0);
                        preparedStatement.setInt(12, 0);

                        preparedStatement.setString(13, "");
                        preparedStatement.setString(14, "");
                        preparedStatement.setString(15, playerData.getOfflinePlayer().getName());
                        preparedStatement.setString(16, "");
                        preparedStatement.setString(17, "NONE");

                        preparedStatement.setInt(18, 0);
                        preparedStatement.setDate(19, new java.sql.Date(0));
                        preparedStatement.executeUpdate();
                        close(preparedStatement);
                    }
                }
                close(results);

                // Update
                try (PreparedStatement preparedStatement = dbConnection.prepareStatement("UPDATE " + dbTablePrefix + "_Players " + " SET last_Warning=?," + " last_arrest=?," + " last_escape=?," + " bounty=?," + " totalbounty=?," + " last_jail_location=?," + " count_jailed=?," + " count_arrested=?," + " count_escaped=?," + " count_murder=?," + " count_theft=?," + " current_status=?," + " prior_status=?," + " player_name=?," + " lockedinventory=?, " + " wantedlevel=?, " + " pendingtime=?, " + " jailed_until=? " + " " + "WHERE" + " player_id=?")) {
                    preparedStatement.setDate(1, new java.sql.Date(playerData.getLastWarning().getTime()));
                    preparedStatement.setDate(2, new java.sql.Date(playerData.getLastArrest().getTime()));
                    preparedStatement.setDate(3, new java.sql.Date(playerData.getLastEscape().getTime()));

                    preparedStatement.setDouble(4, playerData.getBounty());
                    preparedStatement.setDouble(5, playerData.getTotalBounty());
                    preparedStatement.setString(6, playerData.currentJail == null ? "" : playerData.getLastJailedLocation().jailName);

                    preparedStatement.setInt(7, playerData.getStat("JAILED"));
                    preparedStatement.setInt(8, playerData.getStat("ARRESTED"));
                    preparedStatement.setInt(9, playerData.getStat("ESCAPED"));
                    preparedStatement.setInt(10, playerData.getStat("MURDER"));
                    preparedStatement.setInt(11, playerData.getStat("THEFT"));

                    preparedStatement.setString(12, playerData.getCurrentStatus().toString());
                    preparedStatement.setString(13, playerData.getPriorStatus().toString());

                    preparedStatement.setString(14, playerData.getOfflinePlayer().getName());
                    preparedStatement.setString(15, playerData.serializeLockedInventory());

                    preparedStatement.setString(16, playerData.getWantedLevel().toString());
                    preparedStatement.setInt(17, playerData.getTime());
                    preparedStatement.setDate(18, new java.sql.Date(playerData.getJailedExpires().getTime()));

                    preparedStatement.setString(19, playerData.getPlayerUUID().toString());
                    preparedStatement.executeUpdate();

                    close(preparedStatement);

                }
                sqlStatement.executeUpdate("delete from " + dbTablePrefix + "_WantedReasons WHERE player_id='" + playerData.getPlayerUUID().toString() + "'");

                for (Wanted_Information wanted : playerData.getWantedReasons()) {
                    try (PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO " + dbTablePrefix + "_WantedReasons" + "(player_id,wanted_reason,plugin_reason,attacked_name,witness_name,server_name,bounty,offense_count,offense_date)" + " VALUES(?,?,?,?,?,?,?,?,?)")) {
                        preparedStatement.setString(1, playerData.getPlayerUUID().toString());

                        preparedStatement.setString(2, wanted.getWantedReason());
                        preparedStatement.setString(3, "");
                        preparedStatement.setString(4, wanted.getAttackedName());
                        preparedStatement.setString(5, wanted.getWitnessName());
                        preparedStatement.setString(6, wanted.getServer());

                        preparedStatement.setDouble(7, wanted.getBountyValue());
                        preparedStatement.setInt(8, wanted.getAllOffenseDates().size());
                        preparedStatement.setDate(9, new java.sql.Date(wanted.getFirstOffenseDate().getTime()));
                        preparedStatement.executeUpdate();

                        close(preparedStatement);
                    }
                }

            } catch (Exception err) {
                err.printStackTrace();
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getLastArrests() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,last_arrest FROM " + dbTablePrefix + "_Players Where last_arrest > '2016-01-01' Order by last_arrest desc Limit 30");
                LinkedHashMap<String, Double> returnList = new LinkedHashMap<String, Double>();

                while (results.next()) {
                    long timeDiff = new Date().getTime() - results.getTimestamp("last_arrest").getTime();
                    returnList.put(results.getString("player_name"), (timeDiff / (60.0 * 1000.0)));
                }

                List<Entry<?, Double>> returnList1 = new ArrayList<Entry<?, Double>>();
                for (Entry<?, Double> entry : returnList.entrySet()) {
                    returnList1.add(entry);
                }
                close(results);
                close(sqlStatement);
                return returnList1;
            } catch (Exception e) {
                close(sqlStatement);
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getLastEscapes() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,last_escape FROM " + dbTablePrefix + "_Players Where last_arrest > '2016-01-01' Order by last_escape desc Limit 30");
                LinkedHashMap<String, Double> returnList = new LinkedHashMap<String, Double>();

                while (results.next()) {
                    long timeDiff = new Date().getTime() - results.getTimestamp("last_escape").getTime();
                    returnList.put(results.getString("player_name"), (timeDiff / (60.0 * 1000.0)));
                }

                List<Entry<?, Double>> returnList1 = new ArrayList<Entry<?, Double>>();
                for (Entry<?, Double> entry : returnList.entrySet()) {
                    returnList1.add(entry);
                }
                close(results);
                close(sqlStatement);
                return returnList1;
            } catch (Exception e) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }

        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getMostMurders() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,count_murder FROM " + dbTablePrefix + "_Players Order by count_murder desc Limit 30");
                HashMap<String, Double> returnList = new HashMap<String, Double>();
                while (results.next()) {
                    returnList.put(results.getString("player_name"), results.getDouble("count_murder"));
                }
                close(results);
                close(sqlStatement);
                return LeaderHeadsAPI.sortMap(returnList);
            } catch (Exception e) {
                //
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getMostEscapes() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,count_escaped FROM " + dbTablePrefix + "_Players Order by count_escaped desc Limit 30");
                HashMap<String, Double> returnList = new HashMap<String, Double>();
                while (results.next()) {
                    returnList.put(results.getString("player_name"), results.getDouble("count_escaped"));
                }
                close(results);
                close(sqlStatement);
                return LeaderHeadsAPI.sortMap(returnList);
            } catch (Exception e) {
                //
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getMostArrests() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,count_arrested FROM " + dbTablePrefix + "_Players Order by count_arrested desc Limit 30");
                HashMap<String, Double> returnList = new HashMap<String, Double>();
                while (results.next()) {
                    returnList.put(results.getString("player_name"), results.getDouble("count_arrested"));
                }
                close(results);
                close(sqlStatement);
                return LeaderHeadsAPI.sortMap(returnList);
            } catch (Exception e) {
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getCurrentBounties() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,bounty FROM " + dbTablePrefix + "_Players Order by bounty desc Limit 30");
                HashMap<String, Double> returnList = new HashMap<String, Double>();
                while (results.next()) {
                    returnList.put(results.getString("player_name"), results.getDouble("bounty"));
                }
                close(results);
                close(sqlStatement);
                return LeaderHeadsAPI.sortMap(returnList);
            } catch (Exception e) {
                //
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Entry<?, Double>> getTotalBounties() {
        if (!this.isDbConnected()) {
            try {
                dbConnection = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + dbPort + "/" + dbName, dbLogin, dbPass);
            } catch (SQLException e) {
                // Problems!
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[NPC POLICE] Failure creating database connection");
                return null;
            }
        }

        try {
            Statement sqlStatement = dbConnection.createStatement();
            try {
                sqlStatement.setQueryTimeout(30);
                ResultSet results = sqlStatement.executeQuery("SELECT player_ID,player_name,totalbounty FROM " + dbTablePrefix + "_Players Order by totalbounty desc Limit 30");
                HashMap<String, Double> returnList = new HashMap<String, Double>();
                while (results.next()) {
                    returnList.put(results.getString("player_name"), results.getDouble("totalbounty"));
                }
                close(results);
                close(sqlStatement);
                return LeaderHeadsAPI.sortMap(returnList);
            } catch (Exception e) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }

        return null;
    }

    private boolean isDbConnected() {
        try {
            if (dbConnection == null)
                return false;
            if (dbConnection.isValid(1))
                return true;
        } catch (SQLException e) {
        }
        return false;
    }

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception excp) {
                // Nothing
            }
        }
    }

    private void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception excp) {
            }
        }
    }

    private void close(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Exception excp) {
            }
        }
    }

    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception excp) {
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) {
        if (dbConnection == null) {
            openDatabase();
        }
        if (dbConnection == null) {
            return false;
        }

        try (Statement sqlStatement = dbConnection.createStatement()) {
            sqlStatement.setQueryTimeout(1);
            try (ResultSet results = sqlStatement.executeQuery("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + this.dbName + "' AND TABLE_NAME = '" + tableName + "' AND COLUMN_NAME = '" + columnName + "'")) {
                while (results.next()) {
                    if (results.getString("COLUMN_NAME").equalsIgnoreCase(columnName)) {
                        close(results);
                        close(sqlStatement);
                        return true;
                    }

                }
            }
        } catch (Exception excp) {
            getStorageReference.getMessageManager.logToConsole(excp.toString());
        }
        return false;
    }
}
