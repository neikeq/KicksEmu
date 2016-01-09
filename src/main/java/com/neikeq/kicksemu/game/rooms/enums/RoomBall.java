package com.neikeq.kicksemu.game.rooms.enums;

import java.util.Optional;

public enum RoomBall {

    STAR,
    TANGO,
    QUEST,
    TRY,
    FEVER_HEAT,
    TEAM_ARENA,
    T_10Y,
    T_10W,
    JOHNS_EYE,
    BEACH_BALL;

    public static Optional<RoomBall> fromInt(int ball) {
        return Optional.ofNullable(unsafeFromInt(ball));
    }

    public static RoomBall unsafeFromInt(int ball) {
        switch (ball) {
            case 1:
                return STAR;
            case 2:
                return TANGO;
            case 3:
                return QUEST;
            case 4:
                return TRY;
            case 5:
                return FEVER_HEAT;
            case 6:
                return TEAM_ARENA;
            case 7:
                return T_10Y;
            case 8:
                return T_10W;
            case 9:
                return JOHNS_EYE;
            case 10:
                return BEACH_BALL;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case STAR:
                return 1;
            case TANGO:
                return 2;
            case QUEST:
                return 3;
            case TRY:
                return 4;
            case FEVER_HEAT:
                return 5;
            case TEAM_ARENA:
                return 6;
            case T_10Y:
                return 7;
            case T_10W:
                return 8;
            case JOHNS_EYE:
                return 9;
            case BEACH_BALL:
                return 10;
            default:
                return -1;
        }
    }
}
