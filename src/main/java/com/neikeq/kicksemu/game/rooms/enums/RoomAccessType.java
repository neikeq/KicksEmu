package com.neikeq.kicksemu.game.rooms.enums;

import java.util.Optional;

public enum RoomAccessType {

    FREE,
    PASSWORD;

    public static Optional<RoomAccessType> fromShort(short type) {
        return Optional.ofNullable(unsafeFromShort(type));
    }

    private static RoomAccessType unsafeFromShort(short type) {
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
