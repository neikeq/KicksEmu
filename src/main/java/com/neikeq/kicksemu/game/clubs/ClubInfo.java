package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.SqlUtils;

import java.sql.Connection;

public class ClubInfo {

    private static final String table = "clubs";

    public static String getName(int id, Connection ... con) {
        return SqlUtils.getString("name", table, id, con);
    }

    public static int getUniformHomeShirts(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_home_shirts", table, id, con);
    }

    public static int getUniformHomePants(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_home_pants", table, id, con);
    }

    public static int getUniformHomeSocks(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_home_socks", table, id, con);
    }

    public static int getUniformHomeWrist(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_home_wrist", table, id, con);
    }

    public static int getUniformAwayShirts(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_away_shirts", table, id, con);
    }

    public static int getUniformAwayPants(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_away_pants", table, id, con);
    }

    public static int getUniformAwaySocks(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_away_socks", table, id, con);
    }

    public static int getUniformAwayWrist(int id, Connection ... con) {
        return SqlUtils.getInt("uniform_away_wrist", table, id, con);
    }

    public static void setName(String value, int id, Connection ... con) {
        SqlUtils.setString("name", value, table, id, con);
    }

    public static void setUniformHomeShirts(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_home_shirts", value, table, id, con);
    }

    public static void setUniformHomePants(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_home_pants", value, table, id, con);
    }

    public static void setUniformHomeSocks(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_home_socks", value, table, id, con);
    }

    public static void setUniformHomeWrist(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_home_wrist", value, table, id, con);
    }

    public static void setUniformAwayShirts(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_away_shirts", value, table, id, con);
    }

    public static void setUniformAwayPants(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_away_pants", value, table, id, con);
    }

    public static void setUniformAwaySocks(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_away_socks", value, table, id, con);
    }

    public static void setUniformAwayWrist(int value, int id, Connection ... con) {
        SqlUtils.setInt("uniform_away_wrist", value, table, id, con);
    }
}
