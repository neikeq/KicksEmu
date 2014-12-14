package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.SqlUtils;

public class ClubInfo {

    private static final String table = "clubs";

    public static String getName(int id) {
        return SqlUtils.getString("name", table, id);
    }

    public static int getUniformHomeShirts(int id) {
        return SqlUtils.getInt("uniform_home_shirts", table, id);
    }

    public static int getUniformHomePants(int id) {
        return SqlUtils.getInt("uniform_home_pants", table, id);
    }

    public static int getUniformHomeSocks(int id) {
        return SqlUtils.getInt("uniform_home_socks", table, id);
    }

    public static int getUniformHomeWrist(int id) {
        return SqlUtils.getInt("uniform_home_wrist", table, id);
    }

    public static int getUniformAwayShirts(int id) {
        return SqlUtils.getInt("uniform_away_shirts", table, id);
    }

    public static int getUniformAwayPants(int id) {
        return SqlUtils.getInt("uniform_away_pants", table, id);
    }

    public static int getUniformAwaySocks(int id) {
        return SqlUtils.getInt("uniform_away_socks", table, id);
    }

    public static int getUniformAwayWrist(int id) {
        return SqlUtils.getInt("uniform_away_wrist", table, id);
    }

    public static void setName(String value, int id) {
        SqlUtils.setString("name", value, table, id);
    }

    public static void setUniformHomeShirts(int value, int id) {
        SqlUtils.setInt("uniform_home_shirts", value, table, id);
    }

    public static void setUniformHomePants(int value, int id) {
        SqlUtils.setInt("uniform_home_pants", value, table, id);
    }

    public static void setUniformHomeSocks(int value, int id) {
        SqlUtils.setInt("uniform_home_socks", value, table, id);
    }

    public static void setUniformHomeWrist(int value, int id) {
        SqlUtils.setInt("uniform_home_wrist", value, table, id);
    }

    public static void setUniformAwayShirts(int value, int id) {
        SqlUtils.setInt("uniform_away_shirts", value, table, id);
    }

    public static void setUniformAwayPants(int value, int id) {
        SqlUtils.setInt("uniform_away_pants", value, table, id);
    }

    public static void setUniformAwaySocks(int value, int id) {
        SqlUtils.setInt("uniform_away_socks", value, table, id);
    }

    public static void setUniformAwayWrist(int value, int id) {
        SqlUtils.setInt("uniform_away_wrist", value, table, id);
    }
}
