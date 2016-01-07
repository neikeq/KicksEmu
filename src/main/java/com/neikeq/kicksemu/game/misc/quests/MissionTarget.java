package com.neikeq.kicksemu.game.misc.quests;

public enum MissionTarget {

    NOBODY,
    PLAYER,
    TEAM,
    RIVAL_TEAM;

    public static MissionTarget fromString(String str) {
        return str.isEmpty() ? NOBODY : valueOf(str.toUpperCase());
    }
}
