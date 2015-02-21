package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;

/**
 * Created by Kriss on 19/02/2015.
 */
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

