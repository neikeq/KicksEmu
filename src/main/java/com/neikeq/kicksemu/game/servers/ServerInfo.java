package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.storage.SqlUtils;

public class ServerInfo {

    private short id;

    private SqlUtils sqlUtils;

    // Sql getters and setters

    public short getId() {
        return id;
    }

    public short getFilter() {
        return sqlUtils.getShort("filter");
    }

    public void setFilter(short filter) {
        sqlUtils.setShort("filter", filter);
    }

    public String getName() {
        return sqlUtils.getString("name");
    }

    public void setName(String name) {
        sqlUtils.getString("name");
    }

    public String getAddress() {
        return sqlUtils.getString("address");
    }

    public void setAddress(String ip) {
        sqlUtils.setString("address", ip);
    }

    public short getPort() {
        return sqlUtils.getShort("port");
    }

    public void setPort(short port) {
        sqlUtils.setShort("port", port);
    }

    public short getMinLevel() {
        return sqlUtils.getShort("min_level");
    }

    public void setMinLevel(short minLevel) {
        sqlUtils.setShort("min_level", minLevel);
    }

    public short getMaxLevel() {
        return sqlUtils.getShort("max_level");
    }

    public void setMaxLevel(short maxLevel) {
        sqlUtils.setShort("max_level", maxLevel);
    }

    public short getMaxUsers() {
        return sqlUtils.getShort("max_users");
    }

    public void setMaxUsers(short maxConnectedUsers) {
        sqlUtils.setShort("max_users", maxConnectedUsers);
    }

    public short getConnectedUsers() {
        return sqlUtils.getShort("connected_users");
    }

    public void setConnectedUsers(short connectedUsers) {
        sqlUtils.setShort("connected_users", connectedUsers);
    }

    public boolean isOnline() {
        return sqlUtils.getBoolean("online");
    }

    public void setOnline(boolean status) {
        sqlUtils.setBoolean("online", status);
    }

    public GameServerType getType() {
        return GameServerType.valueOf(sqlUtils.getString("type"));
    }

    public void setType(GameServerType type) {
        sqlUtils.setString("type", type.name());
    }

    public int getExpFactor() {
        return sqlUtils.getInt("exp_factor");
    }

    public void setExpFactor(int expFactor) {
        sqlUtils.setInt("exp_factor", expFactor);
    }

    public int getPointFactor() {
        return sqlUtils.getInt("point_factor");
    }

    public void setPointFactor(int pointFactor) {
        sqlUtils.setInt("point_factor", pointFactor);
    }

    public int getKashFactor() {
        return sqlUtils.getInt("kash_factor");
    }

    public void setKashFactor(int kashFactor) {
        sqlUtils.setInt("kash_factor", kashFactor);
    }

    public boolean getPracticeRewards() {
        return sqlUtils.getBoolean("practice_rewards");
    }

    public void setPracticeRewards(boolean practiceRewards) {
        sqlUtils.setBoolean("practice_rewards", practiceRewards);
    }

    public ServerInfo(short id) {
        this.id = id;
        this.sqlUtils = new SqlUtils(id, "servers");
    }
}
