package com.neikeq.kicksemu.game.rooms;

public enum RoomType {
    FREE,
    PASSWORD;

    public static RoomType fromInt(int type) {
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
