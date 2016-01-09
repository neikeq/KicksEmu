package com.neikeq.kicksemu.game.rooms.enums;

import java.util.Optional;

public enum RoomSize {

    SIZE_2V2,
    SIZE_3V3,
    SIZE_4V4,
    SIZE_5V5;

    public static Optional<RoomSize> fromInt(int size) {
        return Optional.ofNullable(unsafeFromInt(size));
    }

    public static RoomSize unsafeFromInt(int size) {
        switch (size) {
            case 4:
                return SIZE_2V2;
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
            case SIZE_2V2:
                return 4;
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
