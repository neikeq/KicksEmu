package com.neikeq.kicksemu.game.rooms.enums;

public enum GoalkeeperMode {
    AI,
    PLAYER,
    TRAINING_ONE,
    TRAINING_TWO,
    TRAINING_THREE;

    public static GoalkeeperMode fromInt(int mode) {
        switch (mode) {
            case 0:
                return AI;
            case 1:
                return PLAYER;
            case 2:
                return TRAINING_ONE;
            case 3:
                return TRAINING_TWO;
            case 4:
                return TRAINING_THREE;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case AI:
                return 0;
            case PLAYER:
                return 1;
            case TRAINING_ONE:
                return 2;
            case TRAINING_TWO:
                return 3;
            case TRAINING_THREE:
                return 4;
            default:
                return -1;
        }
    }
}
