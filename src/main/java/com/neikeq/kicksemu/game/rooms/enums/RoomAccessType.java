package com.neikeq.kicksemu.game.rooms.enums;

public enum RoomAccessType {

    FREE,
    PASSWORD;

    public static RoomAccessType fromShort(short type) {
        switch (type) {
            case 1:
                return FREE;
            case 2:
                return PASSWORD;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case FREE:
                return 1;
            case PASSWORD:
                return 2;
            default:
                return -1;
        }
    }
}
