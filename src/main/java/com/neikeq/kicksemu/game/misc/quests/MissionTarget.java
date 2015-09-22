package com.neikeq.kicksemu.game.misc.quests;

public enum MissionTarget {

    PLAYER,
    TEAM,
    RIVAL_TEAM;

    public static MissionTarget fromString(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
