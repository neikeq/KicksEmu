package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.utils.table.Row;

public class BonusInfo {

    private final int type;
    private final int bonusOne;
    private final int bonusTwo;

    public BonusInfo(Row row) {
        type = Integer.valueOf(row.nextColumn());
        bonusOne = Integer.valueOf(row.nextColumn());
        bonusTwo = Integer.valueOf(row.nextColumn());
    }

    public int getType() {
        return type;
    }

    public int getBonusOne() {
        return bonusOne;
    }

    public int getBonusTwo() {
        return bonusTwo;
    }
}
