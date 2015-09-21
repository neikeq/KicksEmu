package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.utils.table.Row;

public class MissionInfo {

    private final short id;
    private final int reward;

    public MissionInfo(Row row) {
        row.nextColumn();
        id = Short.valueOf(row.nextColumn());
        row.nextColumn();
        reward = Integer.valueOf(row.nextColumn());
    }

    public short getId() {
        return id;
    }

    public int getReward() {
        return reward;
    }
}
