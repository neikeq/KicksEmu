package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;

/**
 * Created by Kriss on 19/02/2015.
 */
public class LevelExpInfo {

    private final int lvl;
    private final int exp;

    public LevelExpInfo(Row row) {
        row.nextColumn();
        lvl = Integer.valueOf(row.nextColumn());
        exp = Integer.valueOf(row.nextColumn());
    }

    public int getLvl() {
        return lvl;
    }

    public int getExp() {
        return exp;
    }
}

