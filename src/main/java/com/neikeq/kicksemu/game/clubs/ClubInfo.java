package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.SqlUtils;

public class ClubInfo {

    private final int id;
    private final SqlUtils sqlUtils;

    public int getId() {
        return id;
    }

    public String getName() {
        return sqlUtils.getString("name");
    }

    public int getUniformHomeShirts() {
        return sqlUtils.getInt("uniform_home_shirts");
    }

    public int getUniformHomePants() {
        return sqlUtils.getInt("uniform_home_pants");
    }

    public int getUniformHomeSocks() {
        return sqlUtils.getInt("uniform_home_socks");
    }

    public int getUniformHomeWrist() {
        return sqlUtils.getInt("uniform_home_wrist");
    }

    public int getUniformAwayShirts() {
        return sqlUtils.getInt("uniform_away_shirts");
    }

    public int getUniformAwayPants() {
        return sqlUtils.getInt("uniform_away_pants");
    }

    public int getUniformAwaySocks() {
        return sqlUtils.getInt("uniform_away_socks");
    }

    public int getUniformAwayWrist() {
        return sqlUtils.getInt("uniform_away_wrist");
    }

    public void setName(String value) {
        sqlUtils.setString("name", value);
    }

    public void setUniformHomeShirts(int value) {
        sqlUtils.setInt("uniform_home_shirts", value);
    }

    public void setUniformHomePants(int value) {
        sqlUtils.setInt("uniform_home_pants", value);
    }

    public void setUniformHomeSocks(int value) {
        sqlUtils.setInt("uniform_home_socks", value);
    }

    public void setUniformHomeWrist(int value) {
        sqlUtils.setInt("uniform_home_wrist", value);
    }

    public void setUniformAwayShirts(int value) {
        sqlUtils.setInt("uniform_away_shirts", value);
    }

    public void setUniformAwayPants(int value) {
        sqlUtils.setInt("uniform_away_pants", value);
    }

    public void setUniformAwaySocks(int value) {
        sqlUtils.setInt("uniform_away_socks", value);
    }

    public void setUniformAwayWrist(int value) {
        sqlUtils.setInt("uniform_away_wrist", value);
    }

    public ClubInfo(int id) {
        this.id = id;
        this.sqlUtils = SqlUtils.forId(id, "clubs");
    }
}
