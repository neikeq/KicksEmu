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

    public static short getPort(int id) {
        return SqlUtils.getShort("port", table, id);
    }

    public static short getMinLevel(int id) {
        return SqlUtils.getShort("min_level", table, id);
    }

    public static short getMaxLevel(int id) {
        return SqlUtils.getShort("max_level", table, id);
    }

    public static short getMaxUsers(int id) {
        return SqlUtils.getShort("max_users", table, id);
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
}
