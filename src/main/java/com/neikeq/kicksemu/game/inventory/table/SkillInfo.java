package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;

public class SkillInfo {

    private int id;
    private short position;
    private short level;
    private SkillPrice skillPrice;

    public SkillInfo(Row row) {
        row.nextColumn();
        id = Integer.valueOf(row.nextColumn());
        position = Short.valueOf(row.nextColumn());
        row.nextColumn();
        level = Short.valueOf(row.nextColumn());
        row.nextColumn();
        skillPrice = new SkillPrice(row);
    }

    public int getId() {
        return id;
    }

    public short getPosition() {
        return position;
    }

    public short getLevel() {
        return level;
    }

    public SkillPrice getSkillPrice() {
        return skillPrice;
    }
}
