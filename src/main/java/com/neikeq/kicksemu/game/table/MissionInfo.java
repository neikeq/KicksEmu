package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.misc.quests.MissionTarget;
import com.neikeq.kicksemu.game.misc.quests.MissionType;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.SeasonRange;
import com.neikeq.kicksemu.utils.table.Row;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MissionInfo {

    private final short id;
    private final int reward;
    private final SeasonRange season;
    private final MissionTarget target;
    private final MissionType type;
    private final int value;

    private static SeasonRange columnToSeasonRange(Row row) throws ParseRowException {
        String column = row.nextColumn().orElseThrow(ParseRowException::new);

        if (column.isEmpty()) return null;

        try {
            String[] seasons = column.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
            return new SeasonRange(formatter.parse(seasons[0]), formatter.parse(seasons[1]));
        } catch (IndexOutOfBoundsException | ParseException ignored) {
            Output.println("Invalid season range in mission table. Table index: " +
                    row.columnAt(0).orElse("-1 (Empty)"), Level.WARNING);
            return null;
        }
    }

    public MissionInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        row.ignoreColumn();
        reward = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        season = columnToSeasonRange(row);
        row.ignoreColumn();
        try {
            target = MissionTarget.fromString(row.nextColumn().orElseThrow(ParseRowException::new));
            type = MissionType.fromString(row.nextColumn().orElseThrow(ParseRowException::new));
            String valueStr = row.nextColumn().orElseThrow(ParseRowException::new);
            value = valueStr.isEmpty() ? 0 : Integer.valueOf(valueStr);
        } catch (IllegalArgumentException e) {
            throw new ParseRowException("Message: '" + e.getMessage() + "'");
        }
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

    public MissionTarget getTarget() {
        return target;
    }

    public MissionType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
