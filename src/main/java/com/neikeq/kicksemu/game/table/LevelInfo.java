package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.utils.table.Row;

public class LevelInfo {

    private final short level;
    private final int experience;

    public LevelInfo(Row row) {
        row.nextColumn();
        level = Short.valueOf(row.nextColumn());
        experience = Integer.valueOf(row.nextColumn());
    }

    public short getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }
}

