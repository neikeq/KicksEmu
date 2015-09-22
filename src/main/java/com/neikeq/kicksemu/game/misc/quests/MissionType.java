package com.neikeq.kicksemu.game.misc.quests;

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

    public static MissionType fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
