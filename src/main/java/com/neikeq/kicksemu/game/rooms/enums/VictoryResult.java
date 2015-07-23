package com.neikeq.kicksemu.game.rooms.enums;

public enum VictoryResult {

    NO_GAME,
    DRAW,
    WIN,
    LOSE;

    public static VictoryResult fromShort(short result) {
        switch (result) {
            case -1:
                return NO_GAME;
            case 0:
                return DRAW;
            case 1:
                return WIN;
            case 2:
                return LOSE;
            default:
                return null;
        }
    }

    public short toShort() {
        switch (this) {
            case DRAW:
                return 0;
            case WIN:
                return 1;
            case LOSE:
                return 2;
            case NO_GAME:
            default:
                return -1;
        }
    }
}
