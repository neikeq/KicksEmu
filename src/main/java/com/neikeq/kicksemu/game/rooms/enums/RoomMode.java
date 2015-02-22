package com.neikeq.kicksemu.game.rooms.enums;

import com.neikeq.kicksemu.game.servers.GameServerType;

public enum RoomMode {

    // TODO Add CLUB and TOURNAMENT modes

    AI_GOALKEEPER,
    PLAYER_GOALKEEPER,
    TRAINING_ONE,
    TRAINING_TWO,
    TRAINING_THREE;

    public static RoomMode fromInt(int mode) {
        switch (mode) {
            case 0:
                return AI_GOALKEEPER;
            case 1:
                return PLAYER_GOALKEEPER;
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
            case AI_GOALKEEPER:
                return 0;
            case PLAYER_GOALKEEPER:
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
                return this == AI_GOALKEEPER;
            case PRACTICE:
                return this == TRAINING_ONE || this == TRAINING_TWO ||
                        this == TRAINING_THREE;
            default:
                return false;
        }
    }
}
