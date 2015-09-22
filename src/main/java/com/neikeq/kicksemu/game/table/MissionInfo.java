package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.SeasonRange;
import com.neikeq.kicksemu.utils.table.Row;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MissionInfo {

    private final short id;
    private final int reward;
    private final SeasonRange season;

    private static SeasonRange columnToSeasonRange(Row row) {
        String column = row.nextColumn();

        if (column.isEmpty()) return null;

        try {
            String[] seasons = column.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
            return new SeasonRange(formatter.parse(seasons[0]), formatter.parse(seasons[1]));
        } catch (IndexOutOfBoundsException | ParseException ignored) {
            Output.println("Invalid season range in mission table. Index: " +
                    row.columnAt(0), Level.WARNING);
            return null;
        }
    }

    public MissionInfo(Row row) {
        row.nextColumn();
        id = Short.valueOf(row.nextColumn());
        row.nextColumn();
        reward = Integer.valueOf(row.nextColumn());
        season = columnToSeasonRange(row);

    }

    public short getId() {
        return id;
    }

    public int getReward() {
        return reward;
    }

    public SeasonRange getSeason() {
        return season;
    }
}
