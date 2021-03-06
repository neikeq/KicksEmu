package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.utils.table.Row;

public class LevelInfo {

    private final short level;
    private final int experience;
    private final int experienceGap;

    public LevelInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        level = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        experience = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        experienceGap = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
    }

    public short getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceGap() {
        return experienceGap;
    }
}

