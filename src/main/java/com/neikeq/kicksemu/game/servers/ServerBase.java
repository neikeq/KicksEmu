package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.config.Configuration;

public class ServerBase {

    private short id;
    private short filter;
    private String name;
    private String address;
    private short port;
    private byte minLevel;
    private byte maxLevel;
    private short maxUsers;
    private GameServerType type;
    private int expFactor;
    private int pointFactor;
    private int kashFactor;
    private boolean practiceRewards;

    public static ServerBase fromConfig() {
        ServerBase base = new ServerBase();

        base.setId(Configuration.getShort("game.id"));
        base.setFilter(Configuration.getShort("game.filter"));
        base.setName(Configuration.get("game.name"));
        base.setAddress(Configuration.get("game.address"));
        base.setPort((short)(Configuration.getShort("game.tcp.port.factor") + base.getId()));
        base.setMinLevel(Configuration.getByte("game.level.min"));
        base.setMaxLevel(Configuration.getByte("game.level.max"));
        base.setMaxUsers(Configuration.getShort("game.users.max"));
        base.setType(GameServerType.valueOf(Configuration.get("game.type")));
        base.setExpFactor(Configuration.getInt("game.rewards.exp"));
        base.setPointFactor(Configuration.getInt("game.rewards.point"));
        base.setKashFactor(Configuration.getInt("game.rewards.kash"));
        base.setPracticeRewards(Configuration.getBoolean("game.rewards.practice"));

        return base;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getFilter() {
        return filter;
    }

    public void setFilter(short filter) {
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    public byte getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(byte minLevel) {
        this.minLevel = minLevel;
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(byte maxLevel) {
        this.maxLevel = maxLevel;
    }

    public short getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(short maxUsers) {
        this.maxUsers = maxUsers;
    }

    public GameServerType getType() {
        return type;
    }

    public void setType(GameServerType type) {
        this.type = type;
    }

    public int getExpFactor() {
        return expFactor;
    }

    public void setExpFactor(int expFactor) {
        this.expFactor = expFactor;
    }

    public int getPointFactor() {
        return pointFactor;
    }

    public void setPointFactor(int pointFactor) {
        this.pointFactor = pointFactor;
    }

    public int getKashFactor() {
        return kashFactor;
    }

    public void setKashFactor(int kashFactor) {
        this.kashFactor = kashFactor;
    }

    public boolean isPracticeRewards() {
        return practiceRewards;
    }

    public void setPracticeRewards(boolean practiceRewards) {
        this.practiceRewards = practiceRewards;
    }
}
