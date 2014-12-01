package com.neikeq.kicksemu.game.rooms;

public enum RoomSize {
    SIZE_3V3,
    SIZE_4V4,
    SIZE_5V5;

    public static RoomSize fromInt(int size) {
        switch (size) {
            case 6:
                return SIZE_3V3;
            case 8:
                return SIZE_4V4;
            case 10:
                return SIZE_5V5;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case SIZE_3V3:
                return 6;
            case SIZE_4V4:
                return 8;
            case SIZE_5V5:
                return 10;
            default:
                return -1;
        }
    }
}
