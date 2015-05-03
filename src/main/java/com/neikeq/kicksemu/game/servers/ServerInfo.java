package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.storage.SqlUtils;

public class ServerInfo {

    private static final String TABLE = "servers";

    // Sql getters and setters

    public static short getFilter(int id) {
        return SqlUtils.getShort("filter", TABLE, id);
    }

    public static void setFilter(short filter, int id) {
        SqlUtils.setShort("filter", filter, TABLE, id);
    }

    public static String getName(int id) {
        return SqlUtils.getString("name", TABLE, id);
    }

    public static void setName(String name, int id) {
        SqlUtils.setString("name", name, TABLE, id);
    }

    public static String getAddress(int id) {
        return SqlUtils.getString("address", TABLE, id);
    }

    public static short getPort(int id) {
        return SqlUtils.getShort("port", TABLE, id);
    }

    public static short getMinLevel(int id) {
        return SqlUtils.getShort("min_level", TABLE, id);
    }

    public static short getMaxLevel(int id) {
        return SqlUtils.getShort("max_level", TABLE, id);
    }

    public static short getMaxUsers(int id) {
        return SqlUtils.getShort("max_users", TABLE, id);
    }

    public static short getConnectedUsers(int id) {
        return SqlUtils.getShort("connected_users", TABLE, id);
    }

    public static void setConnectedUsers(short connectedUsers, int id) {
        SqlUtils.setShort("connected_users", connectedUsers, TABLE, id);
    }

    public static boolean isOnline(int id) {
        return SqlUtils.getBoolean("online", TABLE, id);
    }

    public static void toggleOffline(int id) {
        SqlUtils.setBoolean("online", false, TABLE, id);
    }

    public static GameServerType getType(int id) {
        return GameServerType.valueOf(SqlUtils.getString("type", TABLE, id));
    }
}
