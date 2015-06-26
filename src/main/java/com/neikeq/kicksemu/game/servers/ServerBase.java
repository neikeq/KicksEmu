package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.config.Configuration;

public class ServerBase {

    private short id = Configuration.getShort("game.id");
    private final short filter = Configuration.getShort("game.filter");
    private final String name = Configuration.get("game.name");
    private final String address = Configuration.get("game.address");
    private final short port = (short)(Configuration.getShort("game.tcp.port.factor") + id);
    private final byte minLevel = Configuration.getByte("game.level.min");
    private final byte maxLevel = Configuration.getByte("game.level.max");
    private final short maxUsers = Configuration.getShort("game.users.max");
    private final ServerType type = ServerType.fromString(Configuration.get("game.type"));
    private final int expFactor = Configuration.getInt("game.rewards.exp");
    private final int pointFactor = Configuration.getInt("game.rewards.point");
    private final int cashFactor = Configuration.getInt("game.rewards.cash");
    private final boolean practiceRewards = Configuration.getBoolean("game.rewards.practice");

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getFilter() {
        return filter;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public short getPort() {
        return port;
    }

    public byte getMinLevel() {
        return minLevel;
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public short getMaxUsers() {
        return maxUsers;
    }

    public ServerType getType() {
        return type;
    }

    public int getExpFactor() {
        return expFactor;
    }

    public int getPointFactor() {
        return pointFactor;
    }

    public int getCashFactor() {
        return cashFactor;
    }

    public boolean isPracticeRewards() {
        return practiceRewards;
    }
}
