package com.neikeq.kicksemu.game.rooms.enums;

import com.neikeq.kicksemu.game.servers.GameServerType;

public enum GoalkeeperMode {

    // TODO Add CLUB and TOURNAMENT modes

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

    // TODO Add TOURNAMENT and CLUB server checks
    public boolean isValidForServer(GameServerType serverType) {
        switch (serverType) {
            case PRIVATE:
            case NORMAL:
                return this == AI;
            case PRACTICE:
                return this == TRAINING_ONE || this == TRAINING_TWO ||
                        this == TRAINING_THREE;
            default:
                return false;
        }
    }
}
