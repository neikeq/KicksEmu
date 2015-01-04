package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Column;

public class SkillInfo {

    private String name;
    private int id;
    private short position;
    private short group;
    private short level;
    private SkillPrice skillPrice;

    public SkillInfo(Column column) {
        name = column.nextRow();
        id = Integer.valueOf(column.nextRow());
        position = Short.valueOf(column.nextRow());
        group = Short.valueOf(column.nextRow());
        level = Short.valueOf(column.nextRow());
        skillPrice = new SkillPrice(column);
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
