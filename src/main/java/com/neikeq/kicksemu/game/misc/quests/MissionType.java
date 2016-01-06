package com.neikeq.kicksemu.game.misc.quests;

import com.neikeq.kicksemu.game.rooms.enums.VictoryResult;
import com.neikeq.kicksemu.game.rooms.match.StatisticsCarrier;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.table.MissionInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum MissionType {

    GOALS,
    ASSISTS,
    STEALS,
    TACKLES,
    INTERCEPTIONS,
    GOALS_LIMIT,
    ASSISTS_LIMIT,
    STEALS_LIMIT,
    TACKLES_LIMIT,
    INTERCEPTIONS_LIMIT,
    WIN,
    DRAW,
    LOSE;

    private static final Map<MissionType, MissionChecker> check;
    static {
        Map<MissionType, MissionChecker> map = new HashMap<>();
        map.put(GOALS, (tr, mt, m) -> tr.getGoals() >= m.getValue());
        map.put(ASSISTS, (tr, mt, m) -> tr.getAssists() >= m.getValue());
        map.put(STEALS, (tr, mt, m) -> tr.getSteals() >= m.getValue());
        map.put(TACKLES, (tr, mt, m) -> tr.getTackles() >= m.getValue());
        map.put(INTERCEPTIONS, (tr, mt, m) -> tr.getBlocks() >= m.getValue());
        map.put(GOALS_LIMIT, (tr, mt, m) -> tr.getGoals() <= m.getValue());
        map.put(ASSISTS_LIMIT, (tr, mt, m) -> tr.getAssists() <= m.getValue());
        map.put(STEALS_LIMIT, (tr, mt, m) -> tr.getSteals() <= m.getValue());
        map.put(TACKLES_LIMIT, (tr, mt, m) -> tr.getTackles() <= m.getValue());
        map.put(INTERCEPTIONS_LIMIT, (tr, mt, m) -> tr.getBlocks() <= m.getValue());
        map.put(WIN, (tr, mt, m) -> mt.map(t ->
                (t.getResult() == VictoryResult.WIN) == (m.getValue() == 1))
                .orElse(false));
        map.put(DRAW, (tr, mt, m) -> mt.map(t ->
                (t.getResult() == VictoryResult.DRAW) == (m.getValue() == 1))
                .orElse(false));
        map.put(LOSE, (tr, mt, m) -> mt.map(t ->
                (t.getResult() == VictoryResult.LOSE) == (m.getValue() == 1))
                .orElse(false));
        check = Collections.unmodifiableMap(map);
    }

    public boolean resultAchievesMission(StatisticsCarrier targetResult,
                                         Optional<TeamResult> teamResult, MissionInfo mission) {
        return Optional.ofNullable(check.get(this))
                .map(m -> m.check(targetResult, teamResult, mission))
                .orElse(false);
    }

    public static MissionType fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @FunctionalInterface
    public interface MissionChecker {

        boolean check(StatisticsCarrier target, Optional<TeamResult> team, MissionInfo mission);
    }
}
