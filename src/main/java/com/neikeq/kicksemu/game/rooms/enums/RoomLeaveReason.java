package com.neikeq.kicksemu.game.rooms.enums;

public enum RoomLeaveReason {

    LEAVED,
    DISCONNECTED,
    KICKED;

    public short toShort() {
        switch (this) {
            case LEAVED:
                return 1;
            case DISCONNECTED:
                return 2;
            case KICKED:
                return 3;
            default:
                return -1;
        }
    }
}
