package com.neikeq.kicksemu.game.rooms.enums;

public enum RoomTeam {

    RED,
    BLUE;

    public static RoomTeam fromInt(int team) {
        switch (team) {
            case 0:
                return RED;
            case 1:
                return BLUE;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case RED:
                return 0;
            case BLUE:
                return 1;
            default:
                return -1;
        }
    }
}
