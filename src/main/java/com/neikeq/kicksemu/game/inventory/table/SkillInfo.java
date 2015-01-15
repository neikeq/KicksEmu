package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;

public class SkillInfo {

    private String name;
    private int id;
    private short position;
    private short group;
    private short level;
    private SkillPrice skillPrice;

    public SkillInfo(Row row) {
        name = row.nextRow();
        id = Integer.valueOf(row.nextRow());
        position = Short.valueOf(row.nextRow());
        group = Short.valueOf(row.nextRow());
        level = Short.valueOf(row.nextRow());
        skillPrice = new SkillPrice(row);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public short getPosition() {
        return position;
    }

    public short getGroup() {
        return group;
    }

    public short getLevel() {
        return level;
    }

    public SkillPrice getSkillPrice() {
        return skillPrice;
    }
}
