package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.storage.SqlUtils;

public class ServerInfo {

    private static final String table = "servers";

    // Sql getters and setters

    public static short getFilter(int id) {
        return SqlUtils.getShort("filter", table, id);
    }

    public static void setFilter(short filter, int id) {
        SqlUtils.setShort("filter", filter, table, id);
    }

    public static String getName(int id) {
        return SqlUtils.getString("name", table, id);
    }

    public static void setName(String name, int id) {
        SqlUtils.setString("name", name, table, id);
    }

    public static String getAddress(int id) {
        return SqlUtils.getString("address", table, id);
    }

    public static void setAddress(String ip, int id) {
        SqlUtils.setString("address", ip, table, id);
    }

    public static short getPort(int id) {
        return SqlUtils.getShort("port", table, id);
    }

    public static void setPort(short port, int id) {
        SqlUtils.setShort("port", port, table, id);
    }

    public static short getMinLevel(int id) {
        return SqlUtils.getShort("min_level", table, id);
    }

    public static void setMinLevel(short minLevel, int id) {
        SqlUtils.setShort("min_level", minLevel, table, id);
    }

    public static short getMaxLevel(int id) {
        return SqlUtils.getShort("max_level", table, id);
    }

    public static void setMaxLevel(short maxLevel, int id) {
        SqlUtils.setShort("max_level", maxLevel, table, id);
    }

    public static short getMaxUsers(int id) {
        return SqlUtils.getShort("max_users", table, id);
    }

    public static void setMaxUsers(short maxConnectedUsers, int id) {
        SqlUtils.setShort("max_users", maxConnectedUsers, table, id);
    }

    public static short getConnectedUsers(int id) {
        return SqlUtils.getShort("connected_users", table, id);
    }

    public static void setConnectedUsers(short connectedUsers, int id) {
        SqlUtils.setShort("connected_users", connectedUsers, table, id);
    }

    public static boolean isOnline(int id) {
        return SqlUtils.getBoolean("online", table, id);
    }

    public static void setOnline(boolean status, int id) {
        SqlUtils.setBoolean("online", status, table, id);
    }

    public static GameServerType getType(int id) {
        return GameServerType.valueOf(SqlUtils.getString("type", table, id));
    }

    public static void setType(GameServerType type, int id) {
        SqlUtils.setString("type", type.name(), table, id);
    }

    public static int getExpFactor(int id) {
        return SqlUtils.getInt("exp_factor", table, id);
    }

    public static void setExpFactor(int expFactor, int id) {
        SqlUtils.setInt("exp_factor", expFactor, table, id);
    }

    public static int getPointFactor(int id) {
        return SqlUtils.getInt("point_factor", table, id);
    }

    public static void setPointFactor(int pointFactor, int id) {
        SqlUtils.setInt("point_factor", pointFactor, table, id);
    }

    public static int getKashFactor(int id) {
        return SqlUtils.getInt("kash_factor", table, id);
    }

    public static void setKashFactor(int kashFactor, int id) {
        SqlUtils.setInt("kash_factor", kashFactor, table, id);
    }

    public static boolean getPracticeRewards(int id) {
        return SqlUtils.getBoolean("practice_rewards", table, id);
    }

    public static void setPracticeRewards(boolean practiceRewards, int id) {
        SqlUtils.setBoolean("practice_rewards", practiceRewards, table, id);
    }
}
